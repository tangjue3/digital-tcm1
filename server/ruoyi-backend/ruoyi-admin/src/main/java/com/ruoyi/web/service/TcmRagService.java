package com.ruoyi.web.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.web.domain.ai.TcmKnowledgeChunk;

@Service
public class TcmRagService implements InitializingBean
{
    private static final Logger log = LoggerFactory.getLogger(TcmRagService.class);

    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(
            Arrays.asList("json", "txt", "md", "csv", "sql"));

    private static final Pattern ASCII_WORD = Pattern.compile("[A-Za-z0-9_]{2,}");

    private static final int CHUNK_SIZE = 800;

    private static final int CHUNK_OVERLAP = 150;

    private static final long MAX_FILE_SIZE = 25L * 1024L * 1024L;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<IndexedChunk> chunks = new ArrayList<>();

    private final Map<String, Integer> documentFrequency = new HashMap<>();

    private final List<String> indexedFiles = new ArrayList<>();

    private double averageChunkTerms = 1.0D;

    @Value("${tcm.rag.top-k:${TCM_RAG_TOP_K:5}}")
    private int defaultTopK;

    @Value("${tcm.rag.knowledge-paths:${TCM_RAG_KNOWLEDGE_PATHS:}}")
    private String configuredKnowledgePaths;

    @Override
    public void afterPropertiesSet()
    {
        reloadIndex();
    }

    public synchronized void reloadIndex()
    {
        chunks.clear();
        documentFrequency.clear();
        indexedFiles.clear();

        List<Path> files = discoverKnowledgeFiles();
        int chunkId = 1;
        for (Path file : files)
        {
            try
            {
                String text = readKnowledgeFile(file);
                if (StringUtils.isEmpty(text))
                {
                    continue;
                }
                String normalized = normalizeText(text);
                if (StringUtils.isEmpty(normalized))
                {
                    continue;
                }
                String title = inferTitle(file, normalized);
                for (String content : splitChunks(normalized))
                {
                    IndexedChunk chunk = new IndexedChunk();
                    chunk.setId("kb-" + chunkId++);
                    chunk.setFilename(file.getFileName().toString());
                    chunk.setSource(toDisplayPath(file));
                    chunk.setTitle(title);
                    chunk.setContent(content);
                    chunk.termFrequency = termFrequency(tokenize(content));
                    chunk.termLength = Math.max(1, chunk.termFrequency.values().stream().mapToInt(Integer::intValue).sum());
                    chunks.add(chunk);
                }
                indexedFiles.add(toDisplayPath(file));
            }
            catch (Exception e)
            {
                log.warn("TCM RAG knowledge file skipped: {}", file, e);
            }
        }

        buildDocumentFrequency();
        averageChunkTerms = chunks.isEmpty() ? 1.0D :
                chunks.stream().mapToInt(chunk -> chunk.termLength).average().orElse(1.0D);
        log.info("TCM RAG index ready, files={}, chunks={}", indexedFiles.size(), chunks.size());
    }

    public List<TcmKnowledgeChunk> search(String query, Integer topK)
    {
        if (chunks.isEmpty())
        {
            throw new ServiceException("本地中医药知识库为空或索引失败，请检查知识库文件路径");
        }

        List<String> queryTokens = tokenize(query);
        if (queryTokens.isEmpty())
        {
            return new ArrayList<>();
        }

        Map<String, Integer> queryTermFrequency = termFrequency(queryTokens);
        int limit = normalizeTopK(topK);
        List<TcmKnowledgeChunk> matched = new ArrayList<>();
        Set<String> seenContents = new HashSet<>();
        for (IndexedChunk chunk : chunks)
        {
            double score = bm25(chunk, queryTermFrequency.keySet());
            score = adjustScore(chunk, score, query);
            if (score <= 0)
            {
                continue;
            }
            TcmKnowledgeChunk publicChunk = copyChunk(chunk, false);
            publicChunk.setScore(roundScore(score));
            String dedupeKey = normalizeForDedupe(publicChunk.getContent());
            if (seenContents.add(dedupeKey))
            {
                matched.add(publicChunk);
            }
        }

        matched.sort(Comparator.comparing(TcmKnowledgeChunk::getScore).reversed());
        if (matched.size() > limit)
        {
            return new ArrayList<>(matched.subList(0, limit));
        }
        return matched;
    }

