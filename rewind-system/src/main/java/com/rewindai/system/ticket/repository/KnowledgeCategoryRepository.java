package com.rewindai.system.ticket.repository;

import com.rewindai.system.ticket.entity.KnowledgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 知识库分类 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface KnowledgeCategoryRepository extends JpaRepository<KnowledgeCategory, Long> {

    Optional<KnowledgeCategory> findByCode(String code);

    List<KnowledgeCategory> findByIsEnabledTrueOrderBySortOrderAsc();

    boolean existsByCode(String code);
}
