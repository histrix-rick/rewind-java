package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.DreamLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 梦境点赞 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface DreamLikeRepository extends JpaRepository<DreamLike, UUID> {

    Optional<DreamLike> findByDreamIdAndUserId(UUID dreamId, UUID userId);

    boolean existsByDreamIdAndUserId(UUID dreamId, UUID userId);

    void deleteByDreamIdAndUserId(UUID dreamId, UUID userId);

    long countByDreamId(UUID dreamId);

    /**
     * 统计：指定时间之后创建的点赞数
     */
    @Query("SELECT COUNT(l) FROM DreamLike l WHERE l.createdAt >= :after")
    long countByCreatedAtAfter(@Param("after") java.time.OffsetDateTime after);

    /**
     * 统计：指定时间范围内创建的点赞数
     */
    @Query("SELECT COUNT(l) FROM DreamLike l WHERE l.createdAt >= :start AND l.createdAt < :end")
    long countByCreatedAtBetween(@Param("start") java.time.OffsetDateTime start, @Param("end") java.time.OffsetDateTime end);
}