    public List<String> getIndexedFiles()
    {
        return new ArrayList<>(indexedFiles);
    }

    public int getChunkCount()
    {
        return chunks.size();
    }

    public String buildContext(List<TcmKnowledgeChunk> matchedChunks)
    {
        if (matchedChunks == null || matchedChunks.isEmpty())
        {
            return "当前知识库未检索到充分依据。";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < matchedChunks.size(); i++)
        {
            TcmKnowledgeChunk chunk = matchedChunks.get(i);
            builder.append("【参考要点").append(i + 1).append("】\n");
            String title = defaultString(chunk.getTitle());
            if (StringUtils.isNotEmpty(title))
            {
                builder.append("主题：").append(title).append("\n");
            }
            builder.append(summarizeChunkContent(chunk.getContent())).append("\n\n");
        }
        return builder.toString();
    }

    public List<TcmKnowledgeChunk> toReferences(List<TcmKnowledgeChunk> matchedChunks)
    {
        if (matchedChunks == null)
        {
            return new ArrayList<>();
        }
        return matchedChunks.stream()
                .map(chunk -> copyChunk(chunk, true))
                .collect(Collectors.toList());
    }

    private int normalizeTopK(Integer topK)
    {
        int value = topK == null || topK <= 0 ? defaultTopK : topK;
        return Math.max(1, Math.min(value, 10));
    }

    private double bm25(IndexedChunk chunk, Set<String> queryTerms)
    {
        double k1 = 1.5D;
        double b = 0.75D;
        double score = 0D;
        int totalChunks = chunks.size();
        for (String term : queryTerms)
        {
            Integer tf = chunk.termFrequency.get(term);
            if (tf == null || tf <= 0)
            {
                continue;
            }
            int df = Math.max(1, documentFrequency.getOrDefault(term, 0));
            double idf = Math.log(1D + (totalChunks - df + 0.5D) / (df + 0.5D));
            double denominator = tf + k1 * (1D - b + b * chunk.termLength / averageChunkTerms);
            score += idf * (tf * (k1 + 1D)) / denominator;
        }
        return score;
    }

    private double adjustScore(IndexedChunk chunk, double score, String query)
    {
        String content = defaultString(chunk.getContent()).toLowerCase(Locale.ROOT);
        String lowerQuery = defaultString(query).toLowerCase(Locale.ROOT);
        if (containsNoise(content))
        {
            score *= 0.12D;
        }
        if (containsClinicalSignal(content))
        {
            score *= 1.35D;
        }
        if (containsSymptomHint(lowerQuery) && containsClinicalSignal(content))
        {
            score *= 1.15D;
        }
        return score;
    }

    private boolean containsSymptomHint(String text)
    {
        return text.contains("咳")
                || text.contains("痰")
                || text.contains("舌")
                || text.contains("脉")
                || text.contains("痛")
                || text.contains("热")
                || text.contains("寒")
                || text.contains("咽")
                || text.contains("胸")
                || text.contains("腹");
    }

    private String summarizeChunkContent(String content)
    {
        String normalized = defaultString(content)
                .replaceAll("\\s+", " ")
                .trim();
        if (normalized.length() <= 320)
        {
            return normalized;
        }
        return normalized.substring(0, 320) + "...";
    }

    private void buildDocumentFrequency()
    {
        for (IndexedChunk chunk : chunks)
        {
            for (String term : chunk.termFrequency.keySet())
            {
                documentFrequency.put(term, documentFrequency.getOrDefault(term, 0) + 1);
            }
        }
    }

