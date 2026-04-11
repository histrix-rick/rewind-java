package com.rewindai.system.security.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.security.entity.BackupRecord;
import com.rewindai.system.security.entity.BackupTask;
import com.rewindai.system.security.enums.BackupStatus;
import com.rewindai.system.security.enums.RuleStatus;
import com.rewindai.system.security.repository.BackupRecordRepository;
import com.rewindai.system.security.repository.BackupTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 备份 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private final BackupTaskRepository backupTaskRepository;
    private final BackupRecordRepository backupRecordRepository;

    // ========== 备份任务管理 ==========

    public Optional<BackupTask> findTaskById(Long id) {
        return backupTaskRepository.findById(id);
    }

    public Optional<BackupTask> findTaskByName(String taskName) {
        return backupTaskRepository.findByTaskName(taskName);
    }

    public boolean existsTaskByName(String taskName) {
        return backupTaskRepository.existsByTaskName(taskName);
    }

    public Page<BackupTask> findAllTasks(Pageable pageable) {
        return backupTaskRepository.findAll(pageable);
    }

    public Page<BackupTask> findTasksByStatus(RuleStatus status, Pageable pageable) {
        return backupTaskRepository.findByStatus(status, pageable);
    }

    public Page<BackupTask> findTasksByTaskType(String taskType, Pageable pageable) {
        return backupTaskRepository.findByTaskType(taskType, pageable);
    }

    public List<BackupTask> findScheduledTasks() {
        return backupTaskRepository.findByCronExpressionIsNotNullAndStatus(RuleStatus.ACTIVE);
    }

    @Transactional
    public BackupTask createTask(BackupTask task) {
        if (existsTaskByName(task.getTaskName())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "备份任务名称已存在");
        }
        BackupTask saved = backupTaskRepository.save(task);
        log.info("备份任务创建成功: taskName={}", saved.getTaskName());
        return saved;
    }

    @Transactional
    public BackupTask updateTask(Long id, BackupTask update) {
        BackupTask task = findTaskById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "备份任务不存在"));

        if (update.getTaskName() != null && !update.getTaskName().equals(task.getTaskName())) {
            if (existsTaskByName(update.getTaskName())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "备份任务名称已存在");
            }
            task.setTaskName(update.getTaskName());
        }
        if (update.getTaskType() != null) {
            task.setTaskType(update.getTaskType());
        }
        if (update.getBackupType() != null) {
            task.setBackupType(update.getBackupType());
        }
        if (update.getCronExpression() != null) {
            task.setCronExpression(update.getCronExpression());
        }
        if (update.getStoragePath() != null) {
            task.setStoragePath(update.getStoragePath());
        }
        if (update.getRetentionDays() != null) {
            task.setRetentionDays(update.getRetentionDays());
        }
        if (update.getCompress() != null) {
            task.setCompress(update.getCompress());
        }
        if (update.getStatus() != null) {
            task.setStatus(update.getStatus());
        }

        BackupTask saved = backupTaskRepository.save(task);
        log.info("备份任务更新成功: taskName={}", saved.getTaskName());
        return saved;
    }

    @Transactional
    public void deleteTask(Long id) {
        BackupTask task = findTaskById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "备份任务不存在"));
        backupTaskRepository.delete(task);
        log.info("备份任务删除成功: taskName={}", task.getTaskName());
    }

    // ========== 备份记录管理 ==========

    public Optional<BackupRecord> findRecordById(Long id) {
        return backupRecordRepository.findById(id);
    }

    public Page<BackupRecord> findAllRecords(Pageable pageable) {
        return backupRecordRepository.findAll(pageable);
    }

    public Page<BackupRecord> findRecordsByTaskId(Long taskId, Pageable pageable) {
        return backupRecordRepository.findByTaskId(taskId, pageable);
    }

    public List<BackupRecord> findRecordsByTaskId(Long taskId) {
        return backupRecordRepository.findByTaskId(taskId);
    }

    public Page<BackupRecord> findRecordsByStatus(BackupStatus status, Pageable pageable) {
        return backupRecordRepository.findByStatus(status, pageable);
    }

    public Page<BackupRecord> findRecordsByBackupType(String backupType, Pageable pageable) {
        return backupRecordRepository.findByBackupType(backupType, pageable);
    }

    public List<BackupRecord> findSuccessfulRecordsByTaskId(Long taskId) {
        return backupRecordRepository.findSuccessfulByTaskId(taskId);
    }

    @Transactional
    public BackupRecord createRecord(BackupRecord record) {
        return backupRecordRepository.save(record);
    }

    @Transactional
    public BackupRecord updateRecordStatus(Long id, BackupStatus status, String errorMessage) {
        BackupRecord record = findRecordById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "备份记录不存在"));
        record.setStatus(status);
        if (errorMessage != null) {
            record.setErrorMessage(errorMessage);
        }
        if (status == BackupStatus.RUNNING) {
            record.setStartedAt(OffsetDateTime.now());
        } else if (status == BackupStatus.SUCCESS || status == BackupStatus.FAILED) {
            record.setCompletedAt(OffsetDateTime.now());
        }
        return backupRecordRepository.save(record);
    }

    @Transactional
    public BackupRecord executeBackup(Long taskId) {
        BackupTask task = findTaskById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "备份任务不存在"));

        BackupRecord record = BackupRecord.builder()
                .taskId(task.getId())
                .taskName(task.getTaskName())
                .backupType(task.getBackupType())
                .status(BackupStatus.PENDING)
                .build();

        BackupRecord saved = createRecord(record);
        log.info("备份任务开始执行: taskName={}, recordId={}", task.getTaskName(), saved.getId());

        // 更新任务最后执行时间
        task.setLastExecutedAt(OffsetDateTime.now());
        backupTaskRepository.save(task);

        return saved;
    }

    @Transactional
    public void restoreBackup(Long recordId) {
        BackupRecord record = findRecordById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "备份记录不存在"));
        if (record.getStatus() != BackupStatus.SUCCESS) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "只能恢复成功的备份");
        }
        log.info("开始恢复备份: recordId={}, filePath={}", recordId, record.getFilePath());
    }

    @Transactional
    public int cleanOldRecords(int retentionDays) {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(retentionDays);
        int count = backupRecordRepository.deleteOldRecords(cutoff);
        log.info("清理旧备份记录: count={}", count);
        return count;
    }
}
