package com.rewindai.app.dto;

import com.rewindai.system.storage.entity.FileRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 文件上传响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    private UUID id;
    private String fileName;
    private String fileUrl;
    private Long fileSize;

    public static FileUploadResponse from(FileRecord record) {
        return FileUploadResponse.builder()
                .id(record.getId())
                .fileName(record.getFileName())
                .fileUrl(record.getFileUrl())
                .fileSize(record.getFileSize())
                .build();
    }
}
