package com.rewindai.system.report.repository;

import com.rewindai.system.report.entity.ContentReport;
import com.rewindai.system.report.enums.ReportStatus;
import com.rewindai.system.report.enums.ReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 内容举报Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface ContentReportRepository extends JpaRepository<ContentReport, Long> {

    /**
     * 按状态分页查询举报
     */
    Page<ContentReport> findByStatus(ReportStatus status, Pageable pageable);

    /**
     * 按目标类型分页查询举报
     */
    Page<ContentReport> findByTargetType(ReportTargetType targetType, Pageable pageable);

    /**
     * 按状态和目标类型分页查询举报
     */
    Page<ContentReport> findByStatusAndTargetType(ReportStatus status, ReportTargetType targetType, Pageable pageable);

    /**
     * 按举报者ID查询
     */
    Page<ContentReport> findByReporterId(UUID reporterId, Pageable pageable);

    /**
     * 按处理人ID查询
     */
    Page<ContentReport> findByHandledBy(Long handledBy, Pageable pageable);

    /**
     * 按目标查询
     */
    Page<ContentReport> findByTargetTypeAndTargetId(ReportTargetType targetType, UUID targetId, Pageable pageable);

    /**
     * 统计各状态的举报数量
     */
    @Query("SELECT r.status, COUNT(r) FROM ContentReport r GROUP BY r.status")
    Object[] countByStatus();

    /**
     * 统计某时间段内的举报数量
     */
    @Query("SELECT COUNT(r) FROM ContentReport r WHERE r.createdAt >= :startDate AND r.createdAt <= :endDate")
    Long countByDateRange(@Param("startDate") java.time.OffsetDateTime startDate,
                          @Param("endDate") java.time.OffsetDateTime endDate);

    /**
     * 检查目标是否被举报过
     */
    boolean existsByTargetTypeAndTargetIdAndStatusNot(ReportTargetType targetType, UUID targetId, ReportStatus status);
}
