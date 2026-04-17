package com.rewindai.system.customer.knowledge;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KnowledgeBaseRetriever {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseRetriever.class);

    private final KnowledgeBaseLoader knowledgeBaseLoader;
    private List<String> knowledgeSections;

    public KnowledgeBaseRetriever(KnowledgeBaseLoader knowledgeBaseLoader) {
        this.knowledgeBaseLoader = knowledgeBaseLoader;
    }

    @PostConstruct
    public void init() {
        log.info("Initializing knowledge base retriever...");
        try {
            knowledgeSections = knowledgeBaseLoader.loadSections();
            log.info("Knowledge base retriever initialized with {} sections", knowledgeSections.size());
            for (int i = 0; i < knowledgeSections.size(); i++) {
                log.info("Section {}: {}", i, knowledgeSections.get(i).substring(0, Math.min(100, knowledgeSections.get(i).length())));
            }
        } catch (Exception e) {
            log.error("Failed to initialize knowledge base", e);
            knowledgeSections = new ArrayList<>();
        }
    }

    public List<String> findRelevant(String query, int maxResults) {
        log.info("Searching for relevant sections for query: {}", query);

        if (knowledgeSections == null || knowledgeSections.isEmpty()) {
            log.warn("Knowledge base is empty!");
            return new ArrayList<>();
        }

        String lowerQuery = query.toLowerCase();
        List<SectionMatch> matches = new ArrayList<>();

        for (int i = 0; i < knowledgeSections.size(); i++) {
            String section = knowledgeSections.get(i);
            String lowerSection = section.toLowerCase();
            int score = calculateMatchScore(lowerSection, lowerQuery);
            if (score > 0) {
                matches.add(new SectionMatch(section, score, i));
                log.info("Section {} matched with score: {}", i, score);
            }
        }

        // 如果没有匹配到，就返回前几个 section
        if (matches.isEmpty()) {
            log.warn("No exact matches found, returning default sections");
            List<String> results = new ArrayList<>();
            for (int i = 0; i < Math.min(maxResults, knowledgeSections.size()); i++) {
                results.add(knowledgeSections.get(i));
            }
            return results;
        }

        matches.sort((a, b) -> Integer.compare(b.score, a.score));

        List<String> results = new ArrayList<>();
        for (int i = 0; i < Math.min(maxResults, matches.size()); i++) {
            results.add(matches.get(i).section);
        }

        log.info("Found {} relevant sections out of {}", results.size(), knowledgeSections.size());
        return results;
    }

    private int calculateMatchScore(String sectionText, String query) {
        int score = 0;

        // 检查完整的查询词
        if (sectionText.contains(query)) {
            score += 100;
        }

        // 检查常见问题
        if (query.contains("修改密码") || query.contains("改密码")) {
            if (sectionText.contains("修改密码")) {
                score += 200;
            }
        }
        if (query.contains("创建") && (query.contains("梦境") || query.contains("白日梦"))) {
            if (sectionText.contains("创建白日梦") || sectionText.contains("开启白日梦")) {
                score += 200;
            }
        }
        if (query.contains("删除") && query.contains("梦境")) {
            if (sectionText.contains("删除")) {
                score += 200;
            }
        }
        if (query.contains("梦想币")) {
            if (sectionText.contains("梦想币")) {
                score += 200;
            }
        }
        if (query.contains("人工客服") || query.contains("联系")) {
            if (sectionText.contains("人工客服")) {
                score += 200;
            }
        }
        if (query.contains("个人资料") || query.contains("编辑资料")) {
            if (sectionText.contains("个人资料")) {
                score += 200;
            }
        }
        if (query.contains("实名") || query.contains("认证")) {
            if (sectionText.contains("实名")) {
                score += 200;
            }
        }

        // 简单的关键词匹配
        String[] keywords = query.split("\\s+");
        for (String keyword : keywords) {
            if (keyword.length() >= 2 && sectionText.contains(keyword)) {
                score += 10;
            }
        }

        return score;
    }

    private static class SectionMatch {
        String section;
        int score;
        int index;

        SectionMatch(String section, int score, int index) {
            this.section = section;
            this.score = score;
            this.index = index;
        }
    }
}
