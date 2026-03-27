package com.rewindai.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 身份证二要素认证配置
 *
 * @author Rewind.ai Team
 */
@Data
@Component
@ConfigurationProperties(prefix = "id-card-check")
public class IdCardCheckProperties {

    /**
     * 是否启用认证（生产环境建议启用，开发环境可以禁用）
     */
    private boolean enabled = true;

    /**
     * AppKey
     */
    private String appKey;

    /**
     * AppSecret
     */
    private String appSecret;

    /**
     * AppCode (用于Authorization头)
     */
    private String appCode;

    /**
     * API地址
     */
    private String apiUrl = "https://kzidcardv1.market.alicloudapi.com/api-mall/api/id_card/check";
}
