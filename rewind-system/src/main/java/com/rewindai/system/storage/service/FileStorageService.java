package com.rewindai.system.storage.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.storage.entity.FileRecord;
import com.rewindai.system.storage.entity.StorageConfig;
import com.rewindai.system.storage.enums.StorageProvider;
import com.rewindai.system.storage.provider.CloudStorageProvider;
import com.rewindai.system.storage.provider.CloudStorageProviderFactory;
import com.rewindai.system.storage.repository.FileRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

/**
 * 文件存储 Service - 仅使用配置的存储方式
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileRecordRepository fileRecordRepository;
    private final StorageConfigService storageConfigService;
    private final CloudStorageProviderFactory providerFactory;

    public Optional<FileRecord> findById(UUID id) {
        return fileRecordRepository.findById(id);
    }

    public Page<FileRecord> findAll(Pageable pageable) {
        return fileRecordRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
    }

    public Page<FileRecord> searchFiles(
            String fileName,
            String originalName,
            String fileExt,
            String storageProvider,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            Pageable pageable) {
        return fileRecordRepository.searchFiles(
                fileName, originalName, fileExt, storageProvider, startDate, endDate, pageable);
    }

    @Transactional
    public FileRecord uploadFile(MultipartFile file, UUID uploaderId, String uploaderType) {
        // 获取默认存储配置
        Optional<StorageConfig> configOpt = storageConfigService.getDefaultConfig();
        if (!configOpt.isPresent()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "请先配置存储方式");
        }

        StorageConfig config = configOpt.get();
        String fileUrl;
        String filePath;
        String fileName;
        String originalName = file.getOriginalFilename();
        String fileExt = getFileExtension(originalName);
        fileName = generateFileName(fileExt);

        // 使用配置的存储上传
        try {
            filePath = generateFilePath(config.getPathPrefix(), fileName);
            CloudStorageProvider provider = providerFactory.getProvider(config);
            fileUrl = provider.uploadFile(config, file, filePath);
            log.info("存储上传成功: provider={}, config={}", config.getProvider(), config.getConfigKey());
        } catch (Exception e) {
            log.error("存储上传失败: provider={}, error={}", config.getProvider(), e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件上传失败: " + e.getMessage());
        }

        FileRecord record = FileRecord.builder()
                .fileName(fileName)
                .originalName(originalName)
                .fileExt(fileExt)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .filePath(filePath)
                .fileUrl(fileUrl)
                .uploaderId(uploaderId)
                .uploaderType(uploaderType)
                .configId(config.getId())
                .storageProvider(config.getProvider().name())
                .build();

        FileRecord saved = fileRecordRepository.save(record);
        log.info("文件上传成功: fileId={}, fileName={}, provider={}",
                saved.getId(), saved.getFileName(), config.getProvider());
        return saved;
    }

    @Transactional
    public void deleteFile(UUID id) {
        FileRecord record = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "文件不存在"));

        // 从存储删除文件
        try {
            Optional<StorageConfig> configOpt = storageConfigService.findById(record.getConfigId());
            if (configOpt.isPresent()) {
                StorageConfig config = configOpt.get();
                CloudStorageProvider provider = providerFactory.getProvider(config);
                provider.deleteFile(config, record.getFilePath());
            }
        } catch (Exception e) {
            log.error("从存储删除文件失败", e);
        }

        record.setIsDeleted(true);
        fileRecordRepository.save(record);
        log.info("文件删除成功: fileId={}", id);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    private String generateFileName(String ext) {
        return UUID.randomUUID().toString().replace("-", "") + ext;
    }

    private String generateFilePath(String prefix, String fileName) {
        String datePath = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        if (prefix != null && !prefix.isEmpty()) {
            return prefix + "/" + datePath + "/" + fileName;
        }
        return datePath + "/" + fileName;
    }
}
