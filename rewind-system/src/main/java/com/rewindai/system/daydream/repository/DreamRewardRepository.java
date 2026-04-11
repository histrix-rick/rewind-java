package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.DreamReward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 梦境打赏记录 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface DreamRewardRepository extends JpaRepository<DreamReward, UUID> {

    Page<DreamReward> findByDreamId(UUID dreamId, Pageable pageable);

    Page<DreamReward> findBySenderId(UUID senderId, Pageable pageable);

    Page<DreamReward> findByReceiverId(UUID receiverId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM DreamReward r WHERE r.dreamId = :dreamId")
    BigDecimal sumAmountByDreamId(@Param("dreamId") UUID dreamId);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM DreamReward r WHERE r.receiverId = :receiverId")
    BigDecimal sumAmountByReceiverId(@Param("receiverId") UUID receiverId);

    long countByDreamId(UUID dreamId);

    // ========== 后台管理查询方法 ==========

    @Query("SELECT COUNT(r) FROM DreamReward r")
    long countAll();

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM DreamReward r")
    BigDecimal sumAllAmount();

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM DreamReward r WHERE r.createdAt >= :after")
    BigDecimal sumAmountByCreatedAtAfter(@Param("after") OffsetDateTime after);

    @Query("SELECT COUNT(r) FROM DreamReward r WHERE r.createdAt >= :after")
    long countByCreatedAtAfter(@Param("after") OffsetDateTime after);

    /**
     * 统计：指定时间范围内创建的打赏数
     */
    @Query("SELECT COUNT(r) FROM DreamReward r WHERE r.createdAt >= :start AND r.createdAt < :end")
    long countByCreatedAtBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    /**
     * 统计：指定时间范围内的打赏金额
     */
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM DreamReward r WHERE r.createdAt >= :start AND r.createdAt < :end")
    BigDecimal sumAmountByCreatedAtBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
}
