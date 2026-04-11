package com.rewindai.system.ticket.repository;

import com.rewindai.system.ticket.entity.KnowledgeBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 知识库 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {

    Page<KnowledgeBase> findByCategory(String category, Pageable pageable);

    Page<KnowledgeBase> findByIsPublishedTrue(Pageable pageable);

    List<KnowledgeBase> findByCategoryAndIsPublishedTrueOrderBySortOrderAsc(String category);

    @Query("SELECT k FROM KnowledgeBase k WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "k.title LIKE %:keyword% OR " +
           "k.content LIKE %:keyword% OR " +
           "k.summary LIKE %:keyword%)")
    Page<KnowledgeBase> searchKnowledge(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT k FROM KnowledgeBase k WHERE k.isPublished = true AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "k.title LIKE %:keyword% OR " +
           "k.content LIKE %:keyword% OR " +
           "k.summary LIKE %:keyword%)")
    Page<KnowledgeBase> searchPublishedKnowledge(@Param("keyword") String keyword, Pageable pageable);
}
