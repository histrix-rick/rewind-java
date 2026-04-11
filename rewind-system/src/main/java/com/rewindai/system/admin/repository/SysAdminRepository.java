package com.rewindai.system.admin.repository;

import com.rewindai.system.admin.entity.SysAdmin;
import com.rewindai.system.admin.enums.AdminStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理员 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface SysAdminRepository extends JpaRepository<SysAdmin, Integer> {

    Optional<SysAdmin> findByUsername(String username);

    Optional<SysAdmin> findByPhoneNumber(String phoneNumber);

    Optional<SysAdmin> findByEmail(String email);

    Optional<SysAdmin> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    /**
     * 后台管理：分页查询所有管理员
     */
    @Override
    Page<SysAdmin> findAll(Pageable pageable);

    /**
     * 后台管理：按状态分页查询管理员
     */
    Page<SysAdmin> findByStatus(AdminStatus status, Pageable pageable);

    /**
     * 后台管理：搜索管理员（用户名/昵称/邮箱）
     */
    @Query("SELECT a FROM SysAdmin a WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "a.username LIKE %:keyword% OR " +
           "a.nickname LIKE %:keyword% OR " +
           "a.email LIKE %:keyword%)")
    Page<SysAdmin> searchAdmins(@Param("keyword") String keyword, Pageable pageable);
}
