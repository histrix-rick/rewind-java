package com.rewindai.admin.controller;

import com.rewindai.admin.dto.SysPermissionResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.admin.entity.SysPermission;
import com.rewindai.system.admin.enums.PermissionModule;
import com.rewindai.system.admin.service.SysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/permission")
@RequiredArgsConstructor
@Tag(name = "后台管理-权限管理", description = "权限管理接口")
public class AdminPermissionController {

    private final SysPermissionService sysPermissionService;

    @GetMapping("/tree")
    @Operation(summary = "获取权限树", description = "获取树形结构的权限列表")
    public Result<List<SysPermissionResponse>> getPermissionTree() {
        List<SysPermission> permissions = sysPermissionService.findAll();
        List<SysPermissionResponse> tree = SysPermissionResponse.buildTree(permissions);
        return Result.success(tree);
    }

    @GetMapping("/list")
    @Operation(summary = "获取权限列表", description = "获取扁平化的权限列表")
    public Result<List<SysPermissionResponse>> getPermissionList() {
        List<SysPermission> permissions = sysPermissionService.findAll();
        List<SysPermissionResponse> responses = permissions.stream()
                .map(SysPermissionResponse::from)
                .toList();
        return Result.success(responses);
    }

    @GetMapping("/module/{module}")
    @Operation(summary = "按模块获取权限", description = "根据模块获取权限列表")
    public Result<List<SysPermissionResponse>> getPermissionsByModule(@PathVariable String module) {
        List<SysPermission> permissions = sysPermissionService.findByModule(PermissionModule.fromCode(module));
        List<SysPermissionResponse> responses = permissions.stream()
                .map(SysPermissionResponse::from)
                .toList();
        return Result.success(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取权限详情", description = "根据ID获取权限详情")
    public Result<SysPermissionResponse> getPermissionById(@PathVariable Long id) {
        return sysPermissionService.findById(id)
                .map(permission -> Result.success(SysPermissionResponse.from(permission)))
                .orElse(Result.notFound("权限不存在"));
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "获取角色权限", description = "根据角色ID获取权限ID列表")
    public Result<List<Long>> getPermissionIdsByRoleId(@PathVariable Long roleId) {
        return Result.success(sysPermissionService.getPermissionIdsByRoleIds(List.of(roleId)));
    }

    @GetMapping("/roles/{roleIds}")
    @Operation(summary = "获取多个角色权限", description = "根据多个角色ID获取权限ID列表")
    public Result<List<Long>> getPermissionIdsByRoleIds(@PathVariable List<Long> roleIds) {
        return Result.success(sysPermissionService.getPermissionIdsByRoleIds(roleIds));
    }
}
