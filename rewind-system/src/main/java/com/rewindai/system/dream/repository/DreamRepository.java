package com.rewindai.system.dream.repository;

import com.rewindai.system.dream.entity.Dream;
import com.rewindai.system.dream.enums.DreamPrivacy;
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
 * 梦境 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface DreamRepository extends JpaRepository<Dream, UUID> {

    Optional<Dream> findByIdAndUserId(UUID id, UUID userId);

    Page<Dream> findByUserId(UUID userId, Pageable pageable);

    Page<Dream> findByUserIdAndStatus(UUID userId, DreamStatus status, Pageable pageable);

    List<Dream> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, DreamStatus status);

    Page<Dream> findByIsPublicTrueAndStatusOrderByCreatedAtDesc(DreamStatus status, Pageable pageable);

    Page<Dream> findByIsPublicTrueAndPrivacyAndStatusOrderByCreatedAtDesc(
            DreamPrivacy privacy, DreamStatus status, Pageable pageable);

    @Query("SELECT d FROM Dream d WHERE d.isPublic = true AND d.status = :status " +
           "AND (d.title LIKE %:keyword% OR d.content LIKE %:keyword% OR d.tags LIKE %:keyword%)")
    Page<Dream> searchPublicDreams(@Param("keyword") String keyword,
                                    @Param("status") DreamStatus status,
                                    Pageable pageable);

    @Modifying
    @Query("UPDATE Dream d SET d.viewCount = d.viewCount + 1 WHERE d.id = :id")
    void incrementViewCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Dream d SET d.likeCount = d.likeCount + 1 WHERE d.id = :id")
    void incrementLikeCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Dream d SET d.likeCount = d.likeCount - 1 WHERE d.id = :id AND d.likeCount > 0")
    void decrementLikeCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Dream d SET d.shareCount = d.shareCount + 1 WHERE d.id = :id")
    void incrementShareCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Dream d SET d.commentCount = d.commentCount + 1 WHERE d.id = :id")
    void incrementCommentCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Dream d SET d.commentCount = d.commentCount - 1 WHERE d.id = :id AND d.commentCount > 0")
    void decrementCommentCount(@Param("id") UUID id);

    long countByUserId(UUID userId);

    long countByUserIdAndStatus(UUID userId, DreamStatus status);

    /**
     * 后台管理：分页查询所有梦境
     */
    @Override
    Page<Dream> findAll(Pageable pageable);

    /**
     * 后台管理：搜索梦境（标题/内容/标签）
     */
    @Query("SELECT d FROM Dream d WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "d.title LIKE %:keyword% OR " +
           "d.content LIKE %:keyword% OR " +
           "d.tags LIKE %:keyword%)")
    Page<Dream> searchAllDreams(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 后台管理：按状态搜索梦境
     */
    @Query("SELECT d FROM Dream d WHERE " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "d.title LIKE %:keyword% OR " +
           "d.content LIKE %:keyword% OR " +
           "d.tags LIKE %:keyword%)")
    Page<Dream> searchDreamsByStatus(@Param("status") DreamStatus status,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);
}
