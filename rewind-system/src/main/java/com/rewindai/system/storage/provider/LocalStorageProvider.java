package com.rewindai.system.storage.provider;

import com.rewindai.system.storage.entity.StorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地存储提供者（作为fallback）
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Component
public class LocalStorageProvider implements CloudStorageProvider {

    @Value("${app.storage.local.base-path:./uploads}")
    private String basePath;

    @Value("${app.storage.local.base-url:http://localhost:8082}")
    private String baseUrl;

    @Override
    public String uploadFile(StorageConfig config, MultipartFile file, String filePath) throws IOException {
        Path targetPath = Paths.get(basePath, filePath);
        Files.createDirectories(targetPath.getParent());
        Files.copy(file.getInputStream(), targetPath);
        log.info("本地文件上传成功: path={}", targetPath);

        // 返回完整的访问URL
        String fileUrl = baseUrl + "/uploads/" + filePath;
        log.info("返回文件URL: {}", fileUrl);
        return fileUrl;
    }

    @Override
    public void deleteFile(StorageConfig config, String filePath) {
        try {
            Path targetPath = Paths.get(basePath, filePath);
            Files.deleteIfExists(targetPath);
            log.info("本地文件删除成功: path={}", targetPath);
        } catch (IOException e) {
            log.error("删除本地文件失败: path={}", filePath, e);
        }
    }
}
