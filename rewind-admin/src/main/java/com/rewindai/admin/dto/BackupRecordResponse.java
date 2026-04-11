package com.rewindai.admin.dto;

import com.rewindai.system.security.entity.BackupRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 备份记录响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupRecordResponse {

    private Long id;
    private Long taskId;
    private String taskName;
    private String backupType;
    private String filePath;
    private Long fileSize;
    private String status;
    private String errorMessage;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private OffsetDateTime createdAt;

    public static BackupRecordResponse from(BackupRecord record) {
        return BackupRecordResponse.builder()
                .id(record.getId())
                .taskId(record.getTaskId())
                .taskName(record.getTaskName())
                .backupType(record.getBackupType())
                .filePath(record.getFilePath())
                .fileSize(record.getFileSize())
                .status(record.getStatus() != null ? record.getStatus().name() : null)
                .errorMessage(record.getErrorMessage())
                .startedAt(record.getStartedAt())
                .completedAt(record.getCompletedAt())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
