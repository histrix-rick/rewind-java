package com.rewindai.system.customer.knowledge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class KnowledgeBaseLoader {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseLoader.class);

    public List<String> loadSections() {
        log.info("Trying to load knowledge base from classpath...");
        List<String> sections = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("app_knowledge_base.md")) {
            if (is == null) {
                log.error("Knowledge base not found in classpath");
                sections.add("# Rewind.ai 白日梦想家\n\n这是一个AI驱动的梦境模拟平台。\n\n常见问题：\n- 如何创建白日梦？在首页点击'开启白日梦'按钮即可。\n- 如何修改密码？在个人中心-设置-账号安全中可以修改密码。");
                return sections;
            }

            StringBuilder fullContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    fullContent.append(line).append("\n");
                }
            }

            // 将整个文档作为一个 section，加上一些常用问答
            sections.add(fullContent.toString());

            // 添加一些常用问答作为单独的 sections，确保能匹配到
            sections.add("如何创建白日梦？在首页点击'开启白日梦'按钮即可。");
            sections.add("如何创建梦境？在首页点击'开启白日梦'按钮即可。");
            sections.add("如何修改密码？在个人中心-设置-账号安全中可以修改密码。");
            sections.add("梦境可以删除吗？可以，先归档后可永久删除。");
            sections.add("梦想币怎么获得？通过被打赏、分享等方式获得。");
            sections.add("如何联系人工客服？工作时间：周一至周五 9:00-18:00，可以拨打客服热线。");
            sections.add("如何编辑个人资料？在个人中心-设置-个人资料中可以编辑。");
            sections.add("实名认证后可以修改吗？实名认证信息提交后不可修改，请谨慎填写。");

        } catch (Exception e) {
            log.error("Failed to load knowledge base from classpath", e);
            sections.add("# Rewind.ai 白日梦想家\n\n这是一个AI驱动的梦境模拟平台。");
        }

        log.info("Loaded {} sections from knowledge base", sections.size());
        return sections;
    }
}
