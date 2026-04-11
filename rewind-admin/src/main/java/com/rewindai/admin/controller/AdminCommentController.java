package com.rewindai.admin.controller;

import com.rewindai.admin.dto.AdminCommentResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.common.core.util.CsvExportUtil;
import com.rewindai.system.daydream.entity.DreamComment;
import com.rewindai.system.daydream.repository.DreamCommentRepository;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 后台管理 - 评论管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/comment")
@RequiredArgsConstructor
@Tag(name = "后台管理-评论管理", description = "后台管理系统评论管理接口")
public class AdminCommentController {

    private final DreamCommentRepository dreamCommentRepository;
    private final UserRepository userRepository;

    @GetMapping("/list")
    @Operation(summary = "获取评论列表", description = "分页获取评论列表，支持搜索")
    public Result<Page<AdminCommentResponse>> getCommentList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isDeleted,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        Page<DreamComment> comments;
        if (isDeleted != null) {
            comments = dreamCommentRepository.searchCommentsByStatus(isDeleted, keyword, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            comments = dreamCommentRepository.searchComments(keyword, pageable);
        } else {
            comments = dreamCommentRepository.findAll(pageable);
        }

        // 批量获取用户信息
        var userIds = comments.getContent().stream()
                .map(DreamComment::getUserId)
                .collect(Collectors.toSet());
        var users = userRepository.findAllByIdIn(userIds);
        var userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 转换为响应DTO并填充用户信息
        Page<AdminCommentResponse> responsePage = comments.map(comment -> {
            var response = AdminCommentResponse.fromEntity(comment);
            var user = userMap.get(comment.getUserId());
            if (user != null) {
                response.setUserNickname(user.getNickname());
                response.setUserAvatar(user.getAvatarUrl());
            }
            return response;
        });

        return Result.success(responsePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取评论详情", description = "根据ID获取评论详情")
    public Result<AdminCommentResponse> getCommentById(@PathVariable UUID id) {
        var commentOpt = dreamCommentRepository.findById(id);
        if (commentOpt.isEmpty()) {
            return Result.notFound("评论不存在");
        }
        var comment = commentOpt.get();
        var response = AdminCommentResponse.fromEntity(comment);
        // 填充用户信息
        userRepository.findById(comment.getUserId()).ifPresent(user -> {
            response.setUserNickname(user.getNickname());
            response.setUserAvatar(user.getAvatarUrl());
        });
        return Result.success(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论", description = "软删除指定评论")
    public Result<Void> deleteComment(@PathVariable UUID id) {
        var commentOpt = dreamCommentRepository.findById(id);
        if (commentOpt.isEmpty()) {
            return Result.notFound("评论不存在");
        }
        var comment = commentOpt.get();
        comment.setIsDeleted(true);
        dreamCommentRepository.save(comment);
        log.info("后台删除评论: commentId={}", id);
        return Result.success();
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "恢复评论", description = "恢复已删除的评论")
    public Result<Void> restoreComment(@PathVariable UUID id) {
        var commentOpt = dreamCommentRepository.findById(id);
        if (commentOpt.isEmpty()) {
            return Result.notFound("评论不存在");
        }
        var comment = commentOpt.get();
        comment.setIsDeleted(false);
        dreamCommentRepository.save(comment);
        log.info("后台恢复评论: commentId={}", id);
        return Result.success();
    }

    @GetMapping("/stats")
    @Operation(summary = "获取评论统计", description = "获取评论总数、正常评论数、已删除评论数")
    public Result<Map<String, Long>> getCommentStats() {
        long totalCount = dreamCommentRepository.count();
        long deletedCount = dreamCommentRepository.findByIsDeleted(true, Pageable.unpaged()).getTotalElements();
        long normalCount = totalCount - deletedCount;

        return Result.success(Map.of(
                "totalCount", totalCount,
                "normalCount", normalCount,
                "deletedCount", deletedCount
        ));
    }

    @GetMapping("/export")
    @Operation(summary = "导出评论数据", description = "导出指定时间范围内的评论数据为CSV")
    public ResponseEntity<byte[]> exportComments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) {

        List<DreamComment> comments = dreamCommentRepository.findByDateRangeForExport(startTime, endTime);

        // 批量获取用户信息
        var userIds = comments.stream()
                .map(DreamComment::getUserId)
                .collect(Collectors.toSet());
        var users = userRepository.findAllByIdIn(userIds);
        var userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        String[] headers = {
            "评论ID", "用户ID", "用户昵称", "梦境ID", "评论内容", "是否删除",
            "点赞数", "创建时间", "更新时间"
        };

        return CsvExportUtil.export("comments", headers, comments, c -> {
            var user = userMap.get(c.getUserId());
            return new String[]{
                c.getId() != null ? c.getId().toString() : "",
                c.getUserId() != null ? c.getUserId().toString() : "",
                user != null ? CsvExportUtil.escape(user.getNickname()) : "",
                c.getDreamId() != null ? c.getDreamId().toString() : "",
                CsvExportUtil.escape(c.getContent()),
                c.getIsDeleted() != null ? c.getIsDeleted().toString() : "",
                c.getLikeCount() != null ? c.getLikeCount().toString() : "0",
                CsvExportUtil.formatDateTime(c.getCreatedAt()),
                CsvExportUtil.formatDateTime(c.getUpdatedAt())
            };
        });
    }
}
