package com.rewindai.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员登录响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "管理员登录响应")
public class AdminLoginResponse {

    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "过期时间（秒）", example = "604800")
    private Long expiresIn;

    @Schema(description = "管理员信息")
    private AdminInfo adminInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "管理员信息")
    public static class AdminInfo {
        @Schema(description = "管理员ID", example = "1")
        private Long adminId;

        @Schema(description = "用户名", example = "admin")
        private String username;

        @Schema(description = "邮箱", example = "chenwenqi1991@gmail.com")
        private String email;

        @Schema(description = "真实姓名", example = "谌文琦")
        private String realName;

        @Schema(description = "是否需要修改密码", example = "true")
        private Boolean needChangePassword;
    }
}
