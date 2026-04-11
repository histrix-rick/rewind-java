package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.DreamFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 梦境关注 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface DreamFollowRepository extends JpaRepository<DreamFollow, UUID> {

    boolean existsByUserIdAndDreamId(UUID userId, UUID dreamId);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteByUserIdAndDreamId(UUID userId, UUID dreamId);

    List<UUID> findDreamIdsByUserId(UUID userId);

    List<UUID> findUserIdsByDreamId(UUID dreamId);

    long countByDreamId(UUID dreamId);

    long countByUserId(UUID userId);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("DELETE FROM DreamFollow df WHERE df.dreamId = :dreamId")
    void deleteAllByDreamId(@Param("dreamId") UUID dreamId);
}
