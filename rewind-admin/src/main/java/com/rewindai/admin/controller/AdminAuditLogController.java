package com.rewindai.admin.controller;

import com.rewindai.admin.dto.AdminLogResponse;
import com.rewindai.admin.dto.UserLogResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.security.entity.AdminOperationLog;
import com.rewindai.system.security.entity.UserSensitiveLog;
import com.rewindai.system.security.enums.RiskLevel;
import com.rewindai.system.security.service.AdminOperationLogService;
import com.rewindai.system.security.service.UserSensitiveLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 审计日志管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/audit-log")
@RequiredArgsConstructor
@Tag(name = "后台管理-审计日志", description = "管理员操作日志和用户敏感操作日志接口")
public class AdminAuditLogController {

    private final AdminOperationLogService adminOperationLogService;
    private final UserSensitiveLogService userSensitiveLogService;

    // ========== 管理员操作日志 ==========

    @GetMapping("/admin")
    @Operation(summary = "获取管理员操作日志列表")
    public Result<Page<AdminLogResponse>> getAdminLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer adminId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AdminOperationLog> logs;
        if (keyword != null && !keyword.isEmpty()) {
            logs = adminOperationLogService.searchByKeyword(keyword, pageable);
        } else if (adminId != null) {
            logs = adminOperationLogService.findByAdminId(adminId, pageable);
        } else if (operationType != null && !operationType.isEmpty()) {
            logs = adminOperationLogService.findByOperationType(operationType, pageable);
        } else if (module != null && !module.isEmpty()) {
            logs = adminOperationLogService.findByModule(module, pageable);
        } else if (startTime != null && endTime != null) {
            logs = adminOperationLogService.findByDateRange(startTime, endTime, pageable);
        } else {
            logs = adminOperationLogService.findAll(pageable);
        }
        return Result.success(logs.map(AdminLogResponse::from));
    }

    @GetMapping("/admin/export")
    @Operation(summary = "导出管理员操作日志")
    public ResponseEntity<byte[]> exportAdminLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) {

        List<AdminOperationLog> logs = adminOperationLogService.findByDateRangeForExport(startTime, endTime);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,管理员ID,管理员用户名,操作类型,模块,描述,请求方法,请求URL,响应状态,客户端IP,执行时间(ms),创建时间\n");

        for (AdminOperationLog log : logs) {
            csv.append(log.getId()).append(",");
            csv.append(log.getAdminId()).append(",");
            csv.append(escapeCsv(log.getAdminUsername())).append(",");
            csv.append(escapeCsv(log.getOperationType())).append(",");
            csv.append(escapeCsv(log.getModule())).append(",");
            csv.append(escapeCsv(log.getDescription())).append(",");
            csv.append(escapeCsv(log.getRequestMethod())).append(",");
            csv.append(escapeCsv(log.getRequestUrl())).append(",");
            csv.append(log.getResponseStatus()).append(",");
            csv.append(escapeCsv(log.getClientIp())).append(",");
            csv.append(log.getExecutionTime()).append(",");
            csv.append(log.getCreatedAt() != null ? log.getCreatedAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : "").append("\n");
        }

        String filename = "admin-logs-" + OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".csv";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    // ========== 用户敏感操作日志 ==========

    @GetMapping("/user")
    @Operation(summary = "获取用户敏感操作日志列表")
    public Result<Page<UserLogResponse>> getUserLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserSensitiveLog> logs;
        if (keyword != null && !keyword.isEmpty()) {
            logs = userSensitiveLogService.searchByKeyword(keyword, pageable);
        } else if (operationType != null && !operationType.isEmpty()) {
            logs = userSensitiveLogService.findByOperationType(operationType, pageable);
        } else if (riskLevel != null) {
            logs = userSensitiveLogService.findByRiskLevel(riskLevel, pageable);
        } else if (startTime != null && endTime != null) {
            logs = userSensitiveLogService.findByDateRange(startTime, endTime, pageable);
        } else {
            logs = userSensitiveLogService.findAll(pageable);
        }
        return Result.success(logs.map(UserLogResponse::from));
    }

    @GetMapping("/user/export")
    @Operation(summary = "导出用户敏感操作日志")
    public ResponseEntity<byte[]> exportUserLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) {

        List<UserSensitiveLog> logs = userSensitiveLogService.findByDateRangeForExport(startTime, endTime);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,用户ID,操作类型,描述,客户端IP,设备信息,位置,风险等级,创建时间\n");

        for (UserSensitiveLog log : logs) {
            csv.append(log.getId()).append(",");
            csv.append(log.getUserId()).append(",");
            csv.append(escapeCsv(log.getOperationType())).append(",");
            csv.append(escapeCsv(log.getDescription())).append(",");
            csv.append(escapeCsv(log.getClientIp())).append(",");
            csv.append(escapeCsv(log.getDeviceInfo())).append(",");
            csv.append(escapeCsv(log.getLocation())).append(",");
            csv.append(log.getRiskLevel() != null ? log.getRiskLevel().name() : "").append(",");
            csv.append(log.getCreatedAt() != null ? log.getCreatedAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : "").append("\n");
        }

        String filename = "user-logs-" + OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".csv";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
