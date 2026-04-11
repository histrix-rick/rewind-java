package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.DreamComment;
import com.rewindai.system.daydream.repository.DaydreamRepository;
import com.rewindai.system.daydream.repository.DreamCommentRepository;
import com.rewindai.system.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 梦境评论 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DreamCommentService {

    private final DreamCommentRepository dreamCommentRepository;
    private final DaydreamRepository daydreamRepository;
    private final NotificationService notificationService;

    /**
     * 获取梦境评论列表
     */
    public Page<DreamComment> getComments(UUID dreamId, Pageable pageable) {
        return dreamCommentRepository.findByDreamIdAndIsDeletedFalseOrderByCreatedAtDesc(dreamId, pageable);
    }

    /**
     * 获取评论的回复列表
     */
    public List<DreamComment> getReplies(UUID parentCommentId) {
        return dreamCommentRepository.findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(parentCommentId);
    }

    /**
     * 获取评论数
     */
    public long getCommentCount(UUID dreamId) {
        return dreamCommentRepository.countByDreamIdAndIsDeletedFalse(dreamId);
    }

    /**
     * 添加评论
     */
    @Transactional
    public DreamComment addComment(UUID dreamId, UUID userId, String content, UUID parentCommentId) {
        Daydream daydream = daydreamRepository.findById(dreamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "评论内容不能为空");
        }

        DreamComment comment = DreamComment.builder()
                .dreamId(dreamId)
                .userId(userId)
                .content(content.trim())
                .parentCommentId(parentCommentId)
                .build();
        DreamComment saved = dreamCommentRepository.save(comment);

        // 更新评论数
        daydream.setCommentCount(daydream.getCommentCount() + 1);
        daydreamRepository.save(daydream);

        // 创建通知（失败不影响主流程）
        try {
            if (parentCommentId != null) {
                // 回复评论，通知原评论作者
                DreamComment parentComment = dreamCommentRepository.findById(parentCommentId).orElse(null);
                if (parentComment != null && !parentComment.getUserId().equals(userId)) {
                    notificationService.createCommentReplyNotification(
                            parentComment.getUserId(),
                            userId,
                            parentCommentId,
                            content
                    );
                }
            } else if (!daydream.getUserId().equals(userId)) {
                // 评论梦境，通知梦境作者
                notificationService.createDreamCommentNotification(
                        daydream.getUserId(),
                        userId,
                        dreamId,
                        daydream.getTitle(),
                        content
                );
            }
        } catch (Exception e) {
            log.warn("创建评论通知失败，但评论已保存: dreamId={}, error={}", dreamId, e.getMessage());
        }

        log.info("评论添加成功: dreamId={}, commentId={}", dreamId, saved.getId());
        return saved;
    }

    /**
     * 删除评论（软删除）
     */
    @Transactional
    public void deleteComment(UUID commentId, UUID userId) {
        DreamComment comment = dreamCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "评论不存在"));

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "只能删除自己的评论");
        }

        comment.setIsDeleted(true);
        dreamCommentRepository.save(comment);

        // 更新评论数
        Daydream daydream = daydreamRepository.findById(comment.getDreamId()).orElse(null);
        if (daydream != null && daydream.getCommentCount() > 0) {
            daydream.setCommentCount(daydream.getCommentCount() - 1);
            daydreamRepository.save(daydream);
        }

        log.info("评论删除成功: commentId={}", commentId);
    }
}
