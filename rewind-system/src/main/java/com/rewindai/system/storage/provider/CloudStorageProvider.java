package com.rewindai.system.storage.provider;

import com.rewindai.system.storage.entity.StorageConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 云存储提供者接口
 *
 * @author Rewind.ai Team
 */
public interface CloudStorageProvider {

    /**
     * 上传文件
     *
     * @param config 存储配置
     * @param file 文件
     * @param filePath 文件路径（包含文件名）
     * @return 访问URL
     */
    String uploadFile(StorageConfig config, MultipartFile file, String filePath) throws IOException;

    /**
     * 删除文件
     *
     * @param config 存储配置
     * @param filePath 文件路径
     */
    void deleteFile(StorageConfig config, String filePath);
}

