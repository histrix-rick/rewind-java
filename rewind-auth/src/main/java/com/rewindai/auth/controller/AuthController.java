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

    @PostMapping("/realname/verify")
    @Operation(summary = "实名认证单独验证", description = "在注册流程中单独验证实名认证信息")
    public Result<RealNameVerifyResponse> verifyRealName(@Valid @RequestBody RealNameVerifyRequest request) {
        log.info("实名认证验证请求: realName={}", request.getRealName());
        RealNameVerifyResponse response = authService.verifyRealName(request);
        return Result.success(response);
    }

    @PostMapping("/sms/send-code")
    @Operation(summary = "发送短信验证码", description = "发送手机验证码，支持登录、注册、实名认证等场景")
    public Result<Void> sendSmsVerificationCode(@Valid @RequestBody SendSmsCodeRequest request) {
        log.info("发送短信验证码请求: phone={}, templateType={}", request.getPhone(), request.getTemplateType());
        authService.sendSmsVerificationCode(request);
        return Result.success("验证码已发送", null);
    }

    @PostMapping("/sms/login")
    @Operation(summary = "短信验证码登录", description = "使用手机号和验证码登录")
    public Result<LoginResponse> smsLogin(@Valid @RequestBody SmsLoginRequest request,
                                           HttpServletRequest httpRequest) {
        log.info("短信验证码登录请求: phone={}", request.getPhone());
        LoginResponse response = authService.smsLogin(request, httpRequest);
        return Result.success(response);
    }

    @PostMapping("/user/change-password")
    @Operation(summary = "用户修改密码", description = "用户通过旧密码修改密码")
    public Result<Void> changeUserPassword(@Valid @RequestBody ChangeUserPasswordRequest request,
                                              Authentication authentication) {
        String userIdStr = (String) authentication.getPrincipal();
        java.util.UUID userId = java.util.UUID.fromString(userIdStr);
        log.info("用户修改密码请求: userId={}", userId);
        authService.changeUserPassword(userId, request);
        return Result.success("密码修改成功，请重新登录", null);
    }

    @PostMapping("/user/reset-password")
    @Operation(summary = "用户重置密码", description = "用户通过短信验证码重置密码")
    public Result<Void> resetUserPassword(@Valid @RequestBody ResetUserPasswordRequest request) {
        log.info("用户重置密码请求: phone={}", request.getPhone());
        authService.resetUserPassword(request);
        return Result.success("密码重置成功，请使用新密码登录", null);
    }
}
