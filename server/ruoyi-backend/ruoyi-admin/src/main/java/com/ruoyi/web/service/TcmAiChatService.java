package com.ruoyi.web.service;

import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.web.domain.ai.TcmChatHistory;
import com.ruoyi.web.domain.ai.TcmChatRequest;
import com.ruoyi.web.domain.ai.TcmChatResponse;
import com.ruoyi.web.domain.ai.TcmKnowledgeChunk;

@Service
public class TcmAiChatService
{
    private static final Logger log = LoggerFactory.getLogger(TcmAiChatService.class);

    private static final int MAX_HISTORY_MESSAGES = 6;

    private static final int MAX_HISTORY_CONTENT_LENGTH = 800;

    private static final String SYSTEM_PROMPT = "你是“中医临床辅助决策助手”，服务对象是具有执业资质的医生，不直接面向患者。"
            + "请优先依据本地知识库检索结果，输出简明、实用、可复核的临床辅助意见。\n\n"
            + "规则：\n"
            + "1. 只能提供辅助参考，不能替代医生诊断。\n"
            + "2. 不得输出“确诊”“必须服用”“保证有效”等绝对化结论。\n"
            + "3. 如知识库依据不足，必须明确写“当前知识库未检索到充分依据”，不得编造出处、组成、剂量、禁忌。\n"
            + "4. 涉及急症、重症、孕产妇、儿童、老人、肝肾功能异常、抗凝药、免疫抑制剂、精神类药物时，必须提示重点复核风险。\n"
            + "5. 不得给出超出知识库依据的精确剂量，不得推荐偏方、秘方、违法或危险治疗。\n\n"
            + "输出尽量控制在 6 个小节内，避免空话和重复。若信息明显不足，不要长篇展开，直接指出缺失信息和下一步建议。\n\n"
            + "建议格式：\n"
            + "【病例摘要】\n"
            + "【辨证思路】\n"
            + "【可能证型】\n"
            + "【治法与方药参考】\n"
            + "【安全风险】\n"
            + "【建议追问与提醒】";

    private final TcmRagService ragService;

    private final RestTemplate restTemplate = createRestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Pattern API_KEY_PATTERN = Pattern.compile("(?m)^\\s*api-key\\s*:\\s*(.*?)\\s*$");

    private static final Pattern BASE_URL_PATTERN = Pattern.compile("(?m)^\\s*base-url\\s*:\\s*(.*?)\\s*$");

    private static final Pattern MODEL_PATTERN = Pattern.compile("(?m)^\\s*model\\s*:\\s*(.*?)\\s*$");

    private volatile String localApiKey = "";

    private volatile String localBaseUrl = "";

    private volatile String localModel = "";

    @Value("${deepseek.api-key:${DEEPSEEK_API_KEY:}}")
    private String apiKey;

    @Value("${deepseek.base-url:${DEEPSEEK_BASE_URL:https://api.deepseek.com}}")
    private String baseUrl;

    @Value("${deepseek.model:${DEEPSEEK_MODEL:deepseek-v4-pro}}")
    private String model;

    public TcmAiChatService(TcmRagService ragService)
    {
        this.ragService = ragService;
        refreshLocalConfig();
    }

    public TcmChatResponse chat(TcmChatRequest request)
    {
        String message = request == null ? null : StringUtils.trim(request.getMessage());
        if (StringUtils.isEmpty(message))
        {
            throw new ServiceException("请输入需要分析的问题");
        }

        List<TcmKnowledgeChunk> matchedChunks = ragService.search(message, request.getTopK());
        String context = ragService.buildContext(matchedChunks);
        String answer = callDeepSeek(request, message, context);

        TcmChatResponse response = new TcmChatResponse();
        response.setAnswer(answer);
        response.setMatchedChunks(matchedChunks);
        response.setReferences(ragService.toReferences(matchedChunks));
        return response;
    }

