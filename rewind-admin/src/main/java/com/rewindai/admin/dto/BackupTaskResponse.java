package com.rewindai.admin.dto;

import com.rewindai.system.security.entity.BackupTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 备份任务响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupTaskResponse {

    private Long id;
    private String taskName;
    private String taskType;
    private String backupType;
    private String cronExpression;
    private String storagePath;
    private Integer retentionDays;
    private Boolean compress;
    private String status;
    private OffsetDateTime lastExecutedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static BackupTaskResponse from(BackupTask task) {
        return BackupTaskResponse.builder()
                .id(task.getId())
                .taskName(task.getTaskName())
                .taskType(task.getTaskType())
                .backupType(task.getBackupType())
                .cronExpression(task.getCronExpression())
                .storagePath(task.getStoragePath())
                .retentionDays(task.getRetentionDays())
                .compress(task.getCompress())
                .status(task.getStatus() != null ? task.getStatus().name() : null)
                .lastExecutedAt(task.getLastExecutedAt())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
