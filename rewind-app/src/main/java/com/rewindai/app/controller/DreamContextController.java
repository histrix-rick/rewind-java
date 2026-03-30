package com.rewindai.app.controller;

import com.rewindai.app.dto.DreamContextRequest;
import com.rewindai.app.dto.DreamContextResponse;
import com.rewindai.app.dto.DreamRelationshipRequest;
import com.rewindai.app.dto.DreamRelationshipResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.DreamContext;
import com.rewindai.system.daydream.entity.DreamRelationship;
import com.rewindai.system.daydream.service.DaydreamService;
import com.rewindai.system.daydream.service.DreamContextService;
import com.rewindai.system.daydream.service.DreamRelationshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 梦境上下文 Controller
 *
 * @author Rewind.ai Team
 */
@Tag(name = "梦境上下文管理", description = "梦境上下文和人物关系相关接口")
@RestController
@RequestMapping("/api/daydreams/{daydreamId}/context")
@RequiredArgsConstructor
public class DreamContextController {

    private final DreamContextService dreamContextService;
    private final DreamRelationshipService dreamRelationshipService;
    private final DaydreamService daydreamService;

    // ========== 梦境上下文 ==========

    @Operation(summary = "获取指定节点的上下文")
    @GetMapping("/nodes/{nodeId}")
    public Result<DreamContextResponse> getContextByNode(
            @PathVariable UUID daydreamId,
            @PathVariable UUID nodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findAccessibleById(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在或无权限访问"));

        return dreamContextService.findByDreamIdAndNodeId(daydreamId, nodeId)
                .map(DreamContextResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("上下文不存在"));
    }

    @Operation(summary = "获取梦境的所有上下文历史")
    @GetMapping
    public Result<List<DreamContextResponse>> getContexts(
            @PathVariable UUID daydreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findAccessibleById(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在或无权限访问"));

        List<DreamContext> contexts = dreamContextService.getContextsByDreamId(daydreamId);
        return Result.success(contexts.stream().map(DreamContextResponse::from).toList());
    }

    @Operation(summary = "创建节点上下文")
    @PostMapping("/nodes/{nodeId}")
    public Result<DreamContextResponse> createContext(
            @PathVariable UUID daydreamId,
            @PathVariable UUID nodeId,
            @Valid @RequestBody DreamContextRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        DreamContext context = DreamContext.builder()
                .identityId(request.getIdentityId())
                .financialAmount(request.getFinancialAmount())
                .educationLevelId(request.getEducationLevelId())
                .birthProvince(request.getBirthProvince())
                .birthCity(request.getBirthCity())
                .birthDistrict(request.getBirthDistrict())
                .birthAddress(request.getBirthAddress())
                .dreamProvince(request.getDreamProvince())
                .dreamCity(request.getDreamCity())
                .dreamDistrict(request.getDreamDistrict())
                .dreamAddress(request.getDreamAddress())
                .build();

        DreamContext saved = dreamContextService.createContext(userId, daydreamId, nodeId, context);
        return Result.success(DreamContextResponse.from(saved));
    }

    @Operation(summary = "更新节点上下文")
    @PutMapping("/{contextId}")
    public Result<DreamContextResponse> updateContext(
            @PathVariable UUID daydreamId,
            @PathVariable UUID contextId,
            @Valid @RequestBody DreamContextRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findModifiableById(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在或无权限修改"));

        DreamContext update = DreamContext.builder()
                .identityId(request.getIdentityId())
                .financialAmount(request.getFinancialAmount())
                .educationLevelId(request.getEducationLevelId())
                .birthProvince(request.getBirthProvince())
                .birthCity(request.getBirthCity())
                .birthDistrict(request.getBirthDistrict())
                .birthAddress(request.getBirthAddress())
                .dreamProvince(request.getDreamProvince())
                .dreamCity(request.getDreamCity())
                .dreamDistrict(request.getDreamDistrict())
                .dreamAddress(request.getDreamAddress())
                .build();

        DreamContext updated = dreamContextService.updateContext(userId, contextId, update);
        return Result.success(DreamContextResponse.from(updated));
    }

    @Operation(summary = "复制上下文到新节点")
    @PostMapping("/copy")
    public Result<DreamContextResponse> copyContext(
            @PathVariable UUID daydreamId,
            @RequestParam UUID fromNodeId,
            @RequestParam UUID toNodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        DreamContext copied = dreamContextService.copyToNode(userId, daydreamId, fromNodeId, toNodeId);
        return Result.success(DreamContextResponse.from(copied));
    }

    // ========== 人物关系 ==========

    @Operation(summary = "获取指定节点的人物关系")
    @GetMapping("/nodes/{nodeId}/relationships")
    public Result<List<DreamRelationshipResponse>> getRelationshipsByNode(
            @PathVariable UUID daydreamId,
            @PathVariable UUID nodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findAccessibleById(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在或无权限访问"));

        List<DreamRelationship> relationships = dreamRelationshipService.getRelationshipsByDreamAndNode(daydreamId, nodeId);
        return Result.success(relationships.stream().map(DreamRelationshipResponse::from).toList());
    }

    @Operation(summary = "获取梦境的所有人物关系")
    @GetMapping("/relationships")
    public Result<List<DreamRelationshipResponse>> getAllRelationships(
            @PathVariable UUID daydreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findAccessibleById(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在或无权限访问"));

        List<DreamRelationship> relationships = dreamRelationshipService.getRelationshipsByDream(daydreamId);
        return Result.success(relationships.stream().map(DreamRelationshipResponse::from).toList());
    }

    @Operation(summary = "添加人物关系")
    @PostMapping("/nodes/{nodeId}/relationships")
    public Result<DreamRelationshipResponse> addRelationship(
            @PathVariable UUID daydreamId,
            @PathVariable UUID nodeId,
            @Valid @RequestBody DreamRelationshipRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        DreamRelationship relationship = DreamRelationship.builder()
                .personName(request.getPersonName())
                .relationshipTypeId(request.getRelationshipTypeId())
                .intimacyLevel(request.getIntimacyLevel())
                .intimacyDescription(request.getIntimacyDescription())
                .notes(request.getNotes())
                .build();

        DreamRelationship saved = dreamRelationshipService.addRelationship(userId, daydreamId, nodeId, relationship);
        return Result.success(DreamRelationshipResponse.from(saved));
    }

    @Operation(summary = "更新人物关系")
    @PutMapping("/relationships/{relationshipId}")
    public Result<DreamRelationshipResponse> updateRelationship(
            @PathVariable UUID daydreamId,
            @PathVariable UUID relationshipId,
            @Valid @RequestBody DreamRelationshipRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findModifiableById(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在或无权限修改"));

        DreamRelationship update = DreamRelationship.builder()
                .personName(request.getPersonName())
                .relationshipTypeId(request.getRelationshipTypeId())
                .intimacyLevel(request.getIntimacyLevel())
                .intimacyDescription(request.getIntimacyDescription())
                .notes(request.getNotes())
                .build();

        DreamRelationship updated = dreamRelationshipService.updateRelationship(userId, relationshipId, update);
        return Result.success(DreamRelationshipResponse.from(updated));
    }

    @Operation(summary = "删除人物关系")
    @DeleteMapping("/relationships/{relationshipId}")
    public Result<Void> deleteRelationship(
            @PathVariable UUID daydreamId,
            @PathVariable UUID relationshipId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findModifiableById(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在或无权限修改"));

        dreamRelationshipService.deleteRelationship(userId, relationshipId);
        return Result.success();
    }

    @Operation(summary = "复制人物关系到新节点")
    @PostMapping("/relationships/copy")
    public Result<List<DreamRelationshipResponse>> copyRelationships(
            @PathVariable UUID daydreamId,
            @RequestParam UUID fromNodeId,
            @RequestParam UUID toNodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        List<DreamRelationship> copied = dreamRelationshipService.copyToNode(userId, daydreamId, fromNodeId, toNodeId);
        return Result.success(copied.stream().map(DreamRelationshipResponse::from).toList());
    }
}
