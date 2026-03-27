package com.rewindai.system.aijudge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 豆包API配置
 *
 * @author Rewind.ai Team
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.doubao")
public class DoubaoConfigProperties {

    /**
     * API Key
     */
    private String apiKey;

    /**
     * API Endpoint
     */
    private String endpoint;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 超时时间（毫秒）
     */
    private Integer timeout = 30000;
}
