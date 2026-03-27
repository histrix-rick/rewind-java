package com.rewindai.auth.controller;

import com.rewindai.auth.dto.*;
import com.rewindai.auth.service.AuthService;
import com.rewindai.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证接口", description = "用户注册、登录等认证相关接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册（App端）")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request,
                                           HttpServletRequest httpRequest) {
        log.info("用户注册请求: username={}", request.getUsername());
        LoginResponse response = authService.register(request, httpRequest);
        return Result.success(response);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "App端用户登录，支持用户名/手机号/邮箱登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                        HttpServletRequest httpRequest) {
        log.info("用户登录请求: account={}", request.getAccount());
        LoginResponse response = authService.login(request, httpRequest);
        return Result.success(response);
    }

    @PostMapping("/send-code")
    @Operation(summary = "发送验证码", description = "发送邮箱验证码（管理员登录用）")
    public Result<Void> sendVerificationCode(@Valid @RequestBody SendCodeRequest request) {
        log.info("发送验证码请求: email={}", request.getEmail());
        authService.sendVerificationCode(request);
        return Result.success("验证码已发送，请查看邮箱（控制台日志中查看）", null);
    }

    @PostMapping("/admin/login")
    @Operation(summary = "管理员登录", description = "后台管理系统管理员登录（需要验证码）")
    public Result<AdminLoginResponse> adminLogin(@Valid @RequestBody AdminLoginRequest request,
                                                   HttpServletRequest httpRequest) {
        log.info("管理员登录请求: account={}", request.getAccount());
        AdminLoginResponse response = authService.adminLogin(request, httpRequest);
        return Result.success(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "管理员修改密码")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                        Authentication authentication) {
        Long adminId = (Long) authentication.getPrincipal();
        log.info("修改密码请求: adminId={}", adminId);
        authService.changePassword(adminId, request);
        return Result.success("密码修改成功", null);
    }
}
