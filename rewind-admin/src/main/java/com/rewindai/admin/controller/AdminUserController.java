package com.rewindai.admin.controller;

import com.rewindai.admin.dto.AdminUserBanRequest;
import com.rewindai.admin.dto.AdminUserResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.common.core.util.CsvExportUtil;
import com.rewindai.system.daydream.repository.DaydreamRepository;
import com.rewindai.system.daydream.repository.UserFollowRepository;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.enums.UserStatus;
import com.rewindai.system.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

/**
 * 后台管理 - 用户管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Tag(name = "后台管理-用户管理", description = "后台管理系统用户管理接口")
public class AdminUserController {

    private final UserRepository userRepository;
    private final DaydreamRepository daydreamRepository;
    private final UserFollowRepository userFollowRepository;

    @GetMapping("/list")
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持搜索")
    public Result<Page<AdminUserResponse>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<User> users;
        if (status != null) {
            users = userRepository.findByStatus(status, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            users = userRepository.searchUsers(keyword, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        // 转换为响应DTO并填充统计数据
        Page<AdminUserResponse> responsePage = users.map(user -> {
            AdminUserResponse response = AdminUserResponse.fromEntity(user);
            populateUserStats(response, user.getId());
            return response;
        });
        return Result.success(responsePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情")
    public Result<AdminUserResponse> getUserById(@PathVariable UUID id) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return Result.notFound("用户不存在");
        }
        var user = userOpt.get();
        AdminUserResponse response = AdminUserResponse.fromEntity(user);
        // 填充统计数据
        populateUserStats(response, user.getId());
        return Result.success(response);
    }

    /**
     * 填充用户统计数据
     */
    private void populateUserStats(AdminUserResponse response, UUID userId) {
        response.setDreamCount(daydreamRepository.countByUserId(userId));
        response.setFollowerCount(userFollowRepository.countByFollowingId(userId));
        response.setFollowingCount(userFollowRepository.countByFollowerId(userId));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "更新用户状态（正常/禁言/封号）")
    public Result<AdminUserResponse> updateUserStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUserBanRequest request) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return Result.notFound("用户不存在");
        }
        var user = userOpt.get();
        UserStatus oldStatus = user.getStatus();
        user.setStatus(request.getStatus());
        userRepository.save(user);
        log.info("后台更新用户状态: userId={}, oldStatus={}, newStatus={}, reason={}",
                id, oldStatus, request.getStatus(), request.getReason());

        AdminUserResponse response = AdminUserResponse.fromEntity(user);
        return Result.success(response);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取用户统计", description = "获取用户总数、新增用户、封禁用户等统计")
    public Result<Map<String, Long>> getUserStats() {
        long totalCount = userRepository.count();
        long bannedCount = userRepository.countByStatus(UserStatus.BANNED);
        long mutedCount = userRepository.countByStatus(UserStatus.MUTED);
        long normalCount = userRepository.countByStatus(UserStatus.NORMAL);

        return Result.success(Map.of(
                "totalCount", totalCount,
                "normalCount", normalCount,
                "mutedCount", mutedCount,
                "bannedCount", bannedCount
        ));
    }

    @GetMapping("/export")
    @Operation(summary = "导出用户数据", description = "导出指定时间范围内的用户数据为CSV")
    public ResponseEntity<byte[]> exportUsers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) {

        List<User> users = userRepository.findByDateRangeForExport(startTime, endTime);

        String[] headers = {
            "用户ID", "用户名", "昵称", "手机号", "邮箱", "真实姓名",
            "性别", "生日", "状态", "注册IP", "注册设备",
            "最后登录时间", "最后登录IP", "注册时间", "更新时间"
        };

        return CsvExportUtil.export("users", headers, users, user -> new String[]{
            user.getId() != null ? user.getId().toString() : "",
            CsvExportUtil.escape(user.getUsername()),
            CsvExportUtil.escape(user.getNickname()),
            CsvExportUtil.escape(user.getPhoneNumber()),
            CsvExportUtil.escape(user.getEmail()),
            CsvExportUtil.escape(user.getRealName()),
            user.getGender() != null ? user.getGender().name() : "",
            user.getBirthDate() != null ? user.getBirthDate().toString() : "",
            user.getStatus() != null ? user.getStatus().name() : "",
            CsvExportUtil.escape(user.getRegisterIp()),
            CsvExportUtil.escape(user.getRegisterDeviceId()),
            CsvExportUtil.formatDateTime(user.getLastLoginTime()),
            CsvExportUtil.escape(user.getLastLoginIp()),
            CsvExportUtil.formatDateTime(user.getCreatedAt()),
            CsvExportUtil.formatDateTime(user.getUpdatedAt())
        });
    }
}
