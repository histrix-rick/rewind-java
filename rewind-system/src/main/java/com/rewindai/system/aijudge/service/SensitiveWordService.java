package com.rewindai.system.aijudge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 敏感词过滤服务
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
public class SensitiveWordService {

    private Set<String> sensitiveWords;
    private Pattern sensitivePattern;

    @PostConstruct
    public void init() {
        // 初始化敏感词库（示例），实际项目中应该从数据库或配置文件加载
        sensitiveWords = new HashSet<>(Arrays.asList(
                // 这里只是示例，实际应该使用完整的敏感词库
                "敏感词1", "敏感词2", "敏感词3"
        ));

        // 构建正则表达式
        buildPattern();
    }

    /**
     * 构建敏感词正则表达式
     */
    private void buildPattern() {
        if (sensitiveWords.isEmpty()) {
            sensitivePattern = null;
            return;
        }

        StringBuilder patternBuilder = new StringBuilder();
        patternBuilder.append("(");
        boolean first = true;
        for (String word : sensitiveWords) {
            if (!first) {
                patternBuilder.append("|");
            }
            patternBuilder.append(Pattern.quote(word));
            first = false;
        }
        patternBuilder.append(")");

        sensitivePattern = Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE);
    }

    /**
     * 检查文本是否包含敏感词
     */
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        if (sensitivePattern == null) {
            return false;
        }

        return sensitivePattern.matcher(text).find();
    }

    /**
     * 找出文本中的所有敏感词
     */
    public List<String> findSensitiveWords(String text) {
        List<String> found = new ArrayList<>();
        if (text == null || text.trim().isEmpty() || sensitivePattern == null) {
            return found;
        }

        var matcher = sensitivePattern.matcher(text);
        while (matcher.find()) {
            found.add(matcher.group());
        }

        return found;
    }

    /**
     * 过滤敏感词，替换为*
     */
    public String filterSensitiveWords(String text) {
        if (text == null || text.trim().isEmpty() || sensitivePattern == null) {
            return text;
        }

        return sensitivePattern.matcher(text).replaceAll(matchResult -> {
            int length = matchResult.group().length();
            return "*".repeat(length);
        });
    }

    /**
     * 添加敏感词
     */
    public void addSensitiveWord(String word) {
        if (word != null && !word.trim().isEmpty()) {
            sensitiveWords.add(word.trim());
            buildPattern();
            log.info("添加敏感词: {}", word);
        }
    }

    /**
     * 移除敏感词
     */
    public void removeSensitiveWord(String word) {
        if (word != null) {
            sensitiveWords.remove(word);
            buildPattern();
            log.info("移除敏感词: {}", word);
        }
    }
}
