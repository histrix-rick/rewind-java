package com.rewindai.admin.dto;

import com.rewindai.system.sms.entity.SmsProviderConfig;
import com.rewindai.system.sms.enums.SmsProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 短信运营商配置响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "短信运营商配置响应")
public class SmsProviderConfigResponse {

    @Schema(description = "配置ID", example = "1")
    private Long id;

    @Schema(description = "运营商编码", example = "ALIYUN")
    private SmsProvider providerCode;

    @Schema(description = "运营商名称", example = "阿里云短信服务")
    private String providerName;

    @Schema(description = "Access Key ID（脱敏）", example = "LTAI5t...***")
    private String accessKeyId;

    @Schema(description = "短信签名", example = "RewindAI")
    private String signName;

    @Schema(description = "登录验证码模板编码", example = "SMS_123456789")
    private String templateCodeLogin;

    @Schema(description = "注册验证码模板编码", example = "SMS_123456790")
    private String templateCodeRegister;

    @Schema(description = "实名认证验证码模板编码", example = "SMS_123456791")
    private String templateCodeVerify;

    @Schema(description = "API端点", example = "dysmsapi.aliyuncs.com")
    private String endpoint;

    @Schema(description = "区域", example = "cn-hangzhou")
    private String region;

    @Schema(description = "是否为默认", example = "false")
    private Boolean isDefault;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "创建时间")
    private OffsetDateTime createdAt;

    @Schema(description = "更新时间")
    private OffsetDateTime updatedAt;

    public static SmsProviderConfigResponse from(SmsProviderConfig config) {
        return SmsProviderConfigResponse.builder()
                .id(config.getId())
                .providerCode(config.getProviderCode())
                .providerName(config.getProviderName())
                .accessKeyId(maskSecret(config.getAccessKeyId()))
                .signName(config.getSignName())
                .templateCodeLogin(config.getTemplateCodeLogin())
                .templateCodeRegister(config.getTemplateCodeRegister())
                .templateCodeVerify(config.getTemplateCodeVerify())
                .endpoint(config.getEndpoint())
                .region(config.getRegion())
                .isDefault(config.getIsDefault())
                .isActive(config.getIsActive())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }

    public static List<SmsProviderConfigResponse> fromList(List<SmsProviderConfig> configs) {
        return configs.stream().map(SmsProviderConfigResponse::from).collect(Collectors.toList());
    }

    private static String maskSecret(String secret) {
        if (secret == null || secret.length() <= 4) {
            return "****";
        }
        return secret.substring(0, Math.min(8, secret.length())) + "***";
    }
}
