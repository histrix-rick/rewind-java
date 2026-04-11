package com.rewindai.system.security.repository;

import com.rewindai.system.security.entity.BackupTask;
import com.rewindai.system.security.enums.RuleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 备份任务 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface BackupTaskRepository extends JpaRepository<BackupTask, Long> {

    Optional<BackupTask> findByTaskName(String taskName);

    boolean existsByTaskName(String taskName);

    List<BackupTask> findByStatus(RuleStatus status);

    Page<BackupTask> findByStatus(RuleStatus status, Pageable pageable);

    Page<BackupTask> findByTaskType(String taskType, Pageable pageable);

    List<BackupTask> findByCronExpressionIsNotNullAndStatus(RuleStatus status);
}
