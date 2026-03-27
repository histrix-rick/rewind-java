package com.rewindai.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性
 *
 * @author Rewind.ai Team
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * 密钥
     */
    private String secret;

    /**
     * 过期时间(毫秒)
     */
    private Long expiration;

    /**
     * 请求头
     */
    private String header;

    /**
     * 令牌前缀
     */
    private String prefix;
}
