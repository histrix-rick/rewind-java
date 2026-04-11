package com.rewindai.app.controller;

import com.rewindai.app.dto.DreamCommentRequest;
import com.rewindai.app.dto.DreamCommentResponse;
import com.rewindai.app.dto.LikeStatusResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.DreamComment;
import com.rewindai.system.daydream.service.DaydreamService;
import com.rewindai.system.daydream.service.DreamCommentService;
import com.rewindai.system.daydream.service.DreamLikeService;
import com.rewindai.system.daydream.service.NodeLikeService;
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

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 梦境互动 Controller（点赞、评论）
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Tag(name = "梦境互动", description = "梦境点赞、评论、节点点赞相关接口")
@RestController
@RequestMapping("/api/dream-interactions")
@RequiredArgsConstructor
public class DreamInteractionController {

    private final DreamLikeService dreamLikeService;
    private final NodeLikeService nodeLikeService;
    private final DreamCommentService dreamCommentService;
    private final DaydreamService daydreamService;
    private final UserRepository userRepository;

    // ==================== 梦境点赞 ====================

    @Operation(summary = "获取梦境点赞状态")
    @GetMapping("/dream/{dreamId}/like-status")
    public Result<LikeStatusResponse> getDreamLikeStatus(
            @PathVariable UUID dreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        // 验证可访问
        daydreamService.findAccessibleById(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在或无权限访问"));

        boolean liked = dreamLikeService.hasLiked(dreamId, userId);
        long likeCount = dreamLikeService.getLikeCount(dreamId);
        return Result.success(LikeStatusResponse.builder()
                .liked(liked)
                .likeCount(likeCount)
                .build());
    }

    @Operation(summary = "点赞梦境")
    @PostMapping("/dream/{dreamId}/like")
    public Result<Void> likeDream(
            @PathVariable UUID dreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        // 验证可访问
        daydreamService.findAccessibleById(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在或无权限访问"));

        dreamLikeService.likeDream(dreamId, userId);
        return Result.success();
    }

    @Operation(summary = "取消点赞梦境")
    @DeleteMapping("/dream/{dreamId}/like")
    public Result<Void> unlikeDream(
            @PathVariable UUID dreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        // 验证可访问
        daydreamService.findAccessibleById(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在或无权限访问"));

        dreamLikeService.unlikeDream(dreamId, userId);
        return Result.success();
    }

    // ==================== 节点点赞 ====================

    @Operation(summary = "获取节点点赞状态")
    @GetMapping("/node/{nodeId}/like-status")
    public Result<LikeStatusResponse> getNodeLikeStatus(
            @PathVariable UUID nodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        boolean liked = nodeLikeService.hasLiked(nodeId, userId);
        long likeCount = nodeLikeService.getLikeCount(nodeId);
        return Result.success(LikeStatusResponse.builder()
                .liked(liked)
                .likeCount(likeCount)
                .build());
    }

    @Operation(summary = "点赞节点")
    @PostMapping("/node/{nodeId}/like")
    public Result<Void> likeNode(
            @PathVariable UUID nodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        nodeLikeService.likeNode(nodeId, userId);
        return Result.success();
    }

    @Operation(summary = "取消点赞节点")
    @DeleteMapping("/node/{nodeId}/like")
    public Result<Void> unlikeNode(
            @PathVariable UUID nodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        nodeLikeService.unlikeNode(nodeId, userId);
        return Result.success();
    }

    // ==================== 梦境评论 ====================

    @Operation(summary = "获取梦境评论列表")
    @GetMapping("/dream/{dreamId}/comments")
    public Result<Page<DreamCommentResponse>> getComments(
            @PathVariable UUID dreamId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        // 验证可访问
        daydreamService.findAccessibleById(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在或无权限访问"));

        Page<DreamComment> comments = dreamCommentService.getComments(dreamId, pageable);

        // 批量查询用户信息
        var userIds = comments.getContent().stream()
                .map(DreamComment::getUserId)
                .collect(Collectors.toSet());
        Map<UUID, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 转换为响应DTO，填充用户信息
        return Result.success(comments.map(comment -> {
            User user = userMap.get(comment.getUserId());
            return user != null ? DreamCommentResponse.from(comment, user) : DreamCommentResponse.from(comment);
        }));
    }

    @Operation(summary = "添加梦境评论")
    @PostMapping("/dream/{dreamId}/comments")
    public Result<DreamCommentResponse> addComment(
            @PathVariable UUID dreamId,
            @Valid @RequestBody DreamCommentRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        // 验证可访问
        daydreamService.findAccessibleById(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在或无权限访问"));

        DreamComment comment = dreamCommentService.addComment(
                dreamId, userId, request.getContent(), request.getParentCommentId());

        // 查询当前用户信息并填充
        User user = userRepository.findById(userId).orElse(null);
        DreamCommentResponse response = user != null ?
                DreamCommentResponse.from(comment, user) :
                DreamCommentResponse.from(comment);

        return Result.success(response);
    }

    @Operation(summary = "删除梦境评论")
    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(
            @PathVariable UUID commentId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        dreamCommentService.deleteComment(commentId, userId);
        return Result.success();
    }
}
