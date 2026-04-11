package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.enums.DreamStatus;
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

    @Query("SELECT d FROM Daydream d WHERE d.userId = :userId AND d.status <> com.rewindai.system.daydream.enums.DreamStatus.ARCHIVED")
    Page<Daydream> findByUserIdExcludeArchived(@Param("userId") UUID userId, Pageable pageable);

    Page<Daydream> findByUserId(UUID userId, Pageable pageable);

    /**
     * 查询已归档的梦境（deletedAt不为空）
     */
    @Query("SELECT d FROM Daydream d WHERE d.userId = :userId AND d.deletedAt IS NOT NULL ORDER BY d.deletedAt DESC")
    Page<Daydream> findArchivedByUserId(@Param("userId") UUID userId, Pageable pageable);

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

    /**
     * 查询指定用户的公开梦境（不包含已归档）
     */
    @Query("SELECT d FROM Daydream d WHERE d.userId = :userId AND d.isPublic = true " +
           "AND d.status <> com.rewindai.system.daydream.enums.DreamStatus.ARCHIVED " +
           "ORDER BY d.createdAt DESC")
    Page<Daydream> findPublicByUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * 后台管理：按公开状态搜索白日梦
     */
    @Query("SELECT d FROM Daydream d WHERE " +
           "(:isPublic IS NULL OR d.isPublic = :isPublic) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "d.title LIKE %:keyword% OR " +
           "d.description LIKE %:keyword%)")
    Page<Daydream> searchDaydreamsByPublic(@Param("isPublic") Boolean isPublic,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);

    /**
     * 后台管理：按状态和公开状态搜索白日梦
     */
    @Query("SELECT d FROM Daydream d WHERE " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:isPublic IS NULL OR d.isPublic = :isPublic) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "d.title LIKE %:keyword% OR " +
           "d.description LIKE %:keyword%)")
    Page<Daydream> searchDaydreamsByStatusAndPublic(@Param("status") DreamStatus status,
                                                       @Param("isPublic") Boolean isPublic,
                                                       @Param("keyword") String keyword,
                                                       Pageable pageable);

    /**
     * 统计：公开梦境数
     */
    long countByIsPublicTrue();

    /**
     * 统计：总点赞数
     */
    @Query("SELECT COALESCE(SUM(d.likeCount), 0) FROM Daydream d")
    long sumLikeCount();

    /**
     * 统计：总评论数
     */
    @Query("SELECT COALESCE(SUM(d.commentCount), 0) FROM Daydream d")
    long sumCommentCount();

    /**
     * 统计：总打赏金额
     */
    @Query("SELECT COALESCE(SUM(d.rewardAmount), 0) FROM Daydream d")
    java.math.BigDecimal sumRewardAmount();

    /**
     * 统计：指定时间之后创建的白日梦数
     */
    @Query("SELECT COUNT(d) FROM Daydream d WHERE d.createdAt >= :after")
    long countByCreatedAtAfter(@Param("after") java.time.OffsetDateTime after);

    /**
     * 统计：指定时间范围内创建的白日梦数
     */
    @Query("SELECT COUNT(d) FROM Daydream d WHERE d.createdAt >= :start AND d.createdAt < :end")
    long countByCreatedAtBetween(@Param("start") java.time.OffsetDateTime start, @Param("end") java.time.OffsetDateTime end);

    /**
     * 统计：指定用户的梦境数
     */
    long countByUserId(UUID userId);

    /**
     * 统计：待审核内容数（reviewStatus = 0）
     */
    @Query("SELECT COUNT(d) FROM Daydream d WHERE d.reviewStatus = com.rewindai.system.daydream.enums.ReviewStatus.PENDING")
    long countByReviewStatusPending();

    /**
     * 导出：查询指定时间范围内的白日梦
     */
    @Query("SELECT d FROM Daydream d WHERE d.createdAt >= :start AND d.createdAt < :end ORDER BY d.createdAt DESC")
    List<Daydream> findByDateRangeForExport(@Param("start") java.time.OffsetDateTime start, @Param("end") java.time.OffsetDateTime end);
}
