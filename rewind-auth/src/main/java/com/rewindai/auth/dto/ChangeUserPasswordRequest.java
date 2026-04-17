package com.rewindai.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户修改密码请求DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户修改密码请求")
public class ChangeUserPasswordRequest {

    @Schema(description = "旧密码", required = true, example = "123456")
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", required = true, example = "newpassword123")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32之间")
    private String newPassword;

    @Schema(description = "确认新密码", required = true, example = "newpassword123")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
