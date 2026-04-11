package com.rewindai.system.admin.repository;

import com.rewindai.system.admin.entity.SysAdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理员-角色关联 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface SysAdminRoleRepository extends JpaRepository<SysAdminRole, Long> {

    List<SysAdminRole> findByAdminId(Integer adminId);

    List<SysAdminRole> findByRoleId(Long roleId);

    boolean existsByAdminIdAndRoleId(Integer adminId, Long roleId);

    @Modifying
    @Transactional
    void deleteByAdminId(Integer adminId);

    @Modifying
    @Transactional
    void deleteByRoleId(Long roleId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SysAdminRole ar WHERE ar.adminId = :adminId AND ar.roleId = :roleId")
    void deleteByAdminIdAndRoleId(@Param("adminId") Integer adminId, @Param("roleId") Long roleId);

    @Query("SELECT ar.roleId FROM SysAdminRole ar WHERE ar.adminId = :adminId")
    List<Long> findRoleIdsByAdminId(@Param("adminId") Integer adminId);

    @Query("SELECT ar.adminId FROM SysAdminRole ar WHERE ar.roleId = :roleId")
    List<Integer> findAdminIdsByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT COUNT(ar) FROM SysAdminRole ar WHERE ar.roleId = :roleId")
    long countByRoleId(@Param("roleId") Long roleId);
}
