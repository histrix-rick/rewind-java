package com.rewindai.system.security.repository;

import com.rewindai.system.security.entity.AdminOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 管理员操作日志 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface AdminOperationLogRepository extends JpaRepository<AdminOperationLog, Long> {

    Page<AdminOperationLog> findByAdminId(Integer adminId, Pageable pageable);

    Page<AdminOperationLog> findByOperationType(String operationType, Pageable pageable);

    Page<AdminOperationLog> findByModule(String module, Pageable pageable);

    @Query("SELECT l FROM AdminOperationLog l WHERE l.createdAt BETWEEN :start AND :end")
    Page<AdminOperationLog> findByDateRange(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, Pageable pageable);

    @Query("SELECT l FROM AdminOperationLog l WHERE l.adminUsername LIKE %:keyword% OR l.description LIKE %:keyword% OR l.requestUrl LIKE %:keyword%")
    Page<AdminOperationLog> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT l FROM AdminOperationLog l WHERE l.createdAt BETWEEN :start AND :end")
    List<AdminOperationLog> findByDateRangeForExport(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
}
