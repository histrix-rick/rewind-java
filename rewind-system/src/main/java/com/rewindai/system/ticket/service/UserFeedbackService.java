package com.rewindai.system.ticket.service;

import com.rewindai.system.ticket.entity.FeedbackCategory;
import com.rewindai.system.ticket.entity.UserFeedback;
import com.rewindai.system.ticket.enums.FeedbackStatus;
import com.rewindai.system.ticket.repository.FeedbackCategoryRepository;
import com.rewindai.system.ticket.repository.UserFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户反馈 Service
 *
 * @author Rewind.ai Team
 */
@Service
@RequiredArgsConstructor
public class UserFeedbackService {

    private final UserFeedbackRepository feedbackRepository;
    private final FeedbackCategoryRepository categoryRepository;

    public Optional<UserFeedback> findById(Long id) {
        return feedbackRepository.findById(id);
    }

    public Page<UserFeedback> findAll(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }

    public Page<UserFeedback> findByStatus(FeedbackStatus status, Pageable pageable) {
        return feedbackRepository.findByStatus(status, pageable);
    }

    public Page<UserFeedback> findByCategory(String category, Pageable pageable) {
        return feedbackRepository.findByCategory(category, pageable);
    }

    public List<UserFeedback> findByUserId(UUID userId) {
        return feedbackRepository.findByUserId(userId);
    }

    public Page<UserFeedback> searchFeedbacks(String keyword, Pageable pageable) {
        return feedbackRepository.searchFeedbacks(keyword, pageable);
    }

    @Transactional
    public UserFeedback create(UserFeedback feedback) {
        feedback.setStatus(FeedbackStatus.SUBMITTED);
        // 向后兼容：设置旧的category字段和contactInfo字段
        if (feedback.getCategory() == null && feedback.getCategoryId() != null) {
            feedback.setCategory(String.valueOf(feedback.getCategoryId()));
        }
        if (feedback.getContactInfo() == null && feedback.getContact() != null) {
            feedback.setContactInfo(feedback.getContact());
        }
        if (feedback.getTitle() == null) {
            feedback.setTitle("用户反馈");
        }
        return feedbackRepository.save(feedback);
    }

    @Transactional
    public UserFeedback createFeedback(UserFeedback feedback) {
        return create(feedback);
    }

    @Transactional
    public UserFeedback updateFeedback(UserFeedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Transactional
    public UserFeedback handleFeedback(Long feedbackId, Long handlerId, String handlerName,
                                        FeedbackStatus status, String handleNote) {
        return feedbackRepository.findById(feedbackId).map(feedback -> {
            feedback.setHandlerId(handlerId);
            feedback.setHandlerName(handlerName);
            feedback.setStatus(status);
            feedback.setHandleNote(handleNote);
            feedback.setHandleTime(LocalDateTime.now());
            return feedbackRepository.save(feedback);
        }).orElseThrow(() -> new IllegalArgumentException("反馈不存在"));
    }

    @Transactional
    public UserFeedback updateStatus(Long feedbackId, FeedbackStatus status) {
        return feedbackRepository.findById(feedbackId).map(feedback -> {
            feedback.setStatus(status);
            return feedbackRepository.save(feedback);
        }).orElseThrow(() -> new IllegalArgumentException("反馈不存在"));
    }

    public long countByStatus(FeedbackStatus status) {
        return feedbackRepository.countByStatus(status);
    }

    public List<FeedbackCategory> getAllCategories() {
        return categoryRepository.findByIsEnabledTrueOrderBySortOrderAsc();
    }

    public Optional<FeedbackCategory> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public FeedbackCategory createCategory(FeedbackCategory category) {
        if (categoryRepository.existsByCode(category.getCode())) {
            throw new IllegalArgumentException("分类编码已存在");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public FeedbackCategory updateCategory(FeedbackCategory category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
