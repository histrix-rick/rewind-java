package com.rewindai.app.controller;

import com.rewindai.app.dto.NotificationResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * 通知 Controller
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Tag(name = "通知管理", description = "通知消息相关接口")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "获取通知列表")
    @GetMapping
    public Result<Page<NotificationResponse>> getNotifications(
            @RequestParam(required = false) Boolean unreadOnly,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Page<NotificationResponse> notifications = notificationService
                .getUserNotifications(userId, pageable, unreadOnly)
                .map(NotificationResponse::from);
        return Result.success(notifications);
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread/count")
    public Result<Map<String, Object>> getUnreadCount(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        long count = notificationService.getUnreadCount(userId);
        return Result.success(Map.of("count", count));
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        notificationService.markAsRead(id, userId);
        return Result.success();
    }

    @Operation(summary = "标记所有通知为已读")
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        notificationService.deleteNotification(id, userId);
        return Result.success();
    }

    @Operation(summary = "删除所有通知")
    @DeleteMapping
    public Result<Void> deleteAllNotifications(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        notificationService.deleteAllNotifications(userId);
        return Result.success();
    }
}
