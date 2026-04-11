package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.UserFollow;
import com.rewindai.system.daydream.repository.UserFollowRepository;
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
 * 用户关注 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserFollowService {

    private final UserFollowRepository userFollowRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * 关注用户
     */
    @Transactional
    public UserFollow follow(UUID followerId, UUID followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不能关注自己");
        }

        if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "已经关注该用户");
        }

        UserFollow follow = UserFollow.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();

        UserFollow savedFollow = userFollowRepository.save(follow);

        // 发送关注通知（异步处理，避免失败影响主流程）
        try {
            // 获取关注者昵称
            String followerNickname = userService.findById(followerId)
                    .map(User::getNickname)
                    .orElse(null);
            notificationService.createUserFollowNotification(followingId, followerId, followerNickname);
        } catch (Exception e) {
            log.error("发送关注通知失败", e);
        }

        return savedFollow;
    }

    /**
     * 取消关注
     */
    @Transactional
    public void unfollow(UUID followerId, UUID followingId) {
        userFollowRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    /**
     * 检查是否已关注
     */
    public boolean isFollowing(UUID followerId, UUID followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    /**
     * 获取用户关注的ID列表
     */
    public List<UUID> getFollowingIds(UUID followerId) {
        return userFollowRepository.findFollowingIdsByFollowerId(followerId);
    }

    /**
     * 获取关注用户的ID列表
     */
    public List<UUID> getFollowerIds(UUID followingId) {
        return userFollowRepository.findFollowerIdsByFollowingId(followingId);
    }

    /**
     * 获取关注数量
     */
    public long getFollowingCount(UUID followerId) {
        return userFollowRepository.countByFollowerId(followerId);
    }

    /**
     * 获取粉丝数量
     */
    public long getFollowerCount(UUID followingId) {
        return userFollowRepository.countByFollowingId(followingId);
    }
}
