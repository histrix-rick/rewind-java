package com.rewindai.system.user.repository;

import com.rewindai.system.user.entity.UserLoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 用户登录日志 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface UserLoginLogRepository extends JpaRepository<UserLoginLog, Long> {
}
