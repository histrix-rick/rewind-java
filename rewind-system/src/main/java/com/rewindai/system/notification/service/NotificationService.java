package com.rewindai.system.notification.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.notification.entity.Notification;
import com.rewindai.system.notification.enums.NotificationType;
import com.rewindai.system.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 通知 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 获取用户通知列表
     */
    public Page<Notification> getUserNotifications(UUID userId, Pageable pageable, Boolean unreadOnly) {
        if (Boolean.TRUE.equals(unreadOnly)) {
            return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);
        }
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * 获取未读通知数量
     */
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 标记通知为已读
     */
    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        int updated = notificationRepository.markAsRead(notificationId, userId);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "通知不存在或无权限操作");
        }
        log.info("通知已标记为已读: notificationId={}", notificationId);
    }

    /**
     * 标记所有通知为已读
     */
    @Transactional
    public void markAllAsRead(UUID userId) {
        int count = notificationRepository.markAllAsRead(userId);
        log.info("已标记所有通知为已读: userId={}, count={}", userId, count);
    }

    /**
     * 删除通知
     */
    @Transactional
    public void deleteNotification(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "通知不存在"));

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "无权限操作此通知");
        }

        notificationRepository.delete(notification);
        log.info("通知已删除: notificationId={}", notificationId);
    }

    /**
     * 删除用户所有通知
     */
    @Transactional
    public void deleteAllNotifications(UUID userId) {
        int count = notificationRepository.deleteAllByUserId(userId);
        log.info("已删除所有通知: userId={}, count={}", userId, count);
    }

    /**
     * 创建梦境点赞通知
     */
    @Transactional
    public Notification createDreamLikeNotification(UUID toUserId, UUID fromUserId, UUID dreamId, String dreamTitle) {
        log.info("开始创建梦境点赞通知: toUserId={}, fromUserId={}, dreamId={}", toUserId, fromUserId, dreamId);
        String title = "收到新的点赞";
        String content = "有人点赞了你的梦境《" + dreamTitle + "》";
        Notification notification = createNotification(toUserId, NotificationType.DREAM_LIKE, title, content, dreamId, "DREAM");
        log.info("梦境点赞通知创建完成: notificationId={}", notification.getId());
        return notification;
    }

    /**
     * 创建节点点赞通知
     */
    @Transactional
    public Notification createNodeLikeNotification(UUID toUserId, UUID fromUserId, UUID nodeId, UUID dreamId) {
        String title = "收到新的点赞";
        String content = "有人点赞了你的时间轴节点";
        return createNotification(toUserId, NotificationType.NODE_LIKE, title, content, nodeId, "NODE");
    }

    /**
     * 创建梦境评论通知
     */
    @Transactional
    public Notification createDreamCommentNotification(UUID toUserId, UUID fromUserId, UUID dreamId, String dreamTitle, String commentContent) {
        String title = "收到新的评论";
        String content = "有人评论了你的梦境《" + dreamTitle + "》: " + truncateContent(commentContent, 50);
        return createNotification(toUserId, NotificationType.DREAM_COMMENT, title, content, dreamId, "DREAM");
    }

    /**
     * 创建评论回复通知
     */
    @Transactional
    public Notification createCommentReplyNotification(UUID toUserId, UUID fromUserId, UUID commentId, String replyContent) {
        String title = "收到新的回复";
        String content = "有人回复了你的评论: " + truncateContent(replyContent, 50);
        return createNotification(toUserId, NotificationType.COMMENT_REPLY, title, content, commentId, "COMMENT");
    }

    /**
     * 创建系统通知
     */
    @Transactional
    public Notification createSystemNotification(UUID userId, String title, String content) {
        return createNotification(userId, NotificationType.SYSTEM, title, content, null, null);
    }

    /**
     * 创建梦境打赏通知
     */
    @Transactional
    public Notification createDreamRewardNotification(UUID toUserId, UUID fromUserId, UUID dreamId, java.math.BigDecimal amount) {
        log.info("开始创建梦境打赏通知: toUserId={}, fromUserId={}, dreamId={}, amount={}", toUserId, fromUserId, dreamId, amount);
        String title = "收到新的打赏";
        String content = "有人打赏了你 " + amount + " 梦想币";
        Notification notification = createNotification(toUserId, NotificationType.DREAM_REWARD, title, content, dreamId, "DREAM");
        log.info("梦境打赏通知创建完成: notificationId={}", notification.getId());
        return notification;
    }

    /**
     * 创建用户关注通知
     */
    @Transactional
    public Notification createUserFollowNotification(UUID toUserId, UUID fromUserId, String fromUserNickname) {
        log.info("开始创建用户关注通知: toUserId={}, fromUserId={}", toUserId, fromUserId);
        String title = "收到新的关注";
        String content = (fromUserNickname != null ? fromUserNickname : "有人") + " 关注了你";
        Notification notification = createNotification(toUserId, NotificationType.USER_FOLLOW, title, content, fromUserId, "USER");
        log.info("用户关注通知创建完成: notificationId={}", notification.getId());
        return notification;
    }

    /**
     * 创建梦境关注通知
     */
    @Transactional
    public Notification createDreamFollowNotification(UUID toUserId, UUID fromUserId, String fromUserNickname, UUID dreamId, String dreamTitle) {
        log.info("开始创建梦境关注通知: toUserId={}, fromUserId={}, dreamId={}", toUserId, fromUserId, dreamId);
        String title = "梦境收到新关注";
        String content = (fromUserNickname != null ? fromUserNickname : "有人") + " 关注了你的梦境《" + dreamTitle + "》";
        Notification notification = createNotification(toUserId, NotificationType.DREAM_FOLLOW, title, content, dreamId, "DREAM");
        log.info("梦境关注通知创建完成: notificationId={}", notification.getId());
        return notification;
    }

    /**
     * 通用创建通知方法（使用独立事务，失败不影响主流程）
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Notification createNotification(UUID userId, NotificationType type, String title, String content,
                                            UUID relatedId, String relatedType) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .content(content)
                .relatedId(relatedId)
                .relatedType(relatedType)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("通知创建成功: notificationId={}, type={}, userId={}", saved.getId(), type, userId);
        return saved;
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
