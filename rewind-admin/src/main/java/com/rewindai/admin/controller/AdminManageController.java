package com.rewindai.admin.controller;

import com.rewindai.admin.dto.CreateAdminRequest;
import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.admin.entity.SysAdmin;
import com.rewindai.system.admin.enums.AdminStatus;
import com.rewindai.system.admin.service.SysAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/admin")
@RequiredArgsConstructor
@Tag(name = "后台管理-管理员管理", description = "管理员管理接口")
public class AdminManageController {

    private final SysAdminService sysAdminService;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "123456";

    @PostMapping("/create")
    @Operation(summary = "创建管理员", description = "创建新的管理员账号，默认密码为123456")
    public Result<Void> createAdmin(@Valid @RequestBody CreateAdminRequest request,
                                     Authentication authentication) {
        log.info("创建管理员请求: username={}, email={}", request.getUsername(), request.getEmail());

        // 获取创建者ID
        Integer createdByAdminId = ((Number) authentication.getPrincipal()).intValue();

        // 检查用户名是否已存在
        if (sysAdminService.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (sysAdminService.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "邮箱已存在");
        }

        // 创建管理员
        SysAdmin admin = SysAdmin.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                .email(request.getEmail())
                .nickname(request.getRealName())
                .status(AdminStatus.PENDING_CHANGE)
                .isDefaultPassword(true)
                .createdByAdminId(createdByAdminId)
                .build();

        sysAdminService.save(admin);
        log.info("管理员 {} 创建成功，默认密码: {}", request.getUsername(), DEFAULT_PASSWORD);

        return Result.success("管理员创建成功，默认密码为: " + DEFAULT_PASSWORD, null);
    }
}
