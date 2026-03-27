package com.rewindai.system.aijudge.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rewindai.system.aijudge.config.DoubaoConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 豆包API客户端 - 使用火山引擎Responses API格式
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DoubaoApiClient {

    private final DoubaoConfigProperties config;

    /**
     * 调用豆包API进行聊天
     */
    public String chat(String systemPrompt, String userMessage) {
        try {
            JSONObject requestBody = buildRequestBody(systemPrompt, userMessage);

            // 使用chat/completions端点
            String endpoint = config.getEndpoint();
            if (endpoint.contains("/responses")) {
                endpoint = endpoint.replace("/responses", "/chat/completions");
            }

            log.info("调用豆包API: model={}, endpoint={}", config.getModel(), endpoint);
            log.debug("请求体: {}", requestBody.toString());

            HttpResponse response = HttpRequest.post(endpoint)
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .timeout(60000)
                    .execute();

            String responseBody = response.body();
            log.info("豆包API响应状态: {}", response.getStatus());
            log.debug("豆包API响应内容: {}", responseBody);

            if (!response.isOk()) {
                log.error("豆包API调用失败: status={}, body={}", response.getStatus(), responseBody);
                throw new RuntimeException("豆包API调用失败: status=" + response.getStatus() + ", body=" + responseBody);
            }

            return parseResponse(responseBody);

        } catch (Exception e) {
            log.error("调用豆包API异常", e);
            throw new RuntimeException("调用豆包API失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建请求体 - OpenAI chat/completions格式
     */
    private JSONObject buildRequestBody(String systemPrompt, String userMessage) {
        JSONObject body = new JSONObject();
        body.set("model", config.getModel());

        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content", systemPrompt);
        messages.add(systemMessage);

        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", userMessage);
        messages.add(userMsg);

        body.set("messages", messages);

        return body;
    }

    /**
     * 解析响应 - OpenAI chat/completions格式
     */
    private String parseResponse(String responseBody) {
        JSONObject json = JSONUtil.parseObj(responseBody);
        JSONArray choices = json.getJSONArray("choices");
        if (choices != null && choices.size() > 0) {
            JSONObject choice = choices.getJSONObject(0);
            JSONObject message = choice.getJSONObject("message");
            if (message != null) {
                String text = message.getStr("content");
                log.info("成功解析豆包API响应: {}", text);
                return text;
            }
        }
        log.error("无法解析豆包API响应: {}", responseBody);
        throw new RuntimeException("豆包API返回格式无法解析");
    }

    /**
     * 解析JSON响应
     */
    public Map<String, Object> parseJsonResponse(String jsonStr) {
        try {
            return JSONUtil.toBean(jsonStr, Map.class);
        } catch (Exception e) {
            log.error("解析JSON响应失败: {}", jsonStr, e);
            return new HashMap<>();
        }
    }
}
