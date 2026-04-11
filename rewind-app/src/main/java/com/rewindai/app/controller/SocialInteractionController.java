package com.rewindai.app.controller;

import com.rewindai.app.dto.DreamRewardResponse;
import com.rewindai.app.dto.FollowRequest;
import com.rewindai.app.dto.FollowStatusResponse;
import com.rewindai.app.dto.RewardRequest;
import com.rewindai.app.dto.SimpleUserResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.DreamReward;
import com.rewindai.system.daydream.service.DaydreamService;
import com.rewindai.system.daydream.service.DreamFollowService;
import com.rewindai.system.daydream.service.DreamRewardService;
import com.rewindai.system.daydream.service.UserFollowService;
import com.rewindai.system.notification.service.NotificationService;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 社交互动 Controller（关注、打赏）
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Tag(name = "社交互动", description = "关注、打赏相关接口")
@RestController
@RequestMapping("/api/social-interactions")
@RequiredArgsConstructor
public class SocialInteractionController {

    private final UserFollowService userFollowService;
    private final DreamFollowService dreamFollowService;
    private final DreamRewardService dreamRewardService;
    private final DaydreamService daydreamService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // ==================== 用户关注 ====================

    @Operation(summary = "关注用户")
    @PostMapping("/follow")
    public Result<FollowStatusResponse> followUser(
            @Valid @RequestBody FollowRequest request,
            Authentication authentication) {
        UUID followerId = UUID.fromString(authentication.getName());

        userFollowService.follow(followerId, request.getFollowingId());

        // 发送关注通知
        try {
            User follower = userRepository.findById(followerId).orElse(null);
            String followerNickname = follower != null ?
                    (follower.getNickname() != null ? follower.getNickname() : follower.getUsername()) : null;
            notificationService.createUserFollowNotification(request.getFollowingId(), followerId, followerNickname);
        } catch (Exception e) {
            log.error("发送关注通知失败", e);
        }

        return getFollowStatus(request.getFollowingId(), followerId);
    }

    @Operation(summary = "取消关注")
    @DeleteMapping("/follow/{followingId}")
    public Result<FollowStatusResponse> unfollowUser(
            @PathVariable UUID followingId,
            Authentication authentication) {
        UUID followerId = UUID.fromString(authentication.getName());

        userFollowService.unfollow(followerId, followingId);

        return getFollowStatus(followingId, followerId);
    }

    @Operation(summary = "获取关注状态")
    @GetMapping("/follow-status/{userId}")
    public Result<FollowStatusResponse> getFollowStatus(
            @PathVariable UUID userId,
            Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        return getFollowStatus(userId, currentUserId);
    }

    private Result<FollowStatusResponse> getFollowStatus(UUID targetUserId, UUID currentUserId) {
        boolean isFollowing = userFollowService.isFollowing(currentUserId, targetUserId);
        long followerCount = userFollowService.getFollowerCount(targetUserId);
        long followingCount = userFollowService.getFollowingCount(targetUserId);

        return Result.success(FollowStatusResponse.builder()
                .isFollowing(isFollowing)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build());
    }

    @Operation(summary = "获取当前用户关注的用户ID列表")
    @GetMapping("/my-following")
    public Result<java.util.List<UUID>> getMyFollowing(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return Result.success(userFollowService.getFollowingIds(userId));
    }

    @Operation(summary = "获取当前用户的粉丝ID列表")
    @GetMapping("/my-followers")
    public Result<java.util.List<UUID>> getMyFollowers(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return Result.success(userFollowService.getFollowerIds(userId));
    }

    @Operation(summary = "批量获取用户信息")
    @PostMapping("/users/batch")
    public Result<List<SimpleUserResponse>> getUsersBatch(@RequestBody List<UUID> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return Result.success(users.stream().map(SimpleUserResponse::from).toList());
    }

    // ==================== 梦境打赏 ====================

    @Operation(summary = "打赏梦境")
    @PostMapping("/dream/{dreamId}/reward")
    public Result<DreamRewardResponse> rewardDream(
            @PathVariable UUID dreamId,
            @Valid @RequestBody RewardRequest request,
            Authentication authentication) {
        UUID senderId = UUID.fromString(authentication.getName());

        // 验证可访问
        daydreamService.findAccessibleById(dreamId, senderId)
                .orElseThrow(() -> new RuntimeException("梦境不存在或无权限访问"));

        DreamReward reward = dreamRewardService.rewardDream(
                senderId, dreamId, request.getAmount(), request.getMessage());

        // 查询打赏者信息并填充
        User sender = userRepository.findById(senderId).orElse(null);
        DreamRewardResponse response = DreamRewardResponse.from(reward);
        if (sender != null) {
            response.setSenderNickname(sender.getNickname() != null ? sender.getNickname() : sender.getUsername());
        }

        return Result.success(response);
    }

