package com.rewindai.app.controller;

import com.rewindai.app.dto.DaydreamResponse;
import com.rewindai.app.dto.UserAttributeResponse;
import com.rewindai.app.dto.UserDetailResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.service.DaydreamService;
import com.rewindai.system.daydream.service.UserFollowService;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.entity.UserAttribute;
import com.rewindai.system.user.service.AttributeService;
import com.rewindai.system.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 前端API - 用户控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "前端API-用户", description = "前端用户相关接口")
public class AppUserController {

    private final UserService userService;
    private final AttributeService attributeService;
    private final UserFollowService userFollowService;
    private final DaydreamService daydreamService;

    @GetMapping("/profile")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的信息")
    public Result<User> getUserProfile(Authentication authentication) {
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);
        return userService.findById(userId)
                .map(Result::success)
                .orElse(Result.error(404, "用户不存在"));
    }

    @GetMapping("/attribute")
    @Operation(summary = "获取用户属性", description = "获取当前登录用户的属性")
    public Result<UserAttributeResponse> getUserAttribute(Authentication authentication) {
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);
        UserAttribute attribute = attributeService.getOrCreateAttribute(userId);
        return Result.success(UserAttributeResponse.from(attribute));
    }

    @GetMapping("/{userId}/detail")
    @Operation(summary = "获取用户详情", description = "获取指定用户的详情信息")
    public Result<UserDetailResponse> getUserDetail(
            @PathVariable UUID userId,
            Authentication authentication) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        long followerCount = userFollowService.getFollowerCount(userId);
        long followingCount = userFollowService.getFollowingCount(userId);

        Boolean isFollowing = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            try {
                UUID currentUserId = UUID.fromString((String) authentication.getPrincipal());
                if (!currentUserId.equals(userId)) {
                    isFollowing = userFollowService.isFollowing(currentUserId, userId);
                }
            } catch (Exception e) {
                // 未登录用户，isFollowing保持null
            }
        }

        return Result.success(UserDetailResponse.from(user, followerCount, followingCount, isFollowing));
    }

    @GetMapping("/{userId}/public-dreams")
    @Operation(summary = "获取用户公开梦境列表", description = "获取指定用户的公开梦境列表")
    public Result<Page<DaydreamResponse>> getUserPublicDreams(
            @PathVariable UUID userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        // 验证用户存在
        userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Page<Daydream> daydreams = daydreamService.getPublicDaydreamsByUserId(userId, pageable);

        return Result.success(daydreams.map(daydream -> {
            var progress = daydreamService.calculateProgress(daydream);
            return DaydreamResponse.from(daydream, progress);
        }));
    }
}