    private List<Path> discoverKnowledgeFiles()
    {
        LinkedHashSet<Path> candidates = new LinkedHashSet<>();
        for (Path root : discoverRoots())
        {
            addConfiguredPaths(candidates, root);
            addDefaultSearchDirectories(candidates, root);
        }
        return candidates.stream()
                .filter(Files::isRegularFile)
                .filter(this::isSupportedKnowledgeFile)
                .filter(this::isSafeSize)
                .collect(Collectors.toList());
    }

    private List<Path> discoverRoots()
    {
        LinkedHashSet<Path> roots = new LinkedHashSet<>();
        Path current = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        for (Path path = current; path != null; path = path.getParent())
        {
            roots.add(path);
            if (roots.size() >= 8)
            {
                break;
            }
        }
        return new ArrayList<>(roots);
    }

    private void addConfiguredPaths(Set<Path> candidates, Path root)
    {
        if (StringUtils.isEmpty(configuredKnowledgePaths))
        {
            return;
        }
        for (String item : configuredKnowledgePaths.split("[,;]"))
        {
            String trimmed = item.trim();
            if (StringUtils.isEmpty(trimmed))
            {
                continue;
            }
            Path path = Paths.get(trimmed);
            if (!path.isAbsolute())
            {
                path = root.resolve(path);
            }
            collectPath(candidates, path.normalize(), true);
        }
    }

    private void addDefaultSearchDirectories(Set<Path> candidates, Path root)
    {
        List<String> relativePaths = Arrays.asList(
                "data",
                "knowledge",
                "kb",
                "rag",
                "assets",
                "public",
                "database",
                "database/sql",
                "server/ruoyi-backend/sql",
                "server/ruoyi-backend/webapi/webapi/src/main/resources",
                "ruoyi-admin/src/main/resources",
                "src/main/resources");
        for (String relativePath : relativePaths)
        {
            collectPath(candidates, root.resolve(relativePath).normalize(), false);
        }
    }

    private void collectPath(Set<Path> candidates, Path path, boolean includeAllSupported)
    {
        if (!Files.exists(path))
        {
            return;
        }
        try
        {
            if (Files.isRegularFile(path))
            {
                if (includeAllSupported || looksLikeKnowledgePath(path))
                {
                    candidates.add(path.toRealPath());
                }
                return;
            }
            try (Stream<Path> stream = Files.walk(path, 8, FileVisitOption.FOLLOW_LINKS))
            {
                stream.filter(Files::isRegularFile)
                        .filter(file -> !isIgnoredPath(file))
                        .filter(file -> includeAllSupported || looksLikeKnowledgePath(file))
                        .forEach(file -> {
                            try
                            {
                                candidates.add(file.toRealPath());
                            }
                            catch (IOException e)
                            {
                                log.debug("Skip unresolved knowledge file {}", file, e);
                            }
                        });
            }
        }
        catch (IOException e)
        {
            log.warn("Failed to scan knowledge path: {}", path, e);
        }
    }

    private boolean looksLikeKnowledgePath(Path file)
    {
        String filename = file.getFileName().toString().toLowerCase(Locale.ROOT);
        String fullPath = file.toString().toLowerCase(Locale.ROOT).replace('\\', '/');
        boolean inKnowledgeDirectory = fullPath.contains("/data/")
                || fullPath.contains("/knowledge/")
                || fullPath.contains("/kb/")
                || fullPath.contains("/rag/");
        boolean inDatabaseDirectory = fullPath.contains("/database/") || fullPath.contains("/sql/");
        boolean keywordName = filename.contains("tcm")
                || filename.contains("zhongyi")
                || filename.contains("zhongyao")
                || filename.contains("chatmed")
                || filename.contains("shennong")
                || filename.contains("fangji")
                || filename.contains("knowledge")
                || filename.contains("kb")
                || filename.contains("20mb");
        boolean databaseKnowledgeName = filename.contains("tcm_vsl")
                || filename.contains("zhongyi")
                || filename.contains("zhongyao")
                || filename.contains("chatmed")
                || filename.contains("shennong")
                || filename.contains("fangji")
                || filename.contains("knowledge")
                || filename.contains("kb")
                || filename.contains("20mb");
        return inKnowledgeDirectory || (!inDatabaseDirectory && keywordName) || (inDatabaseDirectory && databaseKnowledgeName);
    }