    @Operation(summary = "获取梦境打赏记录")
    @GetMapping("/dream/{dreamId}/rewards")
    public Result<Page<DreamRewardResponse>> getDreamRewards(
            @PathVariable UUID dreamId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        // 验证可访问
        daydreamService.findAccessibleById(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在或无权限访问"));

        Page<DreamReward> rewards = dreamRewardService.getDreamRewards(dreamId, pageable);

        // 批量查询用户信息
        var userIds = rewards.getContent().stream()
                .map(DreamReward::getSenderId)
                .collect(Collectors.toSet());
        Map<UUID, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 转换为响应DTO，填充用户信息
        return Result.success(rewards.map(reward -> {
            User sender = userMap.get(reward.getSenderId());
            DreamRewardResponse response = DreamRewardResponse.from(reward);
            if (sender != null) {
                response.setSenderNickname(sender.getNickname() != null ? sender.getNickname() : sender.getUsername());
            }
            return response;
        }));
    }

    @Operation(summary = "获取梦境打赏统计")
    @GetMapping("/dream/{dreamId}/reward-stats")
    public Result<Map<String, Object>> getDreamRewardStats(
            @PathVariable UUID dreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        // 验证可访问
        daydreamService.findAccessibleById(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在或无权限访问"));

        var totalAmount = dreamRewardService.getTotalRewardAmount(dreamId);
        var rewardCount = dreamRewardService.getRewardCount(dreamId);

        return Result.success(Map.of(
                "totalAmount", totalAmount,
                "rewardCount", rewardCount
        ));
    }

    @Operation(summary = "获取我收到的打赏记录")
    @GetMapping("/my-received-rewards")
    public Result<Page<DreamRewardResponse>> getMyReceivedRewards(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        Page<DreamReward> rewards = dreamRewardService.getReceivedRewards(userId, pageable);

        // 批量查询用户信息
        var userIds = rewards.getContent().stream()
                .map(DreamReward::getSenderId)
                .collect(Collectors.toSet());
        Map<UUID, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 转换为响应DTO，填充用户信息
        return Result.success(rewards.map(reward -> {
            User sender = userMap.get(reward.getSenderId());
            DreamRewardResponse response = DreamRewardResponse.from(reward);
            if (sender != null) {
                response.setSenderNickname(sender.getNickname() != null ? sender.getNickname() : sender.getUsername());
            }
            return response;
        }));
    }

    // ==================== 梦境关注 ====================

    @Operation(summary = "关注梦境")
    @PostMapping("/dream/{dreamId}/follow")
    public Result<Map<String, Object>> followDream(
            @PathVariable UUID dreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        dreamFollowService.follow(userId, dreamId);

        long followerCount = dreamFollowService.getFollowerCount(dreamId);
        return Result.success(Map.of(
                "isFollowing", true,
                "followerCount", followerCount
        ));
    }

    @Operation(summary = "取消关注梦境")
    @DeleteMapping("/dream/{dreamId}/follow")
    public Result<Map<String, Object>> unfollowDream(
            @PathVariable UUID dreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        dreamFollowService.unfollow(userId, dreamId);

        long followerCount = dreamFollowService.getFollowerCount(dreamId);
        return Result.success(Map.of(
                "isFollowing", false,
                "followerCount", followerCount
        ));
    }

    @Operation(summary = "获取梦境关注状态")
    @GetMapping("/dream/{dreamId}/follow-status")
    public Result<Map<String, Object>> getDreamFollowStatus(
            @PathVariable UUID dreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        boolean isFollowing = dreamFollowService.isFollowing(userId, dreamId);
        long followerCount = dreamFollowService.getFollowerCount(dreamId);

        return Result.success(Map.of(
                "isFollowing", isFollowing,
                "followerCount", followerCount
        ));
    }

    @Operation(summary = "获取当前用户关注的梦境ID列表")
    @GetMapping("/my-following-dreams")
    public Result<List<UUID>> getMyFollowingDreams(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return Result.success(dreamFollowService.getFollowingDreamIds(userId));
    }
}
