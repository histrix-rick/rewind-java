package com.rewindai.admin.controller;

import com.rewindai.admin.dto.AdminResetPasswordRequest;
import com.rewindai.admin.dto.AdminResponse;
import com.rewindai.admin.dto.AdminUpdateRequest;
import com.rewindai.admin.dto.CreateAdminRequest;
import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.admin.entity.SysAdmin;
import com.rewindai.system.admin.enums.AdminStatus;
import com.rewindai.system.admin.repository.SysAdminRepository;
import com.rewindai.system.admin.service.SysAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    private final SysAdminRepository sysAdminRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "123456";

    @GetMapping("/list")
    @Operation(summary = "获取管理员列表", description = "分页获取管理员列表，支持搜索")
    public Result<Page<AdminResponse>> getAdminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) AdminStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<SysAdmin> admins;
        if (status != null) {
            admins = sysAdminRepository.findByStatus(status, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            admins = sysAdminRepository.searchAdmins(keyword, pageable);
        } else {
            admins = sysAdminRepository.findAll(pageable);
        }
        Page<AdminResponse> responses = admins.map(admin -> {
            List<Long> roleIds = sysAdminService.getRoleIdsByAdminId(admin.getId());
            return AdminResponse.fromWithRoles(admin, roleIds);
        });
        return Result.success(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取管理员详情", description = "根据ID获取管理员详情")
    public Result<AdminResponse> getAdminById(@PathVariable Integer id) {
        return sysAdminRepository.findById(id)
                .map(admin -> {
                    List<Long> roleIds = sysAdminService.getRoleIdsByAdminId(admin.getId());
                    return Result.success(AdminResponse.fromWithRoles(admin, roleIds));
                })
                .orElse(Result.notFound("管理员不存在"));
    }

    @PostMapping("/create")
    @Operation(summary = "创建管理员", description = "创建新的管理员账号，默认密码为123456")
    public Result<AdminResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request,
                                               Authentication authentication) {
        log.info("创建管理员请求: username={}, email={}", request.getUsername(), request.getEmail());

        Integer createdByAdminId = ((Number) authentication.getPrincipal()).intValue();

        if (sysAdminService.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "用户名已存在");
        }

        if (sysAdminService.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "邮箱已存在");
        }

        SysAdmin admin = SysAdmin.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                .email(request.getEmail())
                .nickname(request.getRealName())
                .status(AdminStatus.PENDING_CHANGE)
                .isDefaultPassword(true)
                .createdByAdminId(createdByAdminId)
                .build();

        SysAdmin savedAdmin = sysAdminService.save(admin);
        log.info("管理员 {} 创建成功，默认密码: {}", request.getUsername(), DEFAULT_PASSWORD);

        return Result.success(AdminResponse.from(savedAdmin));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新管理员", description = "更新管理员信息")
    public Result<AdminResponse> updateAdmin(
            @PathVariable Integer id,
            @Valid @RequestBody AdminUpdateRequest request) {
        log.info("更新管理员请求: adminId={}, username={}", id, request.getUsername());

        return sysAdminRepository.findById(id)
                .map(admin -> {
                    if (!admin.getUsername().equals(request.getUsername())) {
                        if (sysAdminService.existsByUsername(request.getUsername())) {
                            throw new BusinessException(ErrorCode.BAD_REQUEST, "用户名已存在");
                        }
                    }
                    if (request.getEmail() != null && !request.getEmail().equals(admin.getEmail())) {
                        if (sysAdminService.existsByEmail(request.getEmail())) {
                            throw new BusinessException(ErrorCode.BAD_REQUEST, "邮箱已存在");
                        }
                    }

                    admin.setUsername(request.getUsername());
                    admin.setNickname(request.getNickname());
                    admin.setAvatar(request.getAvatar());
                    admin.setPhoneNumber(request.getPhoneNumber());
                    admin.setEmail(request.getEmail());
                    if (request.getStatus() != null) {
                        admin.setStatus(AdminStatus.fromCode(Integer.parseInt(request.getStatus())));
                    }

                    SysAdmin updatedAdmin = sysAdminService.save(admin);

                    if (request.getRoleIds() != null) {
                        sysAdminService.assignRoles(updatedAdmin.getId(), request.getRoleIds());
                    }

                    List<Long> roleIds = sysAdminService.getRoleIdsByAdminId(updatedAdmin.getId());
                    log.info("管理员 {} 更新成功", request.getUsername());

                    return Result.success(AdminResponse.fromWithRoles(updatedAdmin, roleIds));
                })
                .orElse(Result.notFound("管理员不存在"));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新管理员状态", description = "启用/禁用管理员")
    public Result<AdminResponse> updateAdminStatus(
            @PathVariable Integer id,
            @RequestParam AdminStatus status) {
        return sysAdminRepository.findById(id)
                .map(admin -> {
                    AdminStatus oldStatus = admin.getStatus();
                    admin.setStatus(status);
                    sysAdminRepository.save(admin);
                    log.info("后台更新管理员状态: adminId={}, oldStatus={}, newStatus={}",
                            id, oldStatus, status);
                    return Result.success(AdminResponse.from(admin));
                })
                .orElse(Result.notFound("管理员不存在"));
    }

    @PutMapping("/{id}/reset-password")
    @Operation(summary = "重置管理员密码", description = "重置管理员密码")
    public Result<Void> resetAdminPassword(
            @PathVariable Integer id,
            @Valid @RequestBody AdminResetPasswordRequest request) {
        if (sysAdminRepository.findById(id).isEmpty()) {
            return Result.notFound("管理员不存在");
        }
        sysAdminService.updatePassword(id, passwordEncoder.encode(request.getNewPassword()));
        log.info("后台重置管理员密码: adminId={}", id);
        return Result.success();
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "分配管理员角色", description = "为管理员分配角色")
    public Result<Void> assignRoles(
            @PathVariable Integer id,
            @RequestBody List<Long> roleIds) {
        if (sysAdminRepository.findById(id).isEmpty()) {
            return Result.notFound("管理员不存在");
        }
        sysAdminService.assignRoles(id, roleIds);
        log.info("管理员 {} 角色分配成功", id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除管理员", description = "删除指定管理员")
    public Result<Void> deleteAdmin(@PathVariable Integer id, Authentication authentication) {
        Integer currentAdminId = ((Number) authentication.getPrincipal()).intValue();
        if (currentAdminId.equals(id)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能删除自己的账号");
        }

        if (sysAdminRepository.existsById(id)) {
            sysAdminService.delete(id);
            log.info("后台删除管理员: adminId={}", id);
            return Result.success();
        }
        return Result.notFound("管理员不存在");
    }

    @GetMapping("/stats")
    @Operation(summary = "获取管理员统计", description = "获取管理员总数、在线管理员等统计")
    public Result<Map<String, Long>> getAdminStats() {
        long totalCount = sysAdminRepository.count();
        long normalCount = sysAdminRepository.findByStatus(AdminStatus.NORMAL, Pageable.unpaged()).getTotalElements();
        long disabledCount = sysAdminRepository.findByStatus(AdminStatus.DISABLED, Pageable.unpaged()).getTotalElements();

        return Result.success(Map.of(
                "totalCount", totalCount,
                "normalCount", normalCount,
                "disabledCount", disabledCount
        ));
    }
}
