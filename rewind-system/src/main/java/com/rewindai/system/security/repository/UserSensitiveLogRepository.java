package com.rewindai.system.security.repository;

import com.rewindai.system.security.entity.UserSensitiveLog;
import com.rewindai.system.security.enums.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 用户敏感操作日志 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface UserSensitiveLogRepository extends JpaRepository<UserSensitiveLog, Long> {

    Page<UserSensitiveLog> findByUserId(UUID userId, Pageable pageable);

    Page<UserSensitiveLog> findByOperationType(String operationType, Pageable pageable);

    Page<UserSensitiveLog> findByRiskLevel(RiskLevel riskLevel, Pageable pageable);

    @Query("SELECT l FROM UserSensitiveLog l WHERE l.createdAt BETWEEN :start AND :end")
    Page<UserSensitiveLog> findByDateRange(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, Pageable pageable);

    @Query("SELECT l FROM UserSensitiveLog l WHERE l.description LIKE %:keyword% OR l.clientIp LIKE %:keyword% OR l.deviceInfo LIKE %:keyword%")
    Page<UserSensitiveLog> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT l FROM UserSensitiveLog l WHERE l.createdAt BETWEEN :start AND :end")
    List<UserSensitiveLog> findByDateRangeForExport(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
}
