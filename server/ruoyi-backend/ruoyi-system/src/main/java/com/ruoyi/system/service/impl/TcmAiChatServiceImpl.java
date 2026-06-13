package com.ruoyi.system.service.impl;

import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import com.ruoyi.system.constant.TcmAiPromptConstants;
import com.ruoyi.system.domain.ai.TcmAiChatMessage;
import com.ruoyi.system.domain.ai.TcmAiChatRequest;
import com.ruoyi.system.domain.ai.TcmAiChatResponse;
import com.ruoyi.system.service.ITcmAiChatService;

@Service
public class TcmAiChatServiceImpl implements ITcmAiChatService
{
    private static final Logger log = LoggerFactory.getLogger(TcmAiChatServiceImpl.class);

    private static final int MAX_INPUT_LENGTH = 1000;

    private static final int MAX_HISTORY_COUNT = 10;

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

    public TcmAiChatServiceImpl()
    {
        refreshLocalConfig();
    }

    @Override
    public TcmAiChatResponse chat(Long userId, TcmAiChatRequest request)
    {
        if (userId == null)
        {
            throw new ServiceException("请先登录后再使用智能问答");
        }
        if (request == null)
        {
            throw new ServiceException("请求参数不能为空");
        }
        refreshLocalConfig();
        if (StringUtils.isEmpty(resolveApiKey()))
        {
            throw new ServiceException("DeepSeek API Key 未配置，请在后端私有配置文件或环境变量中设置");
        }

        String message = normalizeMessage(request.getMessage());
        List<TcmAiChatMessage> history = sanitizeHistory(request.getHistory());
        String answer = normalizeAnswer(callDeepSeek(history, message));

        TcmAiChatResponse response = new TcmAiChatResponse();
        response.setSessionId(normalizeSessionId(request.getSessionId()));
        response.setAnswer(answer);
        response.setSuggestions(Collections.emptyList());
        response.setModel(resolveModel());
        return response;
    }

    private String callDeepSeek(List<TcmAiChatMessage> history, String userMessage)
    {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", resolveModel());
        payload.put("messages", toChatMessages(history, userMessage));
        payload.put("max_tokens", 1600);
        payload.put("temperature", 0.2D);
        log.info("DeepSeek app request -> url={}, model={}, keyPrefix={}, messageCount={}",
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
            log.warn("App DeepSeek request failed, status={}, body={}", e.getRawStatusCode(),
                    sanitizeResponseBody(e.getResponseBodyAsString()));
            throw new ServiceException("AI 服务调用失败，请检查 API Key、请求地址、模型名称或后端网络");
        }
        catch (RestClientException e)
        {
            log.warn("App DeepSeek request failed: {}", safeMessage(e));
            throw new ServiceException("AI 服务调用失败，请检查后端网络或稍后重试");
        }
        catch (Exception e)
        {
            log.warn("App DeepSeek response parse failed: {}", safeMessage(e));
            throw new ServiceException("AI 返回解析失败，请检查接口返回格式");
        }
    }

    private List<Map<String, Object>> toChatMessages(List<TcmAiChatMessage> history, String userMessage)
    {
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(message("system", TcmAiPromptConstants.TCM_HEALTH_CONSULTATION_SYSTEM_PROMPT));
        if (history != null)
        {
            for (TcmAiChatMessage item : history)
            {
                if (item == null || StringUtils.isEmpty(item.getContent()))
                {
                    continue;
                }
                String role = "assistant".equals(item.getRole()) ? "assistant" : "user";
                messages.add(message(role, item.getContent()));
            }
        }
        messages.add(message("user", userMessage));
        return messages;
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

    private String resolveModel()
    {
        if (StringUtils.isNotEmpty(localModel))
        {
            return localModel;
        }
        return StringUtils.isEmpty(model) ? "deepseek-v4-pro" : model.trim();
    }

    private String normalizeMessage(String message)
    {
        String normalized = StringUtils.trim(message);
        if (StringUtils.isEmpty(normalized))
        {
            throw new ServiceException("请输入想咨询的问题");
        }
        if (normalized.length() > MAX_INPUT_LENGTH)
        {
            throw new ServiceException("单次输入请控制在 1000 字以内");
        }
        return normalized;
    }

    private List<TcmAiChatMessage> sanitizeHistory(List<TcmAiChatMessage> history)
    {
        if (history == null || history.isEmpty())
        {
            return Collections.emptyList();
        }

        List<TcmAiChatMessage> sanitized = new ArrayList<>();
        int start = Math.max(0, history.size() - MAX_HISTORY_COUNT);
        for (int i = start; i < history.size(); i++)
        {
            TcmAiChatMessage item = history.get(i);
            if (item == null)
            {
                continue;
            }
            String content = StringUtils.trim(item.getContent());
            if (StringUtils.isEmpty(content))
            {
                continue;
            }
            TcmAiChatMessage copy = new TcmAiChatMessage();
            copy.setRole("assistant".equals(item.getRole()) ? "assistant" : "user");
            copy.setContent(content.length() > MAX_INPUT_LENGTH ? content.substring(0, MAX_INPUT_LENGTH) : content);
            sanitized.add(copy);
        }
        return sanitized;
    }

    private String normalizeSessionId(String sessionId)
    {
        String normalized = StringUtils.trim(sessionId);
        if (StringUtils.isNotEmpty(normalized))
        {
            return normalized;
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String normalizeAnswer(String answer)
    {
        String normalized = StringUtils.trim(answer);
        if (StringUtils.isEmpty(normalized))
        {
            throw new ServiceException("AI 服务返回内容为空，请检查模型接口配置");
        }
        return normalized;
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
