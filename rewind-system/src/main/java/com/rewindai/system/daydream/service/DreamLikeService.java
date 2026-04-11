package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.DreamLike;
import com.rewindai.system.daydream.repository.DaydreamRepository;
import com.rewindai.system.daydream.repository.DreamLikeRepository;
import com.rewindai.system.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 梦境点赞 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DreamLikeService {

    private final DreamLikeRepository dreamLikeRepository;
    private final DaydreamRepository daydreamRepository;
    private final NotificationService notificationService;

    /**
     * 检查用户是否已点赞
     */
    public boolean hasLiked(UUID dreamId, UUID userId) {
        return dreamLikeRepository.existsByDreamIdAndUserId(dreamId, userId);
    }

    /**
     * 获取点赞数
     */
    public long getLikeCount(UUID dreamId) {
        return dreamLikeRepository.countByDreamId(dreamId);
    }

    /**
     * 点赞梦境
     */
    @Transactional
    public void likeDream(UUID dreamId, UUID userId) {
        Daydream daydream = daydreamRepository.findById(dreamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (hasLiked(dreamId, userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "已经点赞过了");
        }

        DreamLike like = DreamLike.builder()
                .dreamId(dreamId)
                .userId(userId)
                .build();
        dreamLikeRepository.save(like);

        // 更新点赞数
        daydream.setLikeCount(daydream.getLikeCount() + 1);
        daydreamRepository.save(daydream);

        // 创建通知（失败不影响主流程）
        try {
            if (!daydream.getUserId().equals(userId)) {
                notificationService.createDreamLikeNotification(
                        daydream.getUserId(),
                        userId,
                        dreamId,
                        daydream.getTitle()
                );
            }
        } catch (Exception e) {
            log.warn("创建点赞通知失败，但点赞已保存: dreamId={}, error={}", dreamId, e.getMessage());
        }

        log.info("点赞成功: dreamId={}, userId={}", dreamId, userId);
    }

    /**
     * 取消点赞
     */
    @Transactional
    public void unlikeDream(UUID dreamId, UUID userId) {
        Daydream daydream = daydreamRepository.findById(dreamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (!hasLiked(dreamId, userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "还没有点赞");
        }

        dreamLikeRepository.deleteByDreamIdAndUserId(dreamId, userId);

        // 更新点赞数
        if (daydream.getLikeCount() > 0) {
            daydream.setLikeCount(daydream.getLikeCount() - 1);
            daydreamRepository.save(daydream);
        }

        log.info("取消点赞成功: dreamId={}, userId={}", dreamId, userId);
    }
}
