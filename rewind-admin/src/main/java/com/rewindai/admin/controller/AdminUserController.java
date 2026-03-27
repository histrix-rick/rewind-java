package com.rewindai.admin.controller;

import com.rewindai.common.core.result.Result;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.enums.UserStatus;
import com.rewindai.system.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/list")
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持搜索")
    public Result<Page<User>> getUserList(
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
        return Result.success(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情")
    public Result<User> getUserById(@PathVariable UUID id) {
        return userRepository.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("用户不存在"));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "启用/禁用用户")
    public Result<User> updateUserStatus(@PathVariable UUID id, @RequestParam UserStatus status) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setStatus(status);
                    userRepository.save(user);
                    return Result.success(user);
                })
                .orElse(Result.notFound("用户不存在"));
    }
}