    private boolean isSupportedKnowledgeFile(Path file)
    {
        return SUPPORTED_EXTENSIONS.contains(extension(file));
    }

    private boolean isSafeSize(Path file)
    {
        try
        {
            long size = Files.size(file);
            return size > 0 && size <= MAX_FILE_SIZE;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    private boolean isIgnoredPath(Path file)
    {
        String normalized = file.toString().toLowerCase(Locale.ROOT).replace('\\', '/');
        return normalized.contains("/node_modules/")
                || normalized.contains("/.git/")
                || normalized.contains("/target/")
                || normalized.contains("/dist/")
                || normalized.contains("/build/")
                || normalized.contains("/.medical-chat/");
    }

    private String readKnowledgeFile(Path file) throws IOException
    {
        byte[] bytes = Files.readAllBytes(file);
        String text = decode(bytes, StandardCharsets.UTF_8);
        if (countReplacementCharacters(text) > 3)
        {
            text = decode(bytes, Charset.forName("GBK"));
        }
        if ("json".equals(extension(file)))
        {
            return flattenJson(text);
        }
        return text;
    }

    private String decode(byte[] bytes, Charset charset)
    {
        return new String(bytes, charset);
    }

    private int countReplacementCharacters(String text)
    {
        int count = 0;
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == '\uFFFD')
            {
                count++;
            }
        }
        return count;
    }

    private String flattenJson(String text)
    {
        try
        {
            JsonNode root = objectMapper.readTree(text);
            List<String> rows = new ArrayList<>();
            flattenJsonNode(root, rows);
            return String.join("\n\n", rows);
        }
        catch (Exception e)
        {
            return text;
        }
    }

    private void flattenJsonNode(JsonNode node, List<String> rows)
    {
        if (node == null || node.isNull())
        {
            return;
        }
        if (node.isArray())
        {
            for (JsonNode child : node)
            {
                flattenJsonNode(child, rows);
            }
            return;
        }
        if (node.isObject())
        {
            List<String> values = new ArrayList<>();
            node.fields().forEachRemaining(entry -> collectJsonScalars(entry.getKey(), entry.getValue(), values));
            if (!values.isEmpty())
            {
                rows.add(String.join("\n", values));
            }
            return;
        }
        rows.add(node.asText());
    }

    private void collectJsonScalars(String key, JsonNode node, List<String> values)
    {
        if (node == null || node.isNull())
        {
            return;
        }
        if (node.isValueNode())
        {
            String value = node.asText("");
            if (StringUtils.isNotEmpty(value))
            {
                values.add(key + "：" + value);
            }
            return;
        }
        if (node.isArray())
        {
            for (JsonNode child : node)
            {
                collectJsonScalars(key, child, values);
            }
            return;
        }
        node.fields().forEachRemaining(entry -> collectJsonScalars(entry.getKey(), entry.getValue(), values));
    }

