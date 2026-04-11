package com.rewindai.system.ticket.service;

import com.rewindai.system.ticket.entity.KnowledgeBase;
import com.rewindai.system.ticket.entity.KnowledgeCategory;
import com.rewindai.system.ticket.repository.KnowledgeBaseRepository;
import com.rewindai.system.ticket.repository.KnowledgeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 知识库 Service
 *
 * @author Rewind.ai Team
 */
@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {

    private final KnowledgeBaseRepository knowledgeRepository;
    private final KnowledgeCategoryRepository categoryRepository;

    public Optional<KnowledgeBase> findById(Long id) {
        return knowledgeRepository.findById(id);
    }

    public Page<KnowledgeBase> findAll(Pageable pageable) {
        return knowledgeRepository.findAll(pageable);
    }

    public Page<KnowledgeBase> findByCategory(String category, Pageable pageable) {
        return knowledgeRepository.findByCategory(category, pageable);
    }

    public Page<KnowledgeBase> findPublished(Pageable pageable) {
        return knowledgeRepository.findByIsPublishedTrue(pageable);
    }

    public List<KnowledgeBase> findPublishedByCategory(String category) {
        return knowledgeRepository.findByCategoryAndIsPublishedTrueOrderBySortOrderAsc(category);
    }

    public Page<KnowledgeBase> searchKnowledge(String keyword, Pageable pageable) {
        return knowledgeRepository.searchKnowledge(keyword, pageable);
    }

    public Page<KnowledgeBase> searchPublishedKnowledge(String keyword, Pageable pageable) {
        return knowledgeRepository.searchPublishedKnowledge(keyword, pageable);
    }

    @Transactional
    public KnowledgeBase createKnowledge(KnowledgeBase knowledge) {
        if (knowledge.getViewCount() == null) {
            knowledge.setViewCount(0);
        }
        if (knowledge.getSortOrder() == null) {
            knowledge.setSortOrder(0);
        }
        if (knowledge.getIsPublished() == null) {
            knowledge.setIsPublished(true);
        }
        return knowledgeRepository.save(knowledge);
    }

    @Transactional
    public KnowledgeBase updateKnowledge(KnowledgeBase knowledge) {
        return knowledgeRepository.save(knowledge);
    }

    @Transactional
    public KnowledgeBase incrementViewCount(Long id) {
        return knowledgeRepository.findById(id).map(knowledge -> {
            knowledge.setViewCount(knowledge.getViewCount() + 1);
            return knowledgeRepository.save(knowledge);
        }).orElse(null);
    }

    @Transactional
    public void deleteKnowledge(Long id) {
        knowledgeRepository.deleteById(id);
    }

    public List<KnowledgeCategory> getAllCategories() {
        return categoryRepository.findByIsEnabledTrueOrderBySortOrderAsc();
    }

    public Optional<KnowledgeCategory> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public KnowledgeCategory createCategory(KnowledgeCategory category) {
        if (categoryRepository.existsByCode(category.getCode())) {
            throw new IllegalArgumentException("分类编码已存在");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public KnowledgeCategory updateCategory(KnowledgeCategory category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
