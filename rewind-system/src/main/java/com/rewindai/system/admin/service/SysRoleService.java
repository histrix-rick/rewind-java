package com.rewindai.system.admin.service;

import com.rewindai.system.admin.entity.SysRole;
import com.rewindai.system.admin.entity.SysRolePermission;
import com.rewindai.system.admin.enums.RoleStatus;
import com.rewindai.system.admin.repository.SysAdminRoleRepository;
import com.rewindai.system.admin.repository.SysRolePermissionRepository;
import com.rewindai.system.admin.repository.SysRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 角色 Service
 *
 * @author Rewind.ai Team
 */
@Service
@RequiredArgsConstructor
public class SysRoleService {

    private final SysRoleRepository sysRoleRepository;
    private final SysAdminRoleRepository sysAdminRoleRepository;
    private final SysRolePermissionRepository sysRolePermissionRepository;

    public Optional<SysRole> findById(Long id) {
        return sysRoleRepository.findById(id);
    }

    public Optional<SysRole> findByRoleCode(String roleCode) {
        return sysRoleRepository.findByRoleCode(roleCode);
    }

    public Optional<SysRole> findByRoleName(String roleName) {
        return sysRoleRepository.findByRoleName(roleName);
    }

    public List<SysRole> findAllActive() {
        return sysRoleRepository.findByStatus(RoleStatus.ACTIVE);
    }

    public List<SysRole> findAllActiveOrderBySortOrder() {
        return sysRoleRepository.findAllActiveOrderBySortOrder();
    }

    public Page<SysRole> findAll(Pageable pageable) {
        return sysRoleRepository.findAll(pageable);
    }

    public Page<SysRole> findByStatus(RoleStatus status, Pageable pageable) {
        return sysRoleRepository.findByStatus(status, pageable);
    }

    public Page<SysRole> searchRoles(String keyword, Pageable pageable) {
        return sysRoleRepository.searchRoles(keyword, pageable);
    }

    @Transactional
    public SysRole create(SysRole role) {
        return sysRoleRepository.save(role);
    }

    @Transactional
    public SysRole update(SysRole role) {
        return sysRoleRepository.save(role);
    }

    @Transactional
    public void delete(Long id) {
        sysRolePermissionRepository.deleteByRoleId(id);
        sysAdminRoleRepository.deleteByRoleId(id);
        sysRoleRepository.deleteById(id);
    }

    public boolean existsByRoleCode(String roleCode) {
        return sysRoleRepository.existsByRoleCode(roleCode);
    }

    public boolean existsByRoleName(String roleName) {
        return sysRoleRepository.existsByRoleName(roleName);
    }

    public boolean hasAdmins(Long roleId) {
        return sysAdminRoleRepository.countByRoleId(roleId) > 0;
    }

    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        sysRolePermissionRepository.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                SysRolePermission rp = SysRolePermission.builder()
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .build();
                sysRolePermissionRepository.save(rp);
            }
        }
    }

    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return sysRolePermissionRepository.findPermissionIdsByRoleId(roleId);
    }
}
