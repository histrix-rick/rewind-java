package com.rewindai.auth.config;

import com.rewindai.system.admin.entity.SysAdmin;
import com.rewindai.system.admin.enums.AdminStatus;
import com.rewindai.system.admin.service.SysAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 初始化管理员数据
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final SysAdminService sysAdminService;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "123456";
    private static final String DEFAULT_ADMIN_EMAIL = "chenwenqi1991@gmail.com";
    private static final String DEFAULT_ADMIN_NICKNAME = "谌文琦";

    @Override
    public void run(String... args) {
        if (!sysAdminService.existsByUsername(DEFAULT_ADMIN_USERNAME)) {
            SysAdmin admin = SysAdmin.builder()
                    .username(DEFAULT_ADMIN_USERNAME)
                    .passwordHash(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD))
                    .email(DEFAULT_ADMIN_EMAIL)
                    .nickname(DEFAULT_ADMIN_NICKNAME)
                    .status(AdminStatus.PENDING_CHANGE)
                    .isDefaultPassword(true)
                    .build();

            sysAdminService.save(admin);
            log.info("========================================");
            log.info("默认管理员账号已创建:");
            log.info("  用户名: {}", DEFAULT_ADMIN_USERNAME);
            log.info("  密码: {}", DEFAULT_ADMIN_PASSWORD);
            log.info("  邮箱: {}", DEFAULT_ADMIN_EMAIL);
            log.info("  状态: 待首次改密");
            log.info("========================================");
        } else {
            log.info("默认管理员账号已存在，跳过初始化");
        }
    }
}
