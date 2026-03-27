package com.rewindai.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员登录请求DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "管理员登录请求")
public class AdminLoginRequest {

    @Schema(description = "账号（用户名/邮箱）", required = true, example = "admin")
    @NotBlank(message = "账号不能为空")
    private String account;

    @Schema(description = "密码", required = true, example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "邮箱验证码", required = true, example = "123456")
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;
}
