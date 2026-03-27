package com.rewindai.app.controller;

import com.rewindai.app.dto.CompleteAssetInfoRequest;
import com.rewindai.app.dto.PropertyPriceEstimateRequest;
import com.rewindai.app.dto.PropertyPriceEstimateResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.DreamAsset;
import com.rewindai.system.daydream.entity.TimelineNodeExtension;
import com.rewindai.system.daydream.enums.AssetType;
import com.rewindai.system.daydream.service.DaydreamService;
import com.rewindai.system.daydream.service.DreamAssetService;
import com.rewindai.system.daydream.service.PropertyPriceService;
import com.rewindai.system.daydream.service.TimelineNodeExtensionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 房价估算与资产完善 Controller
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Tag(name = "房价估算与资产完善", description = "房价估算和资产信息完善接口")
@RestController
@RequestMapping("/api/property-estimate")
@RequiredArgsConstructor
public class PropertyEstimateController {

    private final PropertyPriceService propertyPriceService;
    private final DreamAssetService dreamAssetService;
    private final TimelineNodeExtensionService extensionService;
    private final DaydreamService daydreamService;

    @Operation(summary = "估算房价")
    @PostMapping("/estimate")
    public Result<PropertyPriceEstimateResponse> estimatePrice(
            @Valid @RequestBody PropertyPriceEstimateRequest request,
            Authentication authentication) {
        PropertyPriceService.PropertyPriceEstimate estimate = propertyPriceService.estimatePropertyPrice(
                request.getProvince(),
                request.getCity(),
                request.getDistrict(),
                request.getYear(),
                request.getTotalBudget()
        );

        PropertyPriceEstimateResponse response = PropertyPriceEstimateResponse.builder()
                .averagePricePerSqm(estimate.averagePricePerSqm)
                .estimatedArea(estimate.estimatedArea)
                .estimatedUnitCount(estimate.estimatedUnitCount)
                .locationDescription(estimate.locationDescription)
                .year(estimate.year)
                .dataSource(estimate.dataSource)
                .build();

        return Result.success(response);
    }

    @Operation(summary = "完善资产信息")
    @PostMapping("/complete/{nodeId}")
    public Result<Void> completeAssetInfo(
            @PathVariable UUID nodeId,
            @Valid @RequestBody CompleteAssetInfoRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        // 验证节点存在且用户有权限
        var extension = extensionService.findByNodeId(nodeId)
                .orElseThrow(() -> new RuntimeException("节点扩展不存在"));

        // 如果有资产信息，保存资产
        if (request.getRelationshipUpdates() != null || request.getIdentityUpdates() != null) {
            extensionService.updateExtension(
                    userId,
                    nodeId,
                    true,
                    null,
                    null,
                    request.getRelationshipUpdates(),
                    request.getIdentityUpdates()
            );
        } else {
            // 仅标记资产信息完成
            extensionService.updateAssetInfoCompleted(userId, nodeId, true);
        }

        log.info("资产信息完善完成: nodeId={}", nodeId);
        return Result.success();
    }

    @Operation(summary = "检查资产信息是否已完善")
    @GetMapping("/check/{nodeId}")
    public Result<Boolean> checkAssetInfoCompleted(
            @PathVariable UUID nodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        boolean completed = extensionService.findByNodeId(nodeId)
                .map(TimelineNodeExtension::getAssetInfoCompleted)
                .orElse(false);
        return Result.success(completed);
    }

    @Operation(summary = "快速创建房产资产（带价格估算）")
    @PostMapping("/create-realty/{dreamId}/{nodeId}")
    public Result<DreamAsset> createRealtyAssetWithEstimate(
            @PathVariable UUID dreamId,
            @PathVariable UUID nodeId,
            @Valid @RequestBody PropertyPriceEstimateRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        // 验证用户权限
        daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在"));

        // 估算房价
        PropertyPriceService.PropertyPriceEstimate estimate = propertyPriceService.estimatePropertyPrice(
                request.getProvince(),
                request.getCity(),
                request.getDistrict(),
                request.getYear(),
                request.getTotalBudget()
        );

        // 创建资产名称
        String assetName = request.getCity() + "房产";

        // 创建资产
        DreamAsset asset = dreamAssetService.addRealtyAsset(
                userId,
                dreamId,
                nodeId,
                assetName,
                request.getTotalBudget(),
                request.getProvince(),
                request.getCity(),
                request.getDistrict(),
                null,
                null,
                java.util.Map.of(
                        "averagePricePerSqm", estimate.averagePricePerSqm,
                        "estimatedArea", estimate.estimatedArea,
                        "estimatedUnitCount", estimate.estimatedUnitCount,
                        "dataSource", estimate.dataSource,
                        "year", estimate.year
                )
        );

        return Result.success(asset);
    }
}
