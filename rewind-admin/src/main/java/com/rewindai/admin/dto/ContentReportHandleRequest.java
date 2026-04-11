package com.rewindai.admin.dto;

import com.rewindai.system.report.enums.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台管理 - 举报处理请求DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "举报处理请求")
public class ContentReportHandleRequest {

    @NotNull(message = "处理结果不能为空")
    @Schema(description = "处理结果状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private ReportStatus status;

    @Schema(description = "处理结果描述")
    private String handleResult;

    @Schema(description = "处理备注")
    private String handleRemark;

    @Schema(description = "是否警告用户")
    private Boolean warnUser;

    @Schema(description = "是否删除内容")
    private Boolean deleteContent;

    @Schema(description = "是否封禁用户")
    private Boolean banUser;

    @Schema(description = "封禁时长(天), 仅封禁时有效")
    private Integer banDays;
}
