package com.rewindai.admin.controller;

import com.rewindai.admin.dto.*;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.security.entity.RiskRule;
import com.rewindai.system.security.entity.RiskList;
import com.rewindai.system.security.enums.RiskLevel;
import com.rewindai.system.security.enums.RiskType;
import com.rewindai.system.security.enums.RuleStatus;
import com.rewindai.system.security.service.RiskRuleService;
import com.rewindai.system.security.service.RiskListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 风控管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/risk")
@RequiredArgsConstructor
@Tag(name = "后台管理-风控管理", description = "风控规则和风险名单管理接口")
public class AdminRiskController {

    private final RiskRuleService riskRuleService;
    private final RiskListService riskListService;

    // ========== 风控规则管理 ==========

    @GetMapping("/rules")
    @Operation(summary = "获取风控规则列表")
    public Result<Page<RiskRuleResponse>> getRiskRules(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) RiskType riskType,
            @RequestParam(required = false) RuleStatus status,
            @PageableDefault(size = 20, sort = "sortOrder") Pageable pageable) {

        Page<RiskRule> rules;
        if (keyword != null && !keyword.isEmpty()) {
            rules = riskRuleService.searchByKeyword(keyword, pageable);
        } else if (riskType != null) {
            rules = riskRuleService.findByRiskType(riskType, pageable);
        } else if (status != null) {
            rules = riskRuleService.findByStatus(status, pageable);
        } else {
            rules = riskRuleService.findAll(pageable);
        }
        return Result.success(rules.map(RiskRuleResponse::from));
    }

    @GetMapping("/rules/{id}")
    @Operation(summary = "获取风控规则详情")
    public Result<RiskRuleResponse> getRiskRuleById(@PathVariable Long id) {
        return riskRuleService.findById(id)
                .map(RiskRuleResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("风控规则不存在"));
    }

    @PostMapping("/rules")
    @Operation(summary = "创建风控规则")
    public Result<RiskRuleResponse> createRiskRule(@Valid @RequestBody RiskRuleRequest request) {
        RiskRule rule = RiskRule.builder()
                .ruleName(request.getRuleName())
                .ruleCode(request.getRuleCode())
                .riskType(request.getRiskType() != null ? RiskType.valueOf(request.getRiskType()) : RiskType.CUSTOM)
                .riskLevel(request.getRiskLevel() != null ? RiskLevel.valueOf(request.getRiskLevel()) : RiskLevel.MEDIUM)
                .ruleConfig(request.getRuleConfig())
                .description(request.getDescription())
                .status(request.getStatus() != null ? RuleStatus.valueOf(request.getStatus()) : RuleStatus.ACTIVE)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();

        RiskRule saved = riskRuleService.createRule(rule);
        return Result.success(RiskRuleResponse.from(saved));
    }

    @PutMapping("/rules/{id}")
    @Operation(summary = "更新风控规则")
    public Result<RiskRuleResponse> updateRiskRule(
            @PathVariable Long id,
            @Valid @RequestBody RiskRuleRequest request) {

        RiskRule update = RiskRule.builder()
                .ruleName(request.getRuleName())
                .ruleCode(request.getRuleCode())
                .riskType(request.getRiskType() != null ? RiskType.valueOf(request.getRiskType()) : null)
                .riskLevel(request.getRiskLevel() != null ? RiskLevel.valueOf(request.getRiskLevel()) : null)
                .ruleConfig(request.getRuleConfig())
                .description(request.getDescription())
                .status(request.getStatus() != null ? RuleStatus.valueOf(request.getStatus()) : null)
                .sortOrder(request.getSortOrder())
                .build();

        RiskRule updated = riskRuleService.updateRule(id, update);
        return Result.success(RiskRuleResponse.from(updated));
    }

    @DeleteMapping("/rules/{id}")
    @Operation(summary = "删除风控规则")
    public Result<Void> deleteRiskRule(@PathVariable Long id) {
        riskRuleService.deleteRule(id);
        return Result.success();
    }

    // ========== 风险名单管理 ==========

    @GetMapping("/list")
    @Operation(summary = "获取风险名单列表")
    public Result<Page<RiskListResponse>> getRiskList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String listType,
            @RequestParam(required = false) RiskLevel riskLevel,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<RiskList> entries;
        if (keyword != null && !keyword.isEmpty()) {
            entries = riskListService.searchByKeyword(keyword, pageable);
        } else if (listType != null && !listType.isEmpty()) {
            entries = riskListService.findByListType(listType, pageable);
        } else if (riskLevel != null) {
            entries = riskListService.findByRiskLevel(riskLevel, pageable);
        } else {
            entries = riskListService.findAll(pageable);
        }
        return Result.success(entries.map(RiskListResponse::from));
    }

    @GetMapping("/list/{id}")
    @Operation(summary = "获取风险名单详情")
    public Result<RiskListResponse> getRiskListById(@PathVariable Long id) {
        return riskListService.findById(id)
                .map(RiskListResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("风险名单不存在"));
    }

    @GetMapping("/list/check")
    @Operation(summary = "检查是否在风险名单中")
    public Result<Boolean> checkRiskList(
            @RequestParam String listType,
            @RequestParam String targetValue) {
        return Result.success(riskListService.isBlacklisted(listType, targetValue));
    }

    @PostMapping("/list")
    @Operation(summary = "添加到风险名单")
    public Result<RiskListResponse> addToRiskList(
            @Valid @RequestBody RiskListRequest request,
            Authentication authentication) {

        Integer addedBy = ((Number) authentication.getPrincipal()).intValue();

        RiskList entry = RiskList.builder()
                .listType(request.getListType())
                .targetValue(request.getTargetValue())
                .riskLevel(request.getRiskLevel() != null ? RiskLevel.valueOf(request.getRiskLevel()) : RiskLevel.MEDIUM)
                .reason(request.getReason())
                .addedBy(addedBy)
                .expiresAt(request.getExpiresAt())
                .build();

        RiskList saved = riskListService.addToRiskList(entry);
        return Result.success(RiskListResponse.from(saved));
    }

    @PutMapping("/list/{id}")
    @Operation(summary = "更新风险名单")
    public Result<RiskListResponse> updateRiskList(
            @PathVariable Long id,
            @Valid @RequestBody RiskListRequest request) {

        RiskList update = RiskList.builder()
                .riskLevel(request.getRiskLevel() != null ? RiskLevel.valueOf(request.getRiskLevel()) : null)
                .reason(request.getReason())
                .expiresAt(request.getExpiresAt())
                .build();

        RiskList updated = riskListService.updateRiskList(id, update);
        return Result.success(RiskListResponse.from(updated));
    }

    @DeleteMapping("/list/{id}")
    @Operation(summary = "从风险名单移除")
    public Result<Void> removeFromRiskList(@PathVariable Long id) {
        riskListService.removeFromRiskList(id);
        return Result.success();
    }

    @DeleteMapping("/list/clean-expired")
    @Operation(summary = "清理过期风险名单")
    public Result<Integer> cleanExpired() {
        return Result.success(riskListService.cleanExpired());
    }
}
