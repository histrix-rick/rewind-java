package com.rewindai.admin.controller;

import com.rewindai.admin.dto.SysRoleRequest;
import com.rewindai.admin.dto.SysRoleResponse;
import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.admin.entity.SysRole;
import com.rewindai.system.admin.enums.RoleStatus;
import com.rewindai.system.admin.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
@Tag(name = "后台管理-角色管理", description = "角色管理接口")
public class AdminRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/list")
    @Operation(summary = "获取角色列表", description = "分页获取角色列表，支持搜索")
    public Result<Page<SysRoleResponse>> getRoleList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "sortOrder") Pageable pageable) {
        Page<SysRole> roles;
        if (status != null && !status.isEmpty()) {
            roles = sysRoleService.findByStatus(RoleStatus.fromCode(status), pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            roles = sysRoleService.searchRoles(keyword, pageable);
        } else {
            roles = sysRoleService.findAll(pageable);
        }
        Page<SysRoleResponse> responses = roles.map(role -> {
            List<Long> permissionIds = sysRoleService.getPermissionIdsByRoleId(role.getId());
            return SysRoleResponse.fromWithPermissions(role, permissionIds);
        });
        return Result.success(responses);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有启用角色", description = "获取所有启用的角色列表")
    public Result<List<SysRoleResponse>> getAllActiveRoles() {
        List<SysRole> roles = sysRoleService.findAllActiveOrderBySortOrder();
        List<SysRoleResponse> responses = roles.stream()
                .map(SysRoleResponse::from)
                .toList();
        return Result.success(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详情")
    public Result<SysRoleResponse> getRoleById(@PathVariable Long id) {
        return sysRoleService.findById(id)
                .map(role -> {
                    List<Long> permissionIds = sysRoleService.getPermissionIdsByRoleId(id);
                    return Result.success(SysRoleResponse.fromWithPermissions(role, permissionIds));
                })
                .orElse(Result.notFound("角色不存在"));
    }

    @PostMapping("/create")
    @Operation(summary = "创建角色", description = "创建新的角色")
    public Result<SysRoleResponse> createRole(@Valid @RequestBody SysRoleRequest request) {
        log.info("创建角色请求: roleName={}, roleCode={}", request.getRoleName(), request.getRoleCode());

        if (sysRoleService.existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "角色编码已存在");
        }

        if (sysRoleService.existsByRoleName(request.getRoleName())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "角色名称已存在");
        }

        SysRole role = SysRole.builder()
                .roleName(request.getRoleName())
                .roleCode(request.getRoleCode())
                .description(request.getDescription())
                .sortOrder(request.getSortOrder())
                .status(request.getStatus() != null ? RoleStatus.fromCode(request.getStatus()) : RoleStatus.ACTIVE)
                .build();

        SysRole savedRole = sysRoleService.create(role);

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            sysRoleService.assignPermissions(savedRole.getId(), request.getPermissionIds());
        }

        List<Long> permissionIds = sysRoleService.getPermissionIdsByRoleId(savedRole.getId());
        log.info("角色 {} 创建成功", request.getRoleName());

        return Result.success(SysRoleResponse.fromWithPermissions(savedRole, permissionIds));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色", description = "更新角色信息")
    public Result<SysRoleResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody SysRoleRequest request) {
        log.info("更新角色请求: roleId={}, roleName={}", id, request.getRoleName());

        return sysRoleService.findById(id)
                .map(role -> {
                    if (!role.getRoleCode().equals(request.getRoleCode())) {
                        if (sysRoleService.existsByRoleCode(request.getRoleCode())) {
                            throw new BusinessException(ErrorCode.BAD_REQUEST, "角色编码已存在");
                        }
                    }
                    if (!role.getRoleName().equals(request.getRoleName())) {
                        if (sysRoleService.existsByRoleName(request.getRoleName())) {
                            throw new BusinessException(ErrorCode.BAD_REQUEST, "角色名称已存在");
                        }
                    }

                    role.setRoleName(request.getRoleName());
                    role.setRoleCode(request.getRoleCode());
                    role.setDescription(request.getDescription());
                    role.setSortOrder(request.getSortOrder());
                    if (request.getStatus() != null) {
                        role.setStatus(RoleStatus.fromCode(request.getStatus()));
                    }

                    SysRole updatedRole = sysRoleService.update(role);

                    if (request.getPermissionIds() != null) {
                        sysRoleService.assignPermissions(updatedRole.getId(), request.getPermissionIds());
                    }

                    List<Long> permissionIds = sysRoleService.getPermissionIdsByRoleId(updatedRole.getId());
                    log.info("角色 {} 更新成功", request.getRoleName());

                    return Result.success(SysRoleResponse.fromWithPermissions(updatedRole, permissionIds));
                })
                .orElse(Result.notFound("角色不存在"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "删除指定角色")
    public Result<Void> deleteRole(@PathVariable Long id) {
        if (sysRoleService.hasAdmins(id)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "该角色下还有管理员，无法删除");
        }

        if (sysRoleService.findById(id).isPresent()) {
            sysRoleService.delete(id);
            log.info("后台删除角色: roleId={}", id);
            return Result.success();
        }
        return Result.notFound("角色不存在");
    }

    @PutMapping("/{id}/permissions")
    @Operation(summary = "分配角色权限", description = "为角色分配权限")
    public Result<Void> assignPermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        if (sysRoleService.findById(id).isEmpty()) {
            return Result.notFound("角色不存在");
        }
        sysRoleService.assignPermissions(id, permissionIds);
        log.info("角色 {} 权限分配成功", id);
        return Result.success();
    }
}