    private String callDeepSeek(TcmChatRequest request, String message, String context)
    {
        refreshLocalConfig();
        if (StringUtils.isEmpty(resolveApiKey()))
        {
            throw new ServiceException("DeepSeek API Key 未配置，请在后端私有配置文件或环境变量中设置");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", resolveModel());
        payload.put("messages", toChatMessages(request, message, context));
        payload.put("max_tokens", 1600);
        payload.put("temperature", 0.15D);
        log.info("DeepSeek admin request -> url={}, model={}, keyPrefix={}, messageCount={}",
                chatCompletionsApiUrl(), resolveModel(), maskKeyPrefix(), ((List<?>) payload.get("messages")).size());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resolveApiKey().trim());

        try
        {
            ResponseEntity<String> response = restTemplate.exchange(
                    chatCompletionsApiUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(payload, headers),
                    String.class);
            return extractAnswer(response.getBody());
        }
        catch (RestClientResponseException e)
        {
            log.warn("DeepSeek request failed, status={}, body={}", e.getRawStatusCode(),
                    sanitizeResponseBody(e.getResponseBodyAsString()));
            throw new ServiceException("DeepSeek 服务调用失败，请检查 API Key、请求地址、模型名称或后端网络");
        }
        catch (RestClientException e)
        {
            log.warn("DeepSeek request failed: {}", safeMessage(e));
            throw new ServiceException("DeepSeek 服务调用失败，请检查后端网络或稍后重试");
        }
        catch (Exception e)
        {
            log.warn("DeepSeek response parse failed: {}", safeMessage(e));
            throw new ServiceException("DeepSeek 返回解析失败，请检查接口返回格式");
        }
    }

