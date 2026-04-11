package com.rewindai.system.admin.repository;

import com.rewindai.system.admin.entity.SysRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色-权限关联 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission, Long> {

    List<SysRolePermission> findByRoleId(Long roleId);

    List<SysRolePermission> findByPermissionId(Long permissionId);

    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

    @Modifying
    @Transactional
    void deleteByRoleId(Long roleId);

    @Modifying
    @Transactional
    void deleteByPermissionId(Long permissionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SysRolePermission rp WHERE rp.roleId = :roleId AND rp.permissionId = :permissionId")
    void deleteByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    @Query("SELECT rp.permissionId FROM SysRolePermission rp WHERE rp.roleId = :roleId")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT rp.roleId FROM SysRolePermission rp WHERE rp.permissionId = :permissionId")
    List<Long> findRoleIdsByPermissionId(@Param("permissionId") Long permissionId);

    @Query("SELECT COUNT(rp) FROM SysRolePermission rp WHERE rp.permissionId = :permissionId")
    long countByPermissionId(@Param("permissionId") Long permissionId);

    @Query("SELECT DISTINCT rp.permissionId FROM SysRolePermission rp WHERE rp.roleId IN :roleIds")
    List<Long> findPermissionIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
}
