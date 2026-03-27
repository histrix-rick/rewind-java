package com.rewindai.app.controller;

import com.rewindai.app.dto.UserAttributeResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.entity.UserAttribute;
import com.rewindai.system.user.service.AttributeService;
import com.rewindai.system.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
