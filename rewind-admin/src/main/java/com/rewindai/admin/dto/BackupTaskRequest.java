package com.rewindai.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 备份任务请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupTaskRequest {

    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    @NotBlank(message = "任务类型不能为空")
    private String taskType;

    @NotBlank(message = "备份类型不能为空")
    private String backupType;

    private String cronExpression;

    private String storagePath;

    private Integer retentionDays;

    private Boolean compress;

    private String status;
}
