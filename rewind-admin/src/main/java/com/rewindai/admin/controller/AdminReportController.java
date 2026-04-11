package com.rewindai.admin.controller;

import com.rewindai.admin.dto.ContentReportHandleRequest;
import com.rewindai.admin.dto.ContentReportResponse;
import com.rewindai.admin.dto.ContentReportStatsResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.report.entity.ContentReport;
import com.rewindai.system.report.entity.ContentReportAction;
import com.rewindai.system.report.enums.ReportStatus;
import com.rewindai.system.report.enums.ReportTargetType;
import com.rewindai.system.report.service.ContentReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理 - 内容举报控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/report")
@RequiredArgsConstructor
@Tag(name = "后台管理-内容举报", description = "后台管理系统内容举报管理接口")
public class AdminReportController {

    private final ContentReportService contentReportService;

    @GetMapping("/list")
    @Operation(summary = "获取举报列表", description = "分页获取内容举报列表，支持筛选")
    public Result<Page<ContentReportResponse>> getReportList(
            @Parameter(description = "举报状态") @RequestParam(required = false) ReportStatus status,
            @Parameter(description = "举报目标类型") @RequestParam(required = false) ReportTargetType targetType,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<ContentReport> reports = contentReportService.getReportList(status, targetType, pageable);
        Page<ContentReportResponse> responsePage = reports.map(ContentReportResponse::fromEntity);
        return Result.success(responsePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取举报详情", description = "根据ID获取内容举报详情")
    public Result<ContentReportResponse> getReportDetail(@PathVariable Long id) {
        ContentReport report = contentReportService.getReportById(id);
        if (report == null) {
            return Result.notFound("举报不存在");
        }
        ContentReportResponse response = ContentReportResponse.fromEntity(report);
        return Result.success(response);
    }

    @GetMapping("/{id}/actions")
    @Operation(summary = "获取举报操作记录", description = "获取内容举报的操作历史记录")
    public Result<List<ContentReportAction>> getReportActions(@PathVariable Long id) {
        List<ContentReportAction> actions = contentReportService.getReportActions(id);
        return Result.success(actions);
    }

    @PutMapping("/{id}/process")
    @Operation(summary = "开始处理举报", description = "将举报状态标记为处理中")
    public Result<ContentReportResponse> startProcessing(
            @PathVariable Long id) {
        // 暂时使用固定的管理员ID和名称
        Long adminId = 1L;
        String adminName = "admin";
        ContentReport report = contentReportService.startProcessing(id, adminId, adminName);
        if (report == null) {
            return Result.notFound("举报不存在");
        }
        return Result.success(ContentReportResponse.fromEntity(report));
    }

    @PutMapping("/{id}/handle")
    @Operation(summary = "处理举报", description = "处理内容举报，支持警告用户、删除内容、封禁用户等操作")
    public Result<ContentReportResponse> handleReport(
            @PathVariable Long id,
            @Valid @RequestBody ContentReportHandleRequest request) {
        Long adminId = 1L;
        String adminName = "admin";
        ContentReport report = contentReportService.handleReport(
                id, request.getStatus(), request.getHandleResult(),
                request.getHandleRemark(), adminId, adminName);
        if (report == null) {
            return Result.notFound("举报不存在");
        }

        log.info("管理员 {} 处理了举报 {}, 状态: {}", adminName, id, request.getStatus());
        return Result.success(ContentReportResponse.fromEntity(report));
    }

    @PutMapping("/{id}/dismiss")
    @Operation(summary = "驳回举报", description = "驳回无效的内容举报")
    public Result<ContentReportResponse> dismissReport(
            @PathVariable Long id,
            @RequestParam String reason) {
        Long adminId = 1L;
        String adminName = "admin";
        ContentReport report = contentReportService.dismissReport(id, reason, adminId, adminName);
        if (report == null) {
            return Result.notFound("举报不存在");
        }
        return Result.success(ContentReportResponse.fromEntity(report));
    }

    @GetMapping("/stats")
    @Operation(summary = "获取举报统计", description = "获取内容举报的统计数据")
    public Result<ContentReportStatsResponse> getReportStats() {
        Object[] statusStats = contentReportService.getStatusStats();

        Map<String, Long> statusMap = new HashMap<>();
        if (statusStats != null) {
            for (Object row : statusStats) {
                Object[] rowData = (Object[]) row;
                statusMap.put(rowData[0].toString(), ((Number) rowData[1]).longValue());
            }
        }

        OffsetDateTime todayStart = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime now = OffsetDateTime.now();

        Long todayCount = contentReportService.getCountByDateRange(todayStart, now);
        Long weekCount = contentReportService.getCountByDateRange(weekStart, now);

        ContentReportStatsResponse stats = ContentReportStatsResponse.builder()
                .pendingCount(statusMap.getOrDefault("PENDING", 0L))
                .processingCount(statusMap.getOrDefault("PROCESSING", 0L))
                .resolvedCount(statusMap.getOrDefault("RESOLVED", 0L))
                .dismissedCount(statusMap.getOrDefault("DISMISSED", 0L))
                .todayNewCount(todayCount)
                .weekNewCount(weekCount)
                .build();

        return Result.success(stats);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除举报", description = "批量删除内容举报记录")
    public Result<Void> batchDeleteReports(
            @RequestBody List<Long> ids) {
        Long adminId = 1L;
        String adminName = "admin";
        contentReportService.deleteReports(ids, adminId, adminName);
        return Result.success(null);
    }
}
