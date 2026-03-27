package com.rewindai.system.admin.repository;

import com.rewindai.system.admin.entity.SysAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
