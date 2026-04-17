package com.rewindai.system.customer.agent;

import com.rewindai.system.customer.knowledge.KnowledgeBaseRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceAgent {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceAgent.class);

    private final KnowledgeBaseRetriever retriever;
    private final CustomerServicePrompt promptBuilder;
    private final DoubaoChatModel chatModel;

    public CustomerServiceAgent(
        KnowledgeBaseRetriever retriever,
        CustomerServicePrompt promptBuilder,
        DoubaoChatModel chatModel
    ) {
        this.retriever = retriever;
        this.promptBuilder = promptBuilder;
        this.chatModel = chatModel;
    }

    public String chat(String userQuery) {
        log.info("Processing customer query: {}", userQuery);

        // 1. 检索相关知识库片段
        List<String> relevantSections = retriever.findRelevant(userQuery, 3);
        log.info("Found {} relevant sections", relevantSections.size());

        // 2. 构造 System Prompt
        String systemPrompt = promptBuilder.buildSystemPrompt(relevantSections);

        // 3. 调用大模型
        String answer = chatModel.generate(systemPrompt, userQuery);

        log.info("Generated response: {}", answer);
        return answer;
    }
}
