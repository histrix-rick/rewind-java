package com.rewindai.system.admin.service;

import com.rewindai.system.admin.entity.SysPermission;
import com.rewindai.system.admin.enums.PermissionModule;
import com.rewindai.system.admin.enums.PermissionType;
import com.rewindai.system.admin.repository.SysPermissionRepository;
import com.rewindai.system.admin.repository.SysRolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 权限 Service
 *
 * @author Rewind.ai Team
 */
@Service
@RequiredArgsConstructor
public class SysPermissionService {

    private final SysPermissionRepository sysPermissionRepository;
    private final SysRolePermissionRepository sysRolePermissionRepository;

    public Optional<SysPermission> findById(Long id) {
        return sysPermissionRepository.findById(id);
    }

    public Optional<SysPermission> findByPermissionCode(String permissionCode) {
        return sysPermissionRepository.findByPermissionCode(permissionCode);
    }

    public List<SysPermission> findAll() {
        return sysPermissionRepository.findAllOrderBySortOrder();
    }

    public List<SysPermission> findRootPermissions() {
        return sysPermissionRepository.findRootPermissionsOrderBySortOrder();
    }

    public List<SysPermission> findByParentId(Long parentId) {
        return sysPermissionRepository.findByParentId(parentId);
    }

    public List<SysPermission> findByModule(PermissionModule module) {
        return sysPermissionRepository.findByModuleOrderBySortOrder(module);
    }

    public List<SysPermission> findByType(PermissionType type) {
        return sysPermissionRepository.findByPermissionType(type);
    }

    public List<SysPermission> findByModuleAndType(PermissionModule module, PermissionType type) {
        return sysPermissionRepository.findByPermissionModuleAndPermissionType(module, type);
    }

    @Transactional
    public SysPermission create(SysPermission permission) {
        return sysPermissionRepository.save(permission);
    }

    @Transactional
    public SysPermission update(SysPermission permission) {
        return sysPermissionRepository.save(permission);
    }

    @Transactional
    public void delete(Long id) {
        sysRolePermissionRepository.deleteByPermissionId(id);
        List<SysPermission> children = findByParentId(id);
        for (SysPermission child : children) {
            delete(child.getId());
        }
        sysPermissionRepository.deleteById(id);
    }

    public boolean existsByPermissionCode(String permissionCode) {
        return sysPermissionRepository.existsByPermissionCode(permissionCode);
    }

    public boolean hasRoles(Long permissionId) {
        return sysRolePermissionRepository.countByPermissionId(permissionId) > 0;
    }

    public List<Long> getPermissionIdsByRoleIds(List<Long> roleIds) {
        return sysRolePermissionRepository.findPermissionIdsByRoleIds(roleIds);
    }
}
