package com.rewindai.admin.controller;

import com.rewindai.admin.dto.*;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.security.entity.BackupRecord;
import com.rewindai.system.security.entity.BackupTask;
import com.rewindai.system.security.enums.BackupStatus;
import com.rewindai.system.security.enums.RuleStatus;
import com.rewindai.system.security.service.BackupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据备份管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/backup")
@RequiredArgsConstructor
@Tag(name = "后台管理-数据备份", description = "备份任务配置和备份记录管理接口")
public class AdminBackupController {

    private final BackupService backupService;

    // ========== 备份任务管理 ==========

    @GetMapping("/tasks")
    @Operation(summary = "获取备份任务列表")
    public Result<Page<BackupTaskResponse>> getBackupTasks(
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) RuleStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<BackupTask> tasks;
        if (taskType != null && !taskType.isEmpty()) {
            tasks = backupService.findTasksByTaskType(taskType, pageable);
        } else if (status != null) {
            tasks = backupService.findTasksByStatus(status, pageable);
        } else {
            tasks = backupService.findAllTasks(pageable);
        }
        return Result.success(tasks.map(BackupTaskResponse::from));
    }

    @GetMapping("/tasks/{id}")
    @Operation(summary = "获取备份任务详情")
    public Result<BackupTaskResponse> getBackupTaskById(@PathVariable Long id) {
        return backupService.findTaskById(id)
                .map(BackupTaskResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("备份任务不存在"));
    }

    @PostMapping("/tasks")
    @Operation(summary = "创建备份任务")
    public Result<BackupTaskResponse> createBackupTask(@Valid @RequestBody BackupTaskRequest request) {
        BackupTask task = BackupTask.builder()
                .taskName(request.getTaskName())
                .taskType(request.getTaskType())
                .backupType(request.getBackupType())
                .cronExpression(request.getCronExpression())
                .storagePath(request.getStoragePath())
                .retentionDays(request.getRetentionDays())
                .compress(request.getCompress())
                .status(request.getStatus() != null ? RuleStatus.valueOf(request.getStatus()) : RuleStatus.ACTIVE)
                .build();

        BackupTask saved = backupService.createTask(task);
        return Result.success(BackupTaskResponse.from(saved));
    }

    @PutMapping("/tasks/{id}")
    @Operation(summary = "更新备份任务")
    public Result<BackupTaskResponse> updateBackupTask(
            @PathVariable Long id,
            @Valid @RequestBody BackupTaskRequest request) {

        BackupTask update = BackupTask.builder()
                .taskName(request.getTaskName())
                .taskType(request.getTaskType())
                .backupType(request.getBackupType())
                .cronExpression(request.getCronExpression())
                .storagePath(request.getStoragePath())
                .retentionDays(request.getRetentionDays())
                .compress(request.getCompress())
                .status(request.getStatus() != null ? RuleStatus.valueOf(request.getStatus()) : null)
                .build();

        BackupTask updated = backupService.updateTask(id, update);
        return Result.success(BackupTaskResponse.from(updated));
    }

    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "删除备份任务")
    public Result<Void> deleteBackupTask(@PathVariable Long id) {
        backupService.deleteTask(id);
        return Result.success();
    }

    @PostMapping("/tasks/{id}/execute")
    @Operation(summary = "立即执行备份任务")
    public Result<BackupRecordResponse> executeBackupTask(@PathVariable Long id) {
        BackupRecord record = backupService.executeBackup(id);
        return Result.success(BackupRecordResponse.from(record));
    }

    // ========== 备份记录管理 ==========

    @GetMapping("/records")
    @Operation(summary = "获取备份记录列表")
    public Result<Page<BackupRecordResponse>> getBackupRecords(
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) String backupType,
            @RequestParam(required = false) BackupStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<BackupRecord> records;
        if (taskId != null) {
            records = backupService.findRecordsByTaskId(taskId, pageable);
        } else if (backupType != null && !backupType.isEmpty()) {
            records = backupService.findRecordsByBackupType(backupType, pageable);
        } else if (status != null) {
            records = backupService.findRecordsByStatus(status, pageable);
        } else {
            records = backupService.findAllRecords(pageable);
        }
        return Result.success(records.map(BackupRecordResponse::from));
    }

    @GetMapping("/records/{id}")
    @Operation(summary = "获取备份记录详情")
    public Result<BackupRecordResponse> getBackupRecordById(@PathVariable Long id) {
        return backupService.findRecordById(id)
                .map(BackupRecordResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("备份记录不存在"));
    }

    @GetMapping("/records/task/{taskId}/successful")
    @Operation(summary = "获取任务的成功备份记录")
    public Result<List<BackupRecordResponse>> getSuccessfulBackupRecords(@PathVariable Long taskId) {
        List<BackupRecord> records = backupService.findSuccessfulRecordsByTaskId(taskId);
        return Result.success(records.stream().map(BackupRecordResponse::from).toList());
    }

    @PostMapping("/records/{id}/restore")
    @Operation(summary = "从备份恢复")
    public Result<String> restoreBackup(@PathVariable Long id) {
        backupService.restoreBackup(id);
        return Result.success("数据恢复已开始，请查看执行记录");
    }

    @DeleteMapping("/records/clean-old")
    @Operation(summary = "清理旧备份记录")
    public Result<Integer> cleanOldRecords(@RequestParam(defaultValue = "30") int retentionDays) {
        return Result.success(backupService.cleanOldRecords(retentionDays));
    }
}
