package com.rewindai.system.admin.repository;

import com.rewindai.system.admin.entity.SysPermission;
import com.rewindai.system.admin.enums.PermissionModule;
import com.rewindai.system.admin.enums.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface SysPermissionRepository extends JpaRepository<SysPermission, Long> {

    Optional<SysPermission> findByPermissionCode(String permissionCode);

    boolean existsByPermissionCode(String permissionCode);

    List<SysPermission> findByParentId(Long parentId);

    List<SysPermission> findByPermissionModule(PermissionModule module);

    List<SysPermission> findByPermissionType(PermissionType type);

    List<SysPermission> findByPermissionModuleAndPermissionType(PermissionModule module, PermissionType type);

    @Query("SELECT p FROM SysPermission p WHERE p.parentId IS NULL ORDER BY p.sortOrder ASC, p.id ASC")
    List<SysPermission> findRootPermissionsOrderBySortOrder();

    @Query("SELECT p FROM SysPermission p ORDER BY p.sortOrder ASC, p.id ASC")
    List<SysPermission> findAllOrderBySortOrder();

    @Query("SELECT p FROM SysPermission p WHERE p.permissionModule = :module ORDER BY p.sortOrder ASC, p.id ASC")
    List<SysPermission> findByModuleOrderBySortOrder(@Param("module") PermissionModule module);
}
