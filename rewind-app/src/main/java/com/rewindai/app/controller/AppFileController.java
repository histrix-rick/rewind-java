package com.rewindai.app.controller;

import com.rewindai.app.dto.FileUploadResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.storage.entity.FileRecord;
import com.rewindai.system.storage.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 前端文件上传 Controller
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "前端文件管理", description = "文件上传相关接口")
public class AppFileController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        FileRecord record = fileStorageService.uploadFile(file, userId, "USER");
        return Result.success(FileUploadResponse.from(record));
    }
}
