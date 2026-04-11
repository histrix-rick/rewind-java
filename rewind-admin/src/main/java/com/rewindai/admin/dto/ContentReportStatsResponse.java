package com.rewindai.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 后台管理 - 举报统计响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "举报统计响应")
public class ContentReportStatsResponse {

    @Schema(description = "待处理举报数")
    private Long pendingCount;

    @Schema(description = "处理中举报数")
    private Long processingCount;

    @Schema(description = "已解决举报数")
    private Long resolvedCount;

    @Schema(description = "已驳回举报数")
    private Long dismissedCount;

    @Schema(description = "今日新增举报数")
    private Long todayNewCount;

    @Schema(description = "本周新增举报数")
    private Long weekNewCount;

    @Schema(description = "各分类举报数")
    private Map<String, Long> categoryStats;
}