    private List<Map<String, Object>> toChatMessages(TcmChatRequest request, String message, String context)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(message("system", SYSTEM_PROMPT));
        result.addAll(toChatHistory(request == null ? null : request.getHistory()));
        result.add(message("user", buildUserPrompt(request, message, context)));
        return result;
    }

    private List<Map<String, Object>> toChatHistory(List<TcmChatHistory> history)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        if (history == null || history.isEmpty())
        {
            return result;
        }
        int start = Math.max(0, history.size() - MAX_HISTORY_MESSAGES);
        for (int i = start; i < history.size(); i++)
        {
            TcmChatHistory item = history.get(i);
            if (item == null || StringUtils.isEmpty(item.getContent()))
            {
                continue;
            }
            String role = "assistant".equals(item.getRole()) ? "assistant" : "user";
            result.add(message(role, limitLength(item.getContent(), MAX_HISTORY_CONTENT_LENGTH)));
        }
        return result;
    }

    private String buildUserPrompt(TcmChatRequest request, String message, String context)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("【医生输入】\n").append(message).append("\n\n");
        builder.append("【患者信息】\n").append(formatPatientInfo(request == null ? null : request.getPatientInfo())).append("\n\n");
        builder.append("【知识库检索结果】\n").append(context).append("\n\n");
        builder.append("要求：回答尽量简洁、实用，不要重复“未提供”或大段复述检索原文；");
        builder.append("不要展示知识库文件名、相关度、片段编号等实现细节；");
        builder.append("如果仅能给出有限参考，就明确写出有限依据和建议追问。");
        return builder.toString();
    }

    private String formatPatientInfo(Map<String, Object> patientInfo)
    {
        if (patientInfo == null || patientInfo.isEmpty())
        {
            return "未提供";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : patientInfo.entrySet())
        {
            Object value = entry.getValue();
            if (value == null || StringUtils.isEmpty(String.valueOf(value)))
            {
                continue;
            }
            builder.append(entry.getKey()).append("：").append(value).append("\n");
        }
        return builder.length() == 0 ? "未提供" : builder.toString().trim();
    }

    private String extractAnswer(String body) throws Exception
    {
        JsonNode root = objectMapper.readTree(body);
        JsonNode choices = root.path("choices");
        if (choices.isArray())
        {
            for (JsonNode item : choices)
            {
                String text = item.path("message").path("content").asText();
                if (StringUtils.isNotEmpty(text))
                {
                    return text;
                }
            }
        }
        String fallback = root.path("choices").path(0).path("text").asText();
        if (StringUtils.isNotEmpty(fallback))
        {
            return fallback;
        }
        throw new IllegalStateException("empty content");
    }

    private Map<String, Object> message(String role, String content)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("role", role);
        item.put("content", content);
        return item;
    }

    private String resolveModel()
    {
        if (StringUtils.isNotEmpty(localModel))
        {
            return localModel;
        }
        return StringUtils.isEmpty(model) ? "deepseek-v4-pro" : model.trim();
    }

    private String chatCompletionsApiUrl()
    {
        String normalized = StringUtils.isNotEmpty(localBaseUrl) ? localBaseUrl
                : (StringUtils.isEmpty(baseUrl) ? "https://api.deepseek.com" : baseUrl.trim());
        while (normalized.endsWith("/"))
        {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if (normalized.endsWith("/chat/completions"))
        {
            return normalized;
        }
        if (normalized.endsWith("/v1"))
        {
            return normalized + "/chat/completions";
        }
        return normalized + "/chat/completions";
    }

    private String limitLength(String value, int maxLength)
    {
        if (value == null || value.length() <= maxLength)
        {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String safeMessage(Exception e)
    {
        String message = e.getMessage();
        if (message == null)
        {
            return e.getClass().getSimpleName();
        }
        if (StringUtils.isNotEmpty(resolveApiKey()))
        {
            message = message.replace(resolveApiKey(), "***");
        }
        return message;
    }

    private String sanitizeResponseBody(String body)
    {
        if (body == null)
        {
            return "";
        }
        if (StringUtils.isNotEmpty(resolveApiKey()))
        {
            return body.replace(resolveApiKey(), "***");
        }
        return body;
    }

    private String maskKeyPrefix()
    {
        String effectiveApiKey = resolveApiKey();
        if (StringUtils.isEmpty(effectiveApiKey))
        {
            return "empty";
        }
        String trimmed = effectiveApiKey.trim();
        return trimmed.length() <= 6 ? trimmed : trimmed.substring(0, 6) + "***";
    }

    private String resolveApiKey()
    {
        if (StringUtils.isNotEmpty(localApiKey))
        {
            return localApiKey;
        }
        return apiKey;
    }

    private void refreshLocalConfig()
    {
        localApiKey = "";
        localBaseUrl = "";
        localModel = "";
        for (Path candidate : localConfigCandidates())
        {
            if (!Files.isRegularFile(candidate))
            {
                continue;
            }
            try
            {
                String content = Files.readString(candidate, StandardCharsets.UTF_8);
                localApiKey = cleanLocalValue(findLocalValue(content, API_KEY_PATTERN));
                localBaseUrl = cleanLocalValue(findLocalValue(content, BASE_URL_PATTERN));
                localModel = cleanLocalValue(findLocalValue(content, MODEL_PATTERN));
                return;
            }
            catch (Exception ignored)
            {
                // Fall back to Spring properties.
            }
        }
    }

    private List<Path> localConfigCandidates()
    {
        Path current = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        List<Path> candidates = new ArrayList<>();
        candidates.add(current.resolve("config").resolve("application-secret.yml").normalize());
        candidates.add(current.resolve("..").resolve("config").resolve("application-secret.yml").normalize());
        return candidates;
    }

    private String findLocalValue(String content, Pattern pattern)
    {
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find())
        {
            return "";
        }
        return matcher.group(1);
    }

    private String cleanLocalValue(String value)
    {
        String trimmed = StringUtils.trim(value);
        if (StringUtils.isEmpty(trimmed))
        {
            return "";
        }
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\""))
                || (trimmed.startsWith("'") && trimmed.endsWith("'")))
        {
            return trimmed.length() >= 2 ? trimmed.substring(1, trimmed.length() - 1).trim() : "";
        }
        return trimmed;
    }

    private RestTemplate createRestTemplate()
    {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setProxy(Proxy.NO_PROXY);
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(60000);
        return new RestTemplate(requestFactory);
    }
}
