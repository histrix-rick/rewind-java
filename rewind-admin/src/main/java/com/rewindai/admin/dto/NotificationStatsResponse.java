package com.rewindai.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 后台管理 - 通知统计响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通知统计响应")
public class NotificationStatsResponse {

    @Schema(description = "总通知数")
    private Long totalCount;

    @Schema(description = "未读通知数")
    private Long unreadCount;

    @Schema(description = "各类型通知数")
    private Map<String, Long> typeStats;
}
