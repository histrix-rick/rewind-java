package com.rewindai.admin.controller;

import com.rewindai.admin.dto.AdminDreamRewardResponse;
import com.rewindai.admin.dto.AdminUserFollowResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.DreamReward;
import com.rewindai.system.daydream.entity.UserFollow;
import com.rewindai.system.daydream.repository.DaydreamRepository;
import com.rewindai.system.daydream.repository.DreamRewardRepository;
import com.rewindai.system.daydream.repository.UserFollowRepository;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 后台管理 - 社交互动管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/social")
@RequiredArgsConstructor
@Tag(name = "后台管理-社交互动管理", description = "后台管理系统社交互动管理接口")
public class AdminSocialController {

    private final UserFollowRepository userFollowRepository;
    private final DreamRewardRepository dreamRewardRepository;
    private final UserRepository userRepository;
    private final DaydreamRepository daydreamRepository;

    // ========== 用户关注关系管理 ==========

    @GetMapping("/follows/list")
    @Operation(summary = "获取关注关系列表", description = "分页获取用户关注关系列表")
    public Result<Page<AdminUserFollowResponse>> getFollowList(
            @RequestParam(required = false) UUID followerId,
            @RequestParam(required = false) UUID followingId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserFollow> follows;
        if (followerId != null) {
            follows = userFollowRepository.findByFollowerId(followerId, pageable);
        } else if (followingId != null) {
            follows = userFollowRepository.findByFollowingId(followingId, pageable);
        } else {
            follows = userFollowRepository.findAll(pageable);
        }

        // 批量获取用户信息
        var userIds = follows.getContent().stream()
                .flatMap(f -> java.util.stream.Stream.of(f.getFollowerId(), f.getFollowingId()))
                .collect(Collectors.toSet());
        var users = userRepository.findAllByIdIn(userIds);
        var userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Page<AdminUserFollowResponse> responsePage = follows.map(follow -> {
            var response = AdminUserFollowResponse.builder()
                    .id(follow.getId())
                    .followerId(follow.getFollowerId())
                    .followingId(follow.getFollowingId())
                    .createdAt(follow.getCreatedAt())
                    .build();

            var follower = userMap.get(follow.getFollowerId());
            if (follower != null) {
                response.setFollowerNickname(follower.getNickname());
                response.setFollowerAvatar(follower.getAvatarUrl());
            }

            var following = userMap.get(follow.getFollowingId());
            if (following != null) {
                response.setFollowingNickname(following.getNickname());
                response.setFollowingAvatar(following.getAvatarUrl());
            }

            return response;
        });

        return Result.success(responsePage);
    }

    @DeleteMapping("/follows/{id}")
    @Operation(summary = "解除关注关系", description = "后台管理员解除用户关注关系")
    public Result<Void> deleteFollow(@PathVariable UUID id) {
        var followOpt = userFollowRepository.findById(id);
        if (followOpt.isEmpty()) {
            return Result.notFound("关注关系不存在");
        }
        userFollowRepository.delete(followOpt.get());
        log.info("后台解除关注关系: id={}", id);
        return Result.success();
    }

    @GetMapping("/follows/stats")
    @Operation(summary = "获取关注关系统计", description = "获取关注关系总数等统计")
    public Result<Map<String, Object>> getFollowStats() {
        long totalCount = userFollowRepository.countAll();
        return Result.success(Map.of("totalCount", totalCount));
    }

    // ========== 打赏记录管理 ==========

