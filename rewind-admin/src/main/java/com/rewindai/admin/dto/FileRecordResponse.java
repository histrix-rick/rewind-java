package com.rewindai.admin.dto;

import com.rewindai.system.storage.entity.FileRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 文件记录响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRecordResponse {

    private UUID id;
    private String fileName;
    private String originalName;
    private String fileExt;
    private Long fileSize;
    private String contentType;
    private String filePath;
    private String fileUrl;
    private UUID uploaderId;
    private String uploaderType;
    private Long configId;
    private String storageProvider;
    private OffsetDateTime createdAt;

    public static FileRecordResponse from(FileRecord record) {
        return FileRecordResponse.builder()
                .id(record.getId())
                .fileName(record.getFileName())
                .originalName(record.getOriginalName())
                .fileExt(record.getFileExt())
                .fileSize(record.getFileSize())
                .contentType(record.getContentType())
                .filePath(record.getFilePath())
                .fileUrl(record.getFileUrl())
                .uploaderId(record.getUploaderId())
                .uploaderType(record.getUploaderType())
                .configId(record.getConfigId())
                .storageProvider(record.getStorageProvider())
                .createdAt(record.getCreatedAt())
                .build();
    }

    public String getFileSizeFormatted() {
        if (fileSize == null) return "0 B";
        long size = fileSize;
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.2f MB", size / (1024.0 * 1024));
        return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
    }
}
