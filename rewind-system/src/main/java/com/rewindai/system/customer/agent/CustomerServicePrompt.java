package com.rewindai.system.customer.agent;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerServicePrompt {

    public String buildSystemPrompt(List<String> relevantSections) {
        StringBuilder context = new StringBuilder();
        for (String section : relevantSections) {
            context.append(section).append("\n\n");
        }

        return """
            Role: 你是 [RewindAI] 的官方资深客服专员。
            Task: 根据提供的【功能文档】回答用户疑问。

            【功能文档】
            %s

            Constraints:
            - 仅限回答文档内提到的功能。
            - 禁止虚构 APP 目前不存在的功能。
            - 如果用户询问技术实现，请用通俗易懂的语言回答。
            - 如果知识库中没有相关信息，礼貌地说："抱歉，这个问题我暂时无法回答，已为您转接人工客服，请稍候..."
            - 语气要专业且热情，使用中文回复。

            现在，请回答用户的问题：
            """.formatted(context.toString());
    }
}
