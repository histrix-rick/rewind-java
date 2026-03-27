package com.rewindai.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Rewind Auth 认证服务启动类
 *
 * @author Rewind.ai Team
 */
@SpringBootApplication(scanBasePackages = {
        "com.rewindai.auth",
        "com.rewindai.system",
        "com.rewindai.common"
})
@EnableJpaRepositories(basePackages = "com.rewindai.system")
@EntityScan(basePackages = "com.rewindai.system")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
