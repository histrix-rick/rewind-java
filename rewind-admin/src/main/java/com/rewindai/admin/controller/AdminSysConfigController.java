package com.rewindai.admin.controller;

import com.rewindai.admin.dto.*;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.config.entity.SensitiveWord;
import com.rewindai.system.config.entity.SysConfig;
import com.rewindai.system.config.enums.ConfigCategory;
import com.rewindai.system.config.service.SysSensitiveWordService;
import com.rewindai.system.config.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台系统配置管理 Controller
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/sysconfig")
@RequiredArgsConstructor
@Tag(name = "后台系统配置管理", description = "系统配置和敏感词管理接口")
public class AdminSysConfigController {

    private final SysConfigService sysConfigService;
    private final SysSensitiveWordService sensitiveWordService;

    // ========== 系统配置管理 ==========

    @Operation(summary = "获取所有系统配置")
    @GetMapping("/configs")
    public Result<List<SysConfigResponse>> getAllConfigs() {
        List<SysConfig> configs = sysConfigService.findAll();
        return Result.success(configs.stream().map(SysConfigResponse::from).toList());
    }

    @Operation(summary = "按分类获取系统配置")
    @GetMapping("/configs/category/{category}")
    public Result<List<SysConfigResponse>> getConfigsByCategory(@PathVariable ConfigCategory category) {
        List<SysConfig> configs = sysConfigService.findByCategory(category);
        return Result.success(configs.stream().map(SysConfigResponse::from).toList());
    }

    @Operation(summary = "获取系统配置详情")
    @GetMapping("/configs/{id}")
    public Result<SysConfigResponse> getConfigById(@PathVariable Long id) {
        return sysConfigService.findById(id)
                .map(SysConfigResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("配置不存在"));
    }

    @Operation(summary = "创建系统配置")
    @PostMapping("/configs")
    public Result<SysConfigResponse> createConfig(@Valid @RequestBody SysConfigRequest request) {
        SysConfig config = SysConfig.builder()
                .configKey(request.getConfigKey())
                .configName(request.getConfigName())
                .configValue(request.getConfigValue())
                .configCategory(request.getConfigCategory())
                .valueType(request.getValueType() != null ? request.getValueType() : "STRING")
                .description(request.getDescription())
                .isEncrypted(request.getIsEncrypted() != null ? request.getIsEncrypted() : false)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();

        SysConfig saved = sysConfigService.createConfig(config);
        return Result.success(SysConfigResponse.from(saved));
    }

    @Operation(summary = "更新系统配置")
    @PutMapping("/configs/{id}")
    public Result<SysConfigResponse> updateConfig(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String value = request.get("configValue");
        SysConfig updated = sysConfigService.updateConfig(id, value);
        return Result.success(SysConfigResponse.from(updated));
    }

    @Operation(summary = "批量更新系统配置")
    @PutMapping("/configs/batch")
    public Result<Void> batchUpdateConfigs(@Valid @RequestBody BatchUpdateConfigsRequest request) {
        sysConfigService.batchUpdateConfigs(request.getConfigs());
        return Result.success();
    }

    @Operation(summary = "删除系统配置")
    @DeleteMapping("/configs/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        sysConfigService.deleteConfig(id);
        return Result.success();
    }

    @Operation(summary = "初始化默认配置")
    @PostMapping("/configs/init")
    public Result<Void> initializeConfigs() {
        sysConfigService.initializeDefaultConfigs();
        return Result.success();
    }

    // ========== 敏感词管理 ==========

    @Operation(summary = "获取敏感词列表")
    @GetMapping("/sensitive-words")
    public Result<Page<SensitiveWordResponse>> getSensitiveWords(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String wordType,
            @RequestParam(required = false) String severity,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<SensitiveWord> words;
        if (keyword != null && !keyword.isEmpty()) {
            words = sensitiveWordService.searchByKeyword(keyword, pageable);
        } else if (wordType != null && !wordType.isEmpty()) {
            words = sensitiveWordService.findByWordType(wordType, pageable);
        } else if (severity != null && !severity.isEmpty()) {
            words = sensitiveWordService.findBySeverity(severity, pageable);
        } else {
            words = sensitiveWordService.findAll(pageable);
        }
        return Result.success(words.map(SensitiveWordResponse::from));
    }

    @Operation(summary = "获取敏感词详情")
    @GetMapping("/sensitive-words/{id}")
    public Result<SensitiveWordResponse> getSensitiveWordById(@PathVariable Long id) {
        return sensitiveWordService.findById(id)
                .map(SensitiveWordResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("敏感词不存在"));
    }

    @Operation(summary = "添加敏感词")
    @PostMapping("/sensitive-words")
    public Result<SensitiveWordResponse> createSensitiveWord(@Valid @RequestBody SensitiveWordRequest request) {
        SensitiveWord word = SensitiveWord.builder()
                .word(request.getWord())
                .wordType(request.getWordType() != null ? request.getWordType() : "NORMAL")
                .severity(request.getSeverity() != null ? request.getSeverity() : "MEDIUM")
                .remark(request.getRemark())
                .build();

        SensitiveWord saved = sensitiveWordService.createWord(word);
        return Result.success(SensitiveWordResponse.from(saved));
    }

    @Operation(summary = "更新敏感词")
    @PutMapping("/sensitive-words/{id}")
    public Result<SensitiveWordResponse> updateSensitiveWord(
            @PathVariable Long id,
            @Valid @RequestBody SensitiveWordRequest request) {
        SensitiveWord update = SensitiveWord.builder()
                .word(request.getWord())
                .wordType(request.getWordType())
                .severity(request.getSeverity())
                .remark(request.getRemark())
                .build();

        SensitiveWord updated = sensitiveWordService.updateWord(id, update);
        return Result.success(SensitiveWordResponse.from(updated));
    }

    @Operation(summary = "删除敏感词")
    @DeleteMapping("/sensitive-words/{id}")
    public Result<Void> deleteSensitiveWord(@PathVariable Long id) {
        sensitiveWordService.deleteWord(id);
        return Result.success();
    }

    @Operation(summary = "批量导入敏感词")
    @PostMapping("/sensitive-words/batch-import")
    public Result<List<SensitiveWordResponse>> batchImportSensitiveWords(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String wordType,
            @RequestParam(required = false) String severity) {
        try {
            List<String> words = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        words.add(trimmed);
                    }
                }
            }
            List<SensitiveWord> imported = sensitiveWordService.batchImportWords(words, wordType, severity);
            return Result.success(imported.stream().map(SensitiveWordResponse::from).toList());
        } catch (Exception e) {
            log.error("批量导入敏感词失败", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取所有敏感词列表")
    @GetMapping("/sensitive-words/all")
    public Result<List<String>> getAllSensitiveWords() {
        return Result.success(sensitiveWordService.getAllWords());
    }

    @Operation(summary = "检测文本是否包含敏感词")
    @PostMapping("/sensitive-words/check")
    public Result<Boolean> checkSensitiveWord(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        return Result.success(sensitiveWordService.containsSensitiveWord(text));
    }

    @Operation(summary = "过滤文本中的敏感词")
    @PostMapping("/sensitive-words/filter")
    public Result<String> filterSensitiveWords(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String replacement = request.get("replacement");
        return Result.success(sensitiveWordService.filterSensitiveWords(text, replacement));
    }
}
