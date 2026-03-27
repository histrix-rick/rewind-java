package com.rewindai.app.controller;

import com.rewindai.app.dto.DreamAssetRequest;
import com.rewindai.app.dto.DreamAssetResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.DreamAsset;
import com.rewindai.system.daydream.service.DaydreamService;
import com.rewindai.system.daydream.service.DreamAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 梦境资产 Controller
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Tag(name = "梦境资产管理", description = "梦境资产管理接口")
@RestController
@RequestMapping("/api/dream-assets")
@RequiredArgsConstructor
public class DreamAssetController {

    private final DreamAssetService dreamAssetService;
    private final DaydreamService daydreamService;

    @Operation(summary = "获取梦境的所有活跃资产")
    @GetMapping("/dream/{dreamId}/active")
    public Result<List<DreamAssetResponse>> getActiveAssetsByDream(
            @PathVariable UUID dreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在"));
        List<DreamAsset> assets = dreamAssetService.getActiveAssetsByDream(dreamId);
        return Result.success(assets.stream()
                .map(DreamAssetResponse::from)
                .toList());
    }

    @Operation(summary = "获取梦境的所有资产（包括非活跃）")
    @GetMapping("/dream/{dreamId}/all")
    public Result<List<DreamAssetResponse>> getAllAssetsByDream(
            @PathVariable UUID dreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在"));
        List<DreamAsset> assets = dreamAssetService.getAllAssetsByDream(dreamId);
        return Result.success(assets.stream()
                .map(DreamAssetResponse::from)
                .toList());
    }

    @Operation(summary = "获取梦境指定节点的资产")
    @GetMapping("/dream/{dreamId}/node/{nodeId}")
    public Result<List<DreamAssetResponse>> getAssetsByDreamAndNode(
            @PathVariable UUID dreamId,
            @PathVariable UUID nodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在"));
        List<DreamAsset> assets = dreamAssetService.getAssetsByDreamAndNode(dreamId, nodeId);
        return Result.success(assets.stream()
                .map(DreamAssetResponse::from)
                .toList());
    }

    @Operation(summary = "获取资产详情")
    @GetMapping("/{id}")
    public Result<DreamAssetResponse> getAsset(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return dreamAssetService.findById(id)
                .map(asset -> {
                    daydreamService.findByIdAndUserId(asset.getDreamId(), userId)
                            .orElseThrow(() -> new RuntimeException("梦境不存在"));
                    return Result.success(DreamAssetResponse.from(asset));
                })
                .orElse(Result.notFound("资产不存在"));
    }

    @Operation(summary = "添加资产")
    @PostMapping("/dream/{dreamId}/node/{nodeId}")
    public Result<DreamAssetResponse> addAsset(
            @PathVariable UUID dreamId,
            @PathVariable UUID nodeId,
            @Valid @RequestBody DreamAssetRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        DreamAsset asset = DreamAsset.builder()
                .assetType(request.getAssetType())
                .assetName(request.getAssetName())
                .assetValue(request.getAssetValue())
                .quantity(request.getQuantity())
                .locationProvince(request.getLocationProvince())
                .locationCity(request.getLocationCity())
                .locationDistrict(request.getLocationDistrict())
                .locationAddress(request.getLocationAddress())
                .acquisitionDate(request.getAcquisitionDate())
                .build();
        asset.setMetadata(request.getMetadata());

        DreamAsset saved = dreamAssetService.addAsset(userId, dreamId, nodeId, asset);
        return Result.success(DreamAssetResponse.from(saved));
    }

    @Operation(summary = "更新资产")
    @PutMapping("/{id}")
    public Result<DreamAssetResponse> updateAsset(
            @PathVariable UUID id,
            @Valid @RequestBody DreamAssetRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        DreamAsset update = DreamAsset.builder()
                .assetName(request.getAssetName())
                .assetValue(request.getAssetValue())
                .quantity(request.getQuantity())
                .locationProvince(request.getLocationProvince())
                .locationCity(request.getLocationCity())
                .locationDistrict(request.getLocationDistrict())
                .locationAddress(request.getLocationAddress())
                .acquisitionDate(request.getAcquisitionDate())
                .build();
        update.setMetadata(request.getMetadata());

        DreamAsset updated = dreamAssetService.updateAsset(userId, id, update);
        return Result.success(DreamAssetResponse.from(updated));
    }

    @Operation(summary = "标记资产为非活跃")
    @PatchMapping("/{id}/deactivate")
    public Result<Void> deactivateAsset(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        dreamAssetService.deactivateAsset(userId, id);
        return Result.success();
    }

    @Operation(summary = "删除资产")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAsset(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        dreamAssetService.deleteAsset(userId, id);
        return Result.success();
    }
}
