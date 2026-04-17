package com.rewindai.admin.controller;

import com.rewindai.admin.dto.SmsProviderConfigRequest;
import com.rewindai.admin.dto.SmsProviderConfigResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.sms.entity.SmsProviderConfig;
import com.rewindai.system.sms.service.SmsProviderConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台短信配置管理 Controller
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/sms")
@RequiredArgsConstructor
@Tag(name = "后台短信配置管理", description = "短信运营商配置和短信服务管理接口")
public class AdminSmsController {

    private final SmsProviderConfigService smsProviderConfigService;

    @Operation(summary = "获取所有短信运营商配置")
    @GetMapping("/providers")
    public Result<List<SmsProviderConfigResponse>> getAllProviders() {
        List<SmsProviderConfig> configs = smsProviderConfigService.findAll();
        return Result.success(SmsProviderConfigResponse.fromList(configs));
    }

    @Operation(summary = "获取激活的短信运营商配置")
    @GetMapping("/providers/active")
    public Result<List<SmsProviderConfigResponse>> getActiveProviders() {
        List<SmsProviderConfig> configs = smsProviderConfigService.findActiveProviders();
        return Result.success(SmsProviderConfigResponse.fromList(configs));
    }

    @Operation(summary = "获取默认短信运营商配置")
    @GetMapping("/providers/default")
    public Result<SmsProviderConfigResponse> getDefaultProvider() {
        return smsProviderConfigService.findDefaultProvider()
                .map(SmsProviderConfigResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("未设置默认短信运营商"));
    }

    @Operation(summary = "获取短信运营商配置详情")
    @GetMapping("/providers/{id}")
    public Result<SmsProviderConfigResponse> getProviderById(@PathVariable Long id) {
        return smsProviderConfigService.findById(id)
                .map(SmsProviderConfigResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("配置不存在"));
    }

    @Operation(summary = "创建短信运营商配置")
    @PostMapping("/providers")
    public Result<SmsProviderConfigResponse> createProvider(@Valid @RequestBody SmsProviderConfigRequest request) {
        SmsProviderConfig config = SmsProviderConfig.builder()
                .providerCode(request.getProviderCode())
                .providerName(request.getProviderName())
                .accessKeyId(request.getAccessKeyId())
                .accessKeySecret(request.getAccessKeySecret())
                .signName(request.getSignName())
                .templateCodeLogin(request.getTemplateCodeLogin())
                .templateCodeRegister(request.getTemplateCodeRegister())
                .templateCodeVerify(request.getTemplateCodeVerify())
                .endpoint(request.getEndpoint())
                .region(request.getRegion())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .isActive(request.getIsActive() != null ? request.getIsActive() : false)
                .build();

        SmsProviderConfig saved = smsProviderConfigService.createConfig(config);
        log.info("创建短信运营商配置成功: providerCode={}", saved.getProviderCode());
        return Result.success(SmsProviderConfigResponse.from(saved));
    }

    @Operation(summary = "更新短信运营商配置")
    @PutMapping("/providers/{id}")
    public Result<SmsProviderConfigResponse> updateProvider(
            @PathVariable Long id,
            @Valid @RequestBody SmsProviderConfigRequest request) {
        SmsProviderConfig config = SmsProviderConfig.builder()
                .providerName(request.getProviderName())
                .accessKeyId(request.getAccessKeyId())
                .accessKeySecret(request.getAccessKeySecret())
                .signName(request.getSignName())
                .templateCodeLogin(request.getTemplateCodeLogin())
                .templateCodeRegister(request.getTemplateCodeRegister())
                .templateCodeVerify(request.getTemplateCodeVerify())
                .endpoint(request.getEndpoint())
                .region(request.getRegion())
                .isDefault(request.getIsDefault())
                .isActive(request.getIsActive())
                .build();

        SmsProviderConfig updated = smsProviderConfigService.updateConfig(id, config);
        log.info("更新短信运营商配置成功: id={}", id);
        return Result.success(SmsProviderConfigResponse.from(updated));
    }

    @Operation(summary = "设置默认短信运营商")
    @PutMapping("/providers/{id}/default")
    public Result<SmsProviderConfigResponse> setDefaultProvider(@PathVariable Long id) {
        SmsProviderConfig updated = smsProviderConfigService.setDefaultProvider(id);
        log.info("设置默认短信运营商成功: id={}", id);
        return Result.success(SmsProviderConfigResponse.from(updated));
    }

    @Operation(summary = "删除短信运营商配置")
    @DeleteMapping("/providers/{id}")
    public Result<Void> deleteProvider(@PathVariable Long id) {
        smsProviderConfigService.deleteConfig(id);
        log.info("删除短信运营商配置成功: id={}", id);
        return Result.success();
    }
}
