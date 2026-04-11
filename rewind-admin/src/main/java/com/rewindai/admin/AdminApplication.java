package com.rewindai.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Rewind Admin 后台管理系统启动类
 *
 * @author Rewind.ai Team
 */
@SpringBootApplication(scanBasePackages = {
        "com.rewindai.admin",
        "com.rewindai.system",
        "com.rewindai.common"
})
@EnableJpaRepositories(basePackages = {"com.rewindai.system"})
@EntityScan(basePackages = {"com.rewindai.system"})
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
