package com.rewindai.admin.dto;

import com.rewindai.system.sms.enums.SmsProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短信运营商配置请求DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "短信运营商配置请求")
public class SmsProviderConfigRequest {

    @Schema(description = "运营商编码", required = true, example = "ALIYUN")
    @NotNull(message = "运营商编码不能为空")
    private SmsProvider providerCode;

    @Schema(description = "运营商名称", required = true, example = "阿里云短信服务")
    @NotBlank(message = "运营商名称不能为空")
    private String providerName;

    @Schema(description = "Access Key ID", example = "LTAI5t...")
    private String accessKeyId;

    @Schema(description = "Access Key Secret", example = "abc123...")
    private String accessKeySecret;

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
}