    private List<String> splitChunks(String text)
    {
        if (StringUtils.isEmpty(text))
        {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        int start = 0;
        while (start < text.length())
        {
            int end = Math.min(text.length(), start + CHUNK_SIZE);
            String chunk = text.substring(start, end).trim();
            if (StringUtils.isNotEmpty(chunk))
            {
                result.add(chunk);
            }
            if (end >= text.length())
            {
                break;
            }
            start = Math.max(end - CHUNK_OVERLAP, start + 1);
        }
        return result;
    }

    private List<String> tokenize(String text)
    {
        if (StringUtils.isEmpty(text))
        {
            return Collections.emptyList();
        }
        String lower = text.toLowerCase(Locale.ROOT);
        List<String> tokens = new ArrayList<>();
        Matcher matcher = ASCII_WORD.matcher(lower);
        while (matcher.find())
        {
            tokens.add(matcher.group());
        }

        List<Character> chineseChars = new ArrayList<>();
        for (int i = 0; i < lower.length(); i++)
        {
            char ch = lower.charAt(i);
            if (isCjk(ch))
            {
                tokens.add(String.valueOf(ch));
                chineseChars.add(ch);
            }
            else
            {
                addBigrams(tokens, chineseChars);
                chineseChars.clear();
            }
        }
        addBigrams(tokens, chineseChars);
        return tokens;
    }

    private void addBigrams(List<String> tokens, List<Character> chars)
    {
        for (int i = 0; i + 1 < chars.size(); i++)
        {
            tokens.add(new String(new char[] { chars.get(i), chars.get(i + 1) }));
        }
    }

    private boolean isCjk(char ch)
    {
        return ch >= '\u4E00' && ch <= '\u9FFF';
    }

    private Map<String, Integer> termFrequency(List<String> tokens)
    {
        Map<String, Integer> frequency = new HashMap<>();
        for (String token : tokens)
        {
            frequency.put(token, frequency.getOrDefault(token, 0) + 1);
        }
        return frequency;
    }

    private String normalizeText(String text)
    {
        return text.replace("\uFEFF", "")
                .replaceAll("(?i)<br\\s*/?>", "\n")
                .replaceAll("<[^>]+>", " ")
                .replaceAll("[\\t\\x0B\\f\\r ]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private boolean containsNoise(String content)
    {
        return content.contains("sys_menu")
                || content.contains("create table")
                || content.contains("row_format")
                || content.contains("system/article/index")
                || content.contains("documentation")
                || content.contains("admin',")
                || content.contains("insert into `sys_")
                || content.contains("alter table")
                || content.contains("primary key")
                || content.contains("engine = innodb");
    }

    private boolean containsClinicalSignal(String content)
    {
        return content.contains("症状")
                || content.contains("主治")
                || content.contains("功效")
                || content.contains("辨证")
                || content.contains("治疗")
                || content.contains("治法")
                || content.contains("用药")
                || content.contains("禁忌")
                || content.contains("适应症")
                || content.contains("方选");
    }

    private String inferTitle(Path file, String text)
    {
        String filename = file.getFileName().toString();
        String firstLine = Arrays.stream(text.split("\\R"))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .findFirst()
                .orElse(filename);
        if (firstLine.length() > 60)
        {
            firstLine = firstLine.substring(0, 60);
        }
        return firstLine;
    }

    private String toDisplayPath(Path file)
    {
        return file.toAbsolutePath().normalize().toString();
    }

    private String extension(Path file)
    {
        String name = file.getFileName().toString();
        int index = name.lastIndexOf('.');
        if (index < 0 || index == name.length() - 1)
        {
            return "";
        }
        return name.substring(index + 1).toLowerCase(Locale.ROOT);
    }

    private double roundScore(double value)
    {
        return Math.round(value * 10000D) / 10000D;
    }

    private TcmKnowledgeChunk copyChunk(TcmKnowledgeChunk source, boolean summary)
    {
        TcmKnowledgeChunk target = new TcmKnowledgeChunk();
        target.setId(source.getId());
        target.setSource(source.getSource());
        target.setFilename(source.getFilename());
        target.setTitle(source.getTitle());
        target.setScore(source.getScore());
        String content = defaultString(source.getContent());
        if (summary && content.length() > 240)
        {
            content = content.substring(0, 240) + "...";
        }
        target.setContent(content);
        return target;
    }

    private String defaultString(String value)
    {
        return value == null ? "" : value;
    }

    private String normalizeForDedupe(String value)
    {
        return defaultString(value)
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private static class IndexedChunk extends TcmKnowledgeChunk
    {
        private Map<String, Integer> termFrequency = new HashMap<>();

        private int termLength;
    }
}
