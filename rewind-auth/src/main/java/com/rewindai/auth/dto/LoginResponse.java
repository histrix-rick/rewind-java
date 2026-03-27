package com.rewindai.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponse {

    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "过期时间（秒）", example = "604800")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID", example = "550e8400-e29b-41d4-a716-446655440000")
        private String userId;

        @Schema(description = "用户名", example = "zhangsan")
        private String username;

        @Schema(description = "手机号", example = "13800138000")
        private String phone;

        @Schema(description = "邮箱", example = "zhangsan@example.com")
        private String email;

        @Schema(description = "性别", example = "1")
        private Integer gender;

        @Schema(description = "出生日期", example = "2000-01-01")
        private String birthDate;

        @Schema(description = "昵称", example = "张三")
        private String nickname;

        @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
        private String avatarUrl;
    }
}
