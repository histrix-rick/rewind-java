package com.rewindai.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Rewind App 前端API服务启动类
 *
 * @author Rewind.ai Team
 */
@SpringBootApplication(scanBasePackages = {
        "com.rewindai.app",
        "com.rewindai.system",
        "com.rewindai.common"
})
@EnableJpaRepositories(basePackages = "com.rewindai.system")
@EntityScan(basePackages = "com.rewindai.system")
@EnableCaching
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
