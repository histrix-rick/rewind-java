package com.rewindai.admin.controller;

import com.rewindai.admin.dto.AdminNotificationResponse;
import com.rewindai.admin.dto.NotificationStatsResponse;
import com.rewindai.admin.dto.SendNotificationRequest;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.notification.entity.Notification;
import com.rewindai.system.notification.enums.NotificationType;
import com.rewindai.system.notification.repository.NotificationRepository;
import com.rewindai.system.notification.service.NotificationService;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 后台管理 - 通知管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/notification")
@RequiredArgsConstructor
@Tag(name = "后台管理-通知管理", description = "后台管理系统通知管理接口")
public class AdminNotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping("/list")
    @Operation(summary = "获取通知列表", description = "分页获取通知列表")
    public Result<Page<AdminNotificationResponse>> getNotificationList(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) Boolean isRead,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Notification> notifications;
        if (userId != null && type != null) {
            notifications = notificationRepository.findByUserIdAndType(userId, type, pageable);
        } else if (userId != null) {
            notifications = notificationRepository.findByUserId(userId, pageable);
        } else if (type != null) {
            notifications = notificationRepository.findByType(type, pageable);
        } else if (isRead != null) {
            notifications = notificationRepository.findByIsRead(isRead, pageable);
        } else {
            notifications = notificationRepository.findAll(pageable);
        }

        // 批量获取用户信息
        Set<UUID> userIds = notifications.getContent().stream()
                .map(Notification::getUserId)
                .collect(Collectors.toSet());
        List<User> users = userRepository.findAllByIdIn(userIds);
        Map<UUID, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Page<AdminNotificationResponse> responsePage = notifications.map(notification -> {
            AdminNotificationResponse response = AdminNotificationResponse.fromEntity(notification);
            User user = userMap.get(notification.getUserId());
            if (user != null) {
                response.setUserNickname(user.getNickname());
            }
            return response;
        });

        return Result.success(responsePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取通知详情", description = "根据ID获取通知详情")
    public Result<AdminNotificationResponse> getNotificationById(@PathVariable UUID id) {
        return notificationRepository.findById(id)
                .map(notification -> {
                    AdminNotificationResponse response = AdminNotificationResponse.fromEntity(notification);
                    userRepository.findById(notification.getUserId()).ifPresent(user -> {
                        response.setUserNickname(user.getNickname());
                    });
                    return Result.success(response);
                })
                .orElse(Result.notFound("通知不存在"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知", description = "删除指定通知")
    public Result<Void> deleteNotification(@PathVariable UUID id) {
        notificationRepository.deleteById(id);
        log.info("后台删除通知: id={}", id);
        return Result.success();
    }

    @PostMapping("/send")
    @Operation(summary = "发送系统通知", description = "向指定用户或所有用户发送系统通知")
    public Result<Void> sendSystemNotification(@Valid @RequestBody SendNotificationRequest request) {
        List<UUID> targetUserIds = request.getUserIds();

        if (targetUserIds == null || targetUserIds.isEmpty()) {
            // 发送给所有用户
            List<User> allUsers = userRepository.findAll();
            for (User user : allUsers) {
                try {
                    notificationService.createSystemNotification(user.getId(), request.getTitle(), request.getContent());
                } catch (Exception e) {
                    log.error("发送系统通知失败: userId={}", user.getId(), e);
                }
            }
            log.info("后台发送系统通知给所有用户: count={}", allUsers.size());
        } else {
            // 发送给指定用户
            for (UUID userId : targetUserIds) {
                try {
                    notificationService.createSystemNotification(userId, request.getTitle(), request.getContent());
                } catch (Exception e) {
                    log.error("发送系统通知失败: userId={}", userId, e);
                }
            }
            log.info("后台发送系统通知给指定用户: count={}", targetUserIds.size());
        }

        return Result.success("通知发送成功", null);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取通知统计", description = "获取通知总数、未读数等统计数据")
    public Result<NotificationStatsResponse> getNotificationStats() {
        long totalCount = notificationRepository.countAll();
        long unreadCount = notificationRepository.countUnread();

        // 按类型统计
        List<Notification> allNotifications = notificationRepository.findAll(Sort.unsorted());
        Map<String, Long> typeStats = new HashMap<>();
        for (Notification notification : allNotifications) {
            String typeName = notification.getType() != null ? notification.getType().getDesc() : "未知";
            typeStats.put(typeName, typeStats.getOrDefault(typeName, 0L) + 1);
        }

        NotificationStatsResponse stats = NotificationStatsResponse.builder()
                .totalCount(totalCount)
                .unreadCount(unreadCount)
                .typeStats(typeStats)
                .build();

        return Result.success(stats);
    }
}
