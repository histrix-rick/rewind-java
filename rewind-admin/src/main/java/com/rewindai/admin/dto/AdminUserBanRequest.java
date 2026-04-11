package com.rewindai.admin.dto;

import com.rewindai.system.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台管理 - 用户封禁/解禁请求DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户封禁/解禁请求")
public class AdminUserBanRequest {

    @Schema(description = "用户状态", required = true)
    @NotNull(message = "用户状态不能为空")
    private UserStatus status;

    @Schema(description = "封禁/解禁原因")
    private String reason;
}
