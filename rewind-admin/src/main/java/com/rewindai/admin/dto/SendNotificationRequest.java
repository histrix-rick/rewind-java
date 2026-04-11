package com.rewindai.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * 后台管理 - 发送系统通知请求DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "发送系统通知请求")
public class SendNotificationRequest {

    @Schema(description = "目标用户ID列表（为空表示发送给所有用户）")
    private List<UUID> userIds;

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最多200字符")
    @Schema(description = "标题", required = true)
    private String title;

    @Schema(description = "内容")
    private String content;
}
