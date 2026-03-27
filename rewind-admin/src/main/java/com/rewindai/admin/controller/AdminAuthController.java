package com.rewindai.admin.controller;

import com.rewindai.common.core.result.Result;
import com.rewindai.common.security.util.JwtUtil;
import com.rewindai.system.admin.entity.SysAdmin;
import com.rewindai.system.admin.service.SysAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理认证控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@Tag(name = "后台管理-认证", description = "后台管理系统认证接口")
public class AdminAuthController {

    private final SysAdminService sysAdminService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理员账号密码登录")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("管理员登录请求: username={}", request.getUsername());

        SysAdmin admin = sysAdminService.findByUsername(request.getUsername())
                .orElse(null);

        if (admin == null) {
            return Result.error("用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            return Result.error("用户名或密码错误");
        }

        // 使用管理员ID生成token
        String token = jwtUtil.generateAdminToken(admin.getId(), admin.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", Map.of(
                "id", admin.getId(),
                "username", admin.getUsername(),
                "nickname", admin.getNickname()
        ));

        // 更新登录时间
        admin.setLastLoginAt(java.time.OffsetDateTime.now());
        sysAdminService.save(admin);

        log.info("管理员 {} 登录成功", request.getUsername());
        return Result.success(data);
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
