package com.rewindai.system.ticket.service;

import com.rewindai.system.ticket.entity.FeedbackCategory;
import com.rewindai.system.ticket.repository.FeedbackCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 反馈分类 Service
 *
 * @author Rewind.ai Team
 */
@Service
@RequiredArgsConstructor
public class FeedbackCategoryService {

    private final FeedbackCategoryRepository feedbackCategoryRepository;

    public Optional<FeedbackCategory> findById(Long id) {
        return feedbackCategoryRepository.findById(id);
    }

    public Optional<FeedbackCategory> findByCode(String code) {
        return feedbackCategoryRepository.findByCode(code);
    }

    public List<FeedbackCategory> findAllActive() {
        return feedbackCategoryRepository.findByIsEnabledTrueOrderBySortOrderAsc();
    }

    public List<FeedbackCategory> findAll() {
        return feedbackCategoryRepository.findAll();
    }

    @Transactional
    public FeedbackCategory create(FeedbackCategory category) {
        if (feedbackCategoryRepository.existsByCode(category.getCode())) {
            throw new IllegalArgumentException("分类编码已存在");
        }
        return feedbackCategoryRepository.save(category);
    }

    @Transactional
    public FeedbackCategory update(FeedbackCategory category) {
        return feedbackCategoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        feedbackCategoryRepository.deleteById(id);
    }
}
