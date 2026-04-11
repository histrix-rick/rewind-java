package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户关注关系 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, UUID> {

    Optional<UserFollow> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    @Query("SELECT f.followingId FROM UserFollow f WHERE f.followerId = :followerId")
    List<UUID> findFollowingIdsByFollowerId(@Param("followerId") UUID followerId);

    @Query("SELECT f.followerId FROM UserFollow f WHERE f.followingId = :followingId")
    List<UUID> findFollowerIdsByFollowingId(@Param("followingId") UUID followingId);

    long countByFollowerId(UUID followerId);

    long countByFollowingId(UUID followingId);

    @Modifying
    @Transactional
    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    // ========== 后台管理查询方法 ==========

    Page<UserFollow> findByFollowerId(UUID followerId, Pageable pageable);

    Page<UserFollow> findByFollowingId(UUID followingId, Pageable pageable);

    @Query("SELECT COUNT(f) FROM UserFollow f")
    long countAll();
}
