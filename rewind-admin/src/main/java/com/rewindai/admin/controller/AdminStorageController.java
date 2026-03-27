package com.rewindai.admin.controller;

import com.rewindai.admin.dto.FileRecordResponse;
import com.rewindai.admin.dto.StorageConfigRequest;
import com.rewindai.admin.dto.StorageConfigResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.storage.entity.FileRecord;
import com.rewindai.system.storage.entity.StorageConfig;
import com.rewindai.system.storage.service.FileStorageService;
import com.rewindai.system.storage.service.StorageConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 后台存储管理 Controller
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/storage")
@RequiredArgsConstructor
@Tag(name = "后台文件管理", description = "存储配置和文件管理接口")
public class AdminStorageController {

    private final StorageConfigService storageConfigService;
    private final FileStorageService fileStorageService;

    // ========== 存储配置管理 ==========

    @Operation(summary = "获取所有存储配置")
    @GetMapping("/configs")
    public Result<List<StorageConfigResponse>> getAllConfigs() {
        List<StorageConfig> configs = storageConfigService.findAll();
        return Result.success(configs.stream().map(StorageConfigResponse::from).toList());
    }

    @Operation(summary = "搜索存储配置")
    @GetMapping("/configs/search")
    public Result<List<StorageConfigResponse>> searchConfigs(
            @RequestParam(required = false) String configKey,
            @RequestParam(required = false) String bucketName) {
        List<StorageConfig> configs;
        if (configKey != null && !configKey.isEmpty()) {
            configs = storageConfigService.searchByConfigKey(configKey);
        } else if (bucketName != null && !bucketName.isEmpty()) {
            configs = storageConfigService.searchByBucketName(bucketName);
        } else {
            configs = storageConfigService.findAll();
        }
        return Result.success(configs.stream().map(StorageConfigResponse::from).toList());
    }

    @Operation(summary = "获取存储配置详情")
    @GetMapping("/configs/{id}")
    public Result<StorageConfigResponse> getConfigById(@PathVariable Long id) {
        return storageConfigService.findById(id)
                .map(StorageConfigResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("配置不存在"));
    }

    @Operation(summary = "创建存储配置")
    @PostMapping("/configs")
    public Result<StorageConfigResponse> createConfig(@Valid @RequestBody StorageConfigRequest request) {
        StorageConfig config = StorageConfig.builder()
                .configKey(request.getConfigKey())
                .accessEndpoint(request.getAccessEndpoint())
                .customDomain(request.getCustomDomain())
                .accessKey(request.getAccessKey())
                .secretKey(request.getSecretKey())
                .bucketName(request.getBucketName())
                .pathPrefix(request.getPathPrefix())
                .isHttps(request.getIsHttps() != null ? request.getIsHttps() : false)
                .bucketAccessType(request.getBucketAccessType())
                .region(request.getRegion())
                .provider(request.getProvider())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .remark(request.getRemark())
                .build();

        StorageConfig saved = storageConfigService.createConfig(config);
        return Result.success(StorageConfigResponse.from(saved));
    }

    @Operation(summary = "更新存储配置")
    @PutMapping("/configs/{id}")
    public Result<StorageConfigResponse> updateConfig(
            @PathVariable Long id,
            @Valid @RequestBody StorageConfigRequest request) {
        StorageConfig update = StorageConfig.builder()
                .configKey(request.getConfigKey())
                .accessEndpoint(request.getAccessEndpoint())
                .customDomain(request.getCustomDomain())
                .accessKey(request.getAccessKey())
                .secretKey(request.getSecretKey())
                .bucketName(request.getBucketName())
                .pathPrefix(request.getPathPrefix())
                .isHttps(request.getIsHttps())
                .bucketAccessType(request.getBucketAccessType())
                .region(request.getRegion())
                .provider(request.getProvider())
                .isDefault(request.getIsDefault())
                .remark(request.getRemark())
                .build();

        StorageConfig updated = storageConfigService.updateConfig(id, update);
        return Result.success(StorageConfigResponse.from(updated));
    }

    @Operation(summary = "删除存储配置")
    @DeleteMapping("/configs/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        storageConfigService.deleteConfig(id);
        return Result.success();
    }

    @Operation(summary = "设置默认存储配置")
    @PostMapping("/configs/{id}/set-default")
    public Result<StorageConfigResponse> setDefaultConfig(@PathVariable Long id) {
        StorageConfig config = storageConfigService.setDefault(id);
        return Result.success(StorageConfigResponse.from(config));
    }

    // ========== 文件管理 ==========

    @Operation(summary = "获取文件列表")
    @GetMapping("/files")
    public Result<Page<FileRecordResponse>> getFiles(
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) String originalName,
            @RequestParam(required = false) String fileExt,
            @RequestParam(required = false) String storageProvider,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<FileRecord> files;
        if (fileName != null || originalName != null || fileExt != null ||
            storageProvider != null || startDate != null || endDate != null) {
            files = fileStorageService.searchFiles(
                    fileName, originalName, fileExt, storageProvider, startDate, endDate, pageable);
        } else {
            files = fileStorageService.findAll(pageable);
        }
        return Result.success(files.map(FileRecordResponse::from));
    }

    @Operation(summary = "获取文件详情")
    @GetMapping("/files/{id}")
    public Result<FileRecordResponse> getFileById(@PathVariable UUID id) {
        return fileStorageService.findById(id)
                .map(FileRecordResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("文件不存在"));
    }

    @Operation(summary = "上传文件")
    @PostMapping("/files/upload")
    public Result<FileRecordResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        FileRecord record = fileStorageService.uploadFile(file, null, "ADMIN");
        return Result.success(FileRecordResponse.from(record));
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/files/{id}")
    public Result<Void> deleteFile(@PathVariable UUID id) {
        fileStorageService.deleteFile(id);
        return Result.success();
    }
}
