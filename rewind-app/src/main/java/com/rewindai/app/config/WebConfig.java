package com.rewindai.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置 - 本地存储文件访问
 *
 * @author Rewind.ai Team
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.local.base-path:./uploads}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置本地存储文件访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + basePath + "/");
    }
}
