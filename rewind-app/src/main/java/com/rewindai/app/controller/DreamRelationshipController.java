package com.rewindai.app.controller;

import com.rewindai.app.dto.DreamRelationshipRequest;
import com.rewindai.app.dto.DreamRelationshipResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.DreamRelationship;
import com.rewindai.system.daydream.service.DaydreamService;
import com.rewindai.system.daydream.service.DreamRelationshipService;
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
 * 梦境人物关系 Controller
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Tag(name = "梦境人物关系", description = "梦境人物关系管理接口")
@RestController
@RequestMapping("/api/dream-relationships")
@RequiredArgsConstructor
public class DreamRelationshipController {

    private final DreamRelationshipService dreamRelationshipService;
    private final DaydreamService daydreamService;

    @Operation(summary = "获取梦境的所有人物关系")
    @GetMapping("/dream/{dreamId}")
    public Result<List<DreamRelationshipResponse>> getRelationshipsByDream(
            @PathVariable UUID dreamId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        // 验证用户有权限访问这个梦境
        daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在"));
        List<DreamRelationship> relationships = dreamRelationshipService.getRelationshipsByDream(dreamId);
        return Result.success(relationships.stream()
                .map(DreamRelationshipResponse::from)
                .toList());
    }

    @Operation(summary = "获取梦境指定节点的人物关系")
    @GetMapping("/dream/{dreamId}/node/{nodeId}")
    public Result<List<DreamRelationshipResponse>> getRelationshipsByDreamAndNode(
            @PathVariable UUID dreamId,
            @PathVariable UUID nodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        // 验证用户有权限访问这个梦境
        daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new RuntimeException("梦境不存在"));
        List<DreamRelationship> relationships = dreamRelationshipService.getRelationshipsByDreamAndNode(dreamId, nodeId);
        return Result.success(relationships.stream()
                .map(DreamRelationshipResponse::from)
                .toList());
    }

    @Operation(summary = "添加人物关系")
    @PostMapping("/dream/{dreamId}/node/{nodeId}")
    public Result<DreamRelationshipResponse> addRelationship(
            @PathVariable UUID dreamId,
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

        DreamRelationship saved = dreamRelationshipService.addRelationship(userId, dreamId, nodeId, relationship);
        return Result.success(DreamRelationshipResponse.from(saved));
    }

    @Operation(summary = "更新人物关系")
    @PutMapping("/{id}")
    public Result<DreamRelationshipResponse> updateRelationship(
            @PathVariable UUID id,
            @Valid @RequestBody DreamRelationshipRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        DreamRelationship update = DreamRelationship.builder()
                .personName(request.getPersonName())
                .relationshipTypeId(request.getRelationshipTypeId())
                .intimacyLevel(request.getIntimacyLevel())
                .intimacyDescription(request.getIntimacyDescription())
                .notes(request.getNotes())
                .build();

        DreamRelationship updated = dreamRelationshipService.updateRelationship(userId, id, update);
        return Result.success(DreamRelationshipResponse.from(updated));
    }

    @Operation(summary = "删除人物关系")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRelationship(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        dreamRelationshipService.deleteRelationship(userId, id);
        return Result.success();
    }
}
