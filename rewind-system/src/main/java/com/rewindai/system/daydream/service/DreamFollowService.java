package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.DreamFollow;
import com.rewindai.system.daydream.repository.DaydreamRepository;
import com.rewindai.system.daydream.repository.DreamFollowRepository;
import com.rewindai.system.notification.service.NotificationService;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 梦境关注 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DreamFollowService {

    private final DreamFollowRepository dreamFollowRepository;
    private final DaydreamRepository daydreamRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    /**
     * 关注梦境
     */
    @Transactional
    public DreamFollow follow(UUID userId, UUID dreamId) {
        // 验证梦境存在且公开
        Daydream daydream = daydreamRepository.findById(dreamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "梦境不存在"));

        if (!Boolean.TRUE.equals(daydream.getIsPublic())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只能关注公开梦境");
        }

        // 不能关注自己的梦境
        if (daydream.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不能关注自己的梦境");
        }

        if (dreamFollowRepository.existsByUserIdAndDreamId(userId, dreamId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "已经关注该梦境");
        }

        DreamFollow follow = DreamFollow.builder()
                .userId(userId)
                .dreamId(dreamId)
                .build();

        DreamFollow savedFollow = dreamFollowRepository.save(follow);

        // 发送关注通知（异步处理，避免失败影响主流程）
        try {
            // 获取关注者昵称
            String followerNickname = userService.findById(userId)
                    .map(User::getNickname)
                    .orElse(null);
            notificationService.createDreamFollowNotification(daydream.getUserId(), userId, followerNickname, dreamId, daydream.getTitle());
        } catch (Exception e) {
            log.error("发送梦境关注通知失败", e);
        }

        log.info("梦境关注成功: userId={}, dreamId={}", userId, dreamId);
        return savedFollow;
    }

    /**
     * 取消关注梦境
     */
    @Transactional
    public void unfollow(UUID userId, UUID dreamId) {
        dreamFollowRepository.deleteByUserIdAndDreamId(userId, dreamId);
        log.info("取消关注梦境: userId={}, dreamId={}", userId, dreamId);
    }

    /**
     * 检查是否已关注
     */
    public boolean isFollowing(UUID userId, UUID dreamId) {
        return dreamFollowRepository.existsByUserIdAndDreamId(userId, dreamId);
    }

    /**
     * 获取用户关注的梦境ID列表
     */
    public List<UUID> getFollowingDreamIds(UUID userId) {
        return dreamFollowRepository.findDreamIdsByUserId(userId);
    }

    /**
     * 获取关注梦境的用户ID列表
     */
    public List<UUID> getFollowerUserIds(UUID dreamId) {
        return dreamFollowRepository.findUserIdsByDreamId(dreamId);
    }

    /**
     * 获取梦境关注数量
     */
    public long getFollowerCount(UUID dreamId) {
        return dreamFollowRepository.countByDreamId(dreamId);
    }

    /**
     * 获取用户关注梦境数量
     */
    public long getFollowingCount(UUID userId) {
        return dreamFollowRepository.countByUserId(userId);
    }

    /**
     * 解除该梦境的所有关注（删除梦境时调用）
     */
    @Transactional
    public void unfollowAllForDream(UUID dreamId) {
        dreamFollowRepository.deleteAllByDreamId(dreamId);
        log.info("解除梦境所有关注: dreamId={}", dreamId);
    }
}
