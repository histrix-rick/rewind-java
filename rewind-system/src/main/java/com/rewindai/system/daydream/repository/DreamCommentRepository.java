package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.DreamComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 梦境评论 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface DreamCommentRepository extends JpaRepository<DreamComment, UUID> {

    Page<DreamComment> findByDreamIdAndIsDeletedFalseOrderByCreatedAtDesc(UUID dreamId, Pageable pageable);

    List<DreamComment> findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(UUID parentCommentId);

    long countByDreamIdAndIsDeletedFalse(UUID dreamId);

    /**
     * 后台管理：分页查询所有评论
     */
    @Override
    Page<DreamComment> findAll(Pageable pageable);

    /**
     * 后台管理：按删除状态查询评论
     */
    Page<DreamComment> findByIsDeleted(Boolean isDeleted, Pageable pageable);

    /**
     * 后台管理：搜索评论（内容）
     */
    @Query("SELECT c FROM DreamComment c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "c.content LIKE %:keyword%)")
    Page<DreamComment> searchComments(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 后台管理：按删除状态和关键词搜索评论
     */
    @Query("SELECT c FROM DreamComment c WHERE " +
           "(:isDeleted IS NULL OR c.isDeleted = :isDeleted) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "c.content LIKE %:keyword%)")
    Page<DreamComment> searchCommentsByStatus(@Param("isDeleted") Boolean isDeleted,
                                                @Param("keyword") String keyword,
                                                Pageable pageable);

    /**
     * 统计：指定时间之后创建的评论数
     */
    @Query("SELECT COUNT(c) FROM DreamComment c WHERE c.createdAt >= :after")
    long countByCreatedAtAfter(@Param("after") java.time.OffsetDateTime after);

    /**
     * 统计：指定时间范围内创建的评论数
     */
    @Query("SELECT COUNT(c) FROM DreamComment c WHERE c.createdAt >= :start AND c.createdAt < :end")
    long countByCreatedAtBetween(@Param("start") java.time.OffsetDateTime start, @Param("end") java.time.OffsetDateTime end);

    /**
     * 导出：查询指定时间范围内的评论
     */
    @Query("SELECT c FROM DreamComment c WHERE c.createdAt >= :start AND c.createdAt < :end ORDER BY c.createdAt DESC")
    List<DreamComment> findByDateRangeForExport(@Param("start") java.time.OffsetDateTime start, @Param("end") java.time.OffsetDateTime end);
}
