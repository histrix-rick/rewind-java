package com.rewindai.admin.dto;

import com.rewindai.system.report.entity.ContentReport;
import com.rewindai.system.report.enums.ReportCategory;
import com.rewindai.system.report.enums.ReportStatus;
import com.rewindai.system.report.enums.ReportTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 后台管理 - 内容举报响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容举报响应")
public class ContentReportResponse {

    @Schema(description = "举报ID")
    private Long id;

    @Schema(description = "举报人ID")
    private UUID reporterId;

    @Schema(description = "举报人昵称")
    private String reporterNickname;

    @Schema(description = "举报目标类型")
    private ReportTargetType targetType;

    @Schema(description = "举报目标ID")
    private UUID targetId;

    @Schema(description = "举报目标内容摘要")
    private String targetSummary;

    @Schema(description = "举报分类")
    private ReportCategory reportCategory;

    @Schema(description = "举报原因")
    private String reportReason;

    @Schema(description = "详细描述")
    private String description;

    @Schema(description = "证据图片")
    private List<String> evidenceImages;

    @Schema(description = "举报状态")
    private ReportStatus status;

    @Schema(description = "处理人ID")
    private Long handledBy;

    @Schema(description = "处理人姓名")
    private String handlerName;

    @Schema(description = "处理时间")
    private OffsetDateTime handledAt;

    @Schema(description = "处理结果")
    private String handleResult;

    @Schema(description = "处理备注")
    private String handleRemark;

    @Schema(description = "创建时间")
    private OffsetDateTime createdAt;

    @Schema(description = "更新时间")
    private OffsetDateTime updatedAt;

    /**
     * 从实体转换
     */
    public static ContentReportResponse fromEntity(ContentReport report) {
        return ContentReportResponse.builder()
                .id(report.getId())
                .reporterId(report.getReporterId())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reportCategory(report.getReportCategory())
                .reportReason(report.getReportReason())
                .description(report.getDescription())
                .evidenceImages(report.getEvidenceImages())
                .status(report.getStatus())
                .handledBy(report.getHandledBy())
                .handledAt(report.getHandledAt())
                .handleResult(report.getHandleResult())
                .handleRemark(report.getHandleRemark())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
