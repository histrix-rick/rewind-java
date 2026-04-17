package com.rewindai.system.customer.agent;

import com.rewindai.system.aijudge.client.DoubaoApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DoubaoChatModel {

    private static final Logger log = LoggerFactory.getLogger(DoubaoChatModel.class);

    private final DoubaoApiClient doubaoApiClient;

    public DoubaoChatModel(DoubaoApiClient doubaoApiClient) {
        this.doubaoApiClient = doubaoApiClient;
    }

    public String generate(String systemPrompt, String userMessage) {
        log.debug("Generating response with system prompt length: {}, user message: {}",
            systemPrompt.length(), userMessage);

        return doubaoApiClient.chat(systemPrompt, userMessage);
    }
}
