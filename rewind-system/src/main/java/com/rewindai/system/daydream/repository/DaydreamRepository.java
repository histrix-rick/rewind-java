package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.dream.enums.DreamStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 白日梦 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface DaydreamRepository extends JpaRepository<Daydream, UUID> {

    Optional<Daydream> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT d FROM Daydream d WHERE d.userId = :userId AND d.status <> com.rewindai.system.dream.enums.DreamStatus.ARCHIVED")
    Page<Daydream> findByUserIdExcludeArchived(@Param("userId") UUID userId, Pageable pageable);

    Page<Daydream> findByUserId(UUID userId, Pageable pageable);

    List<Daydream> findByUserIdAndIsActiveTrueAndIsFinishedFalse(UUID userId);

    @Query("SELECT COUNT(d) FROM Daydream d WHERE d.userId = :userId AND d.isActive = true AND d.isFinished = false")
    long countActiveDaydreams(@Param("userId") UUID userId);

    Page<Daydream> findByUserIdAndStatus(UUID userId, DreamStatus status, Pageable pageable);

    Page<Daydream> findByIsPublicTrueAndStatusOrderByCreatedAtDesc(DreamStatus status, Pageable pageable);

    @Query("SELECT d FROM Daydream d WHERE d.isPublic = true AND d.status = :status " +
           "AND (d.title LIKE %:keyword% OR d.description LIKE %:keyword%)")
    Page<Daydream> searchPublicDaydreams(@Param("keyword") String keyword,
                                           @Param("status") DreamStatus status,
                                           Pageable pageable);

    @Modifying
    @Query("UPDATE Daydream d SET d.viewCount = d.viewCount + 1 WHERE d.id = :id")
    void incrementViewCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Daydream d SET d.likeCount = d.likeCount + 1 WHERE d.id = :id")
    void incrementLikeCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Daydream d SET d.likeCount = d.likeCount - 1 WHERE d.id = :id AND d.likeCount > 0")
    void decrementLikeCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Daydream d SET d.shareCount = d.shareCount + 1 WHERE d.id = :id")
    void incrementShareCount(@Param("id") UUID id);

    /**
     * 后台管理：分页查询所有白日梦（包括所有状态）
     */
    @Override
    Page<Daydream> findAll(Pageable pageable);

    /**
     * 后台管理：搜索所有白日梦（标题/描述）
     */
    @Query("SELECT d FROM Daydream d WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "d.title LIKE %:keyword% OR " +
           "d.description LIKE %:keyword%)")
    Page<Daydream> searchAllDaydreams(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 后台管理：按状态搜索白日梦
     */
    @Query("SELECT d FROM Daydream d WHERE " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "d.title LIKE %:keyword% OR " +
           "d.description LIKE %:keyword%)")
    Page<Daydream> searchDaydreamsByStatus(@Param("status") DreamStatus status,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);
}
