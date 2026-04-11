package com.rewindai.system.security.repository;

import com.rewindai.system.security.entity.BackupRecord;
import com.rewindai.system.security.enums.BackupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 备份记录 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface BackupRecordRepository extends JpaRepository<BackupRecord, Long> {

    Page<BackupRecord> findByTaskId(Long taskId, Pageable pageable);

    List<BackupRecord> findByTaskId(Long taskId);

    Page<BackupRecord> findByStatus(BackupStatus status, Pageable pageable);

    Page<BackupRecord> findByBackupType(String backupType, Pageable pageable);

    @Query("SELECT r FROM BackupRecord r WHERE r.createdAt BETWEEN :start AND :end")
    Page<BackupRecord> findByDateRange(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, Pageable pageable);

    @Query("SELECT r FROM BackupRecord r WHERE r.taskId = :taskId AND r.status = 'SUCCESS' ORDER BY r.createdAt DESC")
    List<BackupRecord> findSuccessfulByTaskId(@Param("taskId") Long taskId);

    @Modifying
    @Query("DELETE FROM BackupRecord r WHERE r.createdAt < :cutoff")
    int deleteOldRecords(@Param("cutoff") OffsetDateTime cutoff);
}