    @GetMapping("/rewards/list")
    @Operation(summary = "获取打赏记录列表", description = "分页获取梦境打赏记录列表")
    public Result<Page<AdminDreamRewardResponse>> getRewardList(
            @RequestParam(required = false) UUID dreamId,
            @RequestParam(required = false) UUID senderId,
            @RequestParam(required = false) UUID receiverId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<DreamReward> rewards;
        if (dreamId != null) {
            rewards = dreamRewardRepository.findByDreamId(dreamId, pageable);
        } else if (senderId != null) {
            rewards = dreamRewardRepository.findBySenderId(senderId, pageable);
        } else if (receiverId != null) {
            rewards = dreamRewardRepository.findByReceiverId(receiverId, pageable);
        } else {
            rewards = dreamRewardRepository.findAll(pageable);
        }

        // 批量获取用户和梦境信息
        var userIds = rewards.getContent().stream()
                .flatMap(r -> java.util.stream.Stream.of(r.getSenderId(), r.getReceiverId()))
                .collect(Collectors.toSet());
        var dreamIds = rewards.getContent().stream()
                .map(DreamReward::getDreamId)
                .collect(Collectors.toSet());

        var users = userRepository.findAllByIdIn(userIds);
        var userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        var dreams = daydreamRepository.findAllById(dreamIds);
        var dreamMap = dreams.stream()
                .collect(Collectors.toMap(Daydream::getId, d -> d));

        Page<AdminDreamRewardResponse> responsePage = rewards.map(reward -> {
            var response = AdminDreamRewardResponse.builder()
                    .id(reward.getId())
                    .dreamId(reward.getDreamId())
                    .senderId(reward.getSenderId())
                    .receiverId(reward.getReceiverId())
                    .amount(reward.getAmount())
                    .message(reward.getMessage())
                    .createdAt(reward.getCreatedAt())
                    .build();

            var sender = userMap.get(reward.getSenderId());
            if (sender != null) {
                response.setSenderNickname(sender.getNickname());
                response.setSenderAvatar(sender.getAvatarUrl());
            }

            var receiver = userMap.get(reward.getReceiverId());
            if (receiver != null) {
                response.setReceiverNickname(receiver.getNickname());
                response.setReceiverAvatar(receiver.getAvatarUrl());
            }

            var dream = dreamMap.get(reward.getDreamId());
            if (dream != null) {
                response.setDreamTitle(dream.getTitle());
            }

            return response;
        });

        return Result.success(responsePage);
    }

    @GetMapping("/rewards/{id}")
    @Operation(summary = "获取打赏记录详情", description = "根据ID获取打赏记录详情")
    public Result<AdminDreamRewardResponse> getRewardById(@PathVariable UUID id) {
        var rewardOpt = dreamRewardRepository.findById(id);
        if (rewardOpt.isEmpty()) {
            return Result.notFound("打赏记录不存在");
        }
        var reward = rewardOpt.get();

        var response = AdminDreamRewardResponse.builder()
                .id(reward.getId())
                .dreamId(reward.getDreamId())
                .senderId(reward.getSenderId())
                .receiverId(reward.getReceiverId())
                .amount(reward.getAmount())
                .message(reward.getMessage())
                .createdAt(reward.getCreatedAt())
                .build();

        // 填充用户信息
        userRepository.findById(reward.getSenderId()).ifPresent(sender -> {
            response.setSenderNickname(sender.getNickname());
            response.setSenderAvatar(sender.getAvatarUrl());
        });
        userRepository.findById(reward.getReceiverId()).ifPresent(receiver -> {
            response.setReceiverNickname(receiver.getNickname());
            response.setReceiverAvatar(receiver.getAvatarUrl());
        });

        // 填充梦境信息
        daydreamRepository.findById(reward.getDreamId()).ifPresent(dream -> {
            response.setDreamTitle(dream.getTitle());
        });

        return Result.success(response);
    }

    @GetMapping("/rewards/stats")
    @Operation(summary = "获取打赏统计", description = "获取打赏记录总数、总金额等统计")
    public Result<Map<String, Object>> getRewardStats() {
        long totalCount = dreamRewardRepository.countAll();
        var totalAmount = dreamRewardRepository.sumAllAmount();
        return Result.success(Map.of(
                "totalCount", totalCount,
                "totalAmount", totalAmount
        ));
    }
}
