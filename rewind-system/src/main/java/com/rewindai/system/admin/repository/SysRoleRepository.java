package com.rewindai.system.admin.repository;

import com.rewindai.system.admin.entity.SysRole;
import com.rewindai.system.admin.enums.RoleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {

    Optional<SysRole> findByRoleCode(String roleCode);

    Optional<SysRole> findByRoleName(String roleName);

    boolean existsByRoleCode(String roleCode);

    boolean existsByRoleName(String roleName);

    List<SysRole> findByStatus(RoleStatus status);

    Page<SysRole> findByStatus(RoleStatus status, Pageable pageable);

    @Query("SELECT r FROM SysRole r WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "r.roleName LIKE %:keyword% OR " +
           "r.roleCode LIKE %:keyword% OR " +
           "r.description LIKE %:keyword%)")
    Page<SysRole> searchRoles(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT r FROM SysRole r WHERE r.status = 'ACTIVE' ORDER BY r.sortOrder ASC, r.id ASC")
    List<SysRole> findAllActiveOrderBySortOrder();
}
