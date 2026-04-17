package com.rewindai.app.controller;

import com.rewindai.app.dto.*;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.*;
import com.rewindai.system.daydream.enums.DreamPrivacy;
import com.rewindai.system.daydream.enums.DreamStatus;
import com.rewindai.system.daydream.repository.DaydreamRepository;
import com.rewindai.system.daydream.service.*;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.entity.UserAttribute;
import com.rewindai.system.user.service.AttributeService;
import com.rewindai.system.user.service.UserService;
import com.rewindai.system.wallet.enums.TransactionType;
import com.rewindai.system.wallet.service.UserWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 白日梦 Controller
 *
 * @author Rewind.ai Team
 */
@Tag(name = "白日梦管理", description = "白日梦相关接口")
@RestController
@RequestMapping("/api/daydreams")
@RequiredArgsConstructor
public class DaydreamController {

    private final DaydreamService daydreamService;
    private final DaydreamRepository daydreamRepository;
    private final UserService userService;
    private final AttributeService attributeService;
    private final UserWalletService userWalletService;
    private final DreamContextService dreamContextService;
    private final DreamRelationshipService dreamRelationshipService;
    private final UserIdentityService userIdentityService;
    private final EducationLevelService educationLevelService;
    private final RelationshipTypeService relationshipTypeService;

    @Operation(summary = "创建白日梦")
    @PostMapping
    public Result<DaydreamResponse> create(@Valid @RequestBody CreateDaydreamRequest request,
                                            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Daydream daydream = Daydream.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .coverUrl(request.getCoverUrl())
                .startDate(request.getStartDate())
                .build();

        Daydream saved = daydreamService.create(userId, daydream, user.getBirthDate());

        // 创建白日梦奖励 10 梦想币
        userWalletService.addCoins(userId, new BigDecimal("10"),
                "创建白日梦奖励", TransactionType.REWARD, saved.getId(), "DAYDREAM");

        BigDecimal progress = daydreamService.calculateProgress(saved);
        return Result.success(DaydreamResponse.from(saved, progress));
    }

    @Operation(summary = "完整创建白日梦（包含上下文和关系）")
    @PostMapping("/full")
    public Result<DaydreamResponse> createFull(@Valid @RequestBody CreateDaydreamFullRequest request,
                                                 Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Daydream daydream = Daydream.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .coverUrl(request.getCoverUrl())
                .startDate(request.getStartDate())
                .build();

        if (request.getPrivacy() != null) {
            daydream.setPrivacy(request.getPrivacy() == 1 ?
                    DreamPrivacy.PUBLIC :
                    DreamPrivacy.PRIVATE);
        }

        // 构建DreamContext
        DreamContext context = null;
        if (request.getContext() != null) {
            context = DreamContext.builder()
                    .identityId(request.getContext().getIdentityId())
                    .financialAmount(request.getContext().getFinancialAmount())
                    .educationLevelId(request.getContext().getEducationLevelId())
                    .birthProvince(request.getContext().getBirthProvince())
                    .birthCity(request.getContext().getBirthCity())
                    .birthDistrict(request.getContext().getBirthDistrict())
                    .birthAddress(request.getContext().getBirthAddress())
                    .dreamProvince(request.getContext().getDreamProvince())
                    .dreamCity(request.getContext().getDreamCity())
                    .dreamDistrict(request.getContext().getDreamDistrict())
                    .dreamAddress(request.getContext().getDreamAddress())
                    .build();
        }

        // 构建DreamRelationship列表
        List<DreamRelationship> relationships = null;
        if (request.getRelationships() != null && !request.getRelationships().isEmpty()) {
            relationships = request.getRelationships().stream()
                    .map(req -> DreamRelationship.builder()
                            .personName(req.getPersonName())
                            .relationshipTypeId(req.getRelationshipTypeId())
                            .intimacyLevel(req.getIntimacyLevel())
                            .intimacyDescription(req.getIntimacyDescription())
                            .notes(req.getNotes())
                            .build())
                    .collect(Collectors.toList());
        }

        // 解析草稿ID（如果有）
        UUID draftId = request.getId() != null ? UUID.fromString(request.getId()) : null;

        Daydream saved = daydreamService.createFullFromDraft(draftId, userId, daydream, user.getBirthDate(), context, relationships);

        // 创建白日梦奖励 10 梦想币
        userWalletService.addCoins(userId, new BigDecimal("10"),
                "创建白日梦奖励", TransactionType.REWARD, saved.getId(), "DAYDREAM");

        // 如果创建时就是公开状态，额外奖励 20 梦想币
        if (Boolean.TRUE.equals(saved.getIsPublic())) {
            userWalletService.addCoins(userId, new BigDecimal("20"),
                    "公开分享白日梦奖励", TransactionType.SHARE, saved.getId(), "DAYDREAM_PUBLISH");
        }

        BigDecimal progress = daydreamService.calculateProgress(saved);
        return Result.success(DaydreamResponse.from(saved, progress));
    }

    @Operation(summary = "暂存白日梦（草稿）")
    @PostMapping("/draft")
    public Result<DaydreamResponse> saveDraft(@Valid @RequestBody UpdateDaydreamRequest request,
                                               Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        Daydream daydream = Daydream.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .coverUrl(request.getCoverUrl())
                .startDate(request.getStartDate())
                .privacy(request.getPrivacy())
                .build();

        // 转换context
        com.rewindai.system.daydream.entity.DreamContext context = null;
        if (request.getContext() != null) {
            context = com.rewindai.system.daydream.entity.DreamContext.builder()
                    .identityId(request.getContext().getIdentityId())
                    .financialAmount(request.getContext().getFinancialAmount())
                    .educationLevelId(request.getContext().getEducationLevelId())
                    .birthProvince(request.getContext().getBirthProvince())
                    .birthCity(request.getContext().getBirthCity())
                    .birthDistrict(request.getContext().getBirthDistrict())
                    .birthAddress(request.getContext().getBirthAddress())
                    .dreamProvince(request.getContext().getDreamProvince())
                    .dreamCity(request.getContext().getDreamCity())
                    .dreamDistrict(request.getContext().getDreamDistrict())
                    .dreamAddress(request.getContext().getDreamAddress())
                    .build();
        }

        // 转换relationships
        List<com.rewindai.system.daydream.entity.DreamRelationship> relationships = null;
        if (request.getRelationships() != null && !request.getRelationships().isEmpty()) {
            relationships = request.getRelationships().stream()
                    .map(req -> com.rewindai.system.daydream.entity.DreamRelationship.builder()
                            .personName(req.getPersonName())
                            .relationshipTypeId(req.getRelationshipTypeId())
                            .intimacyLevel(req.getIntimacyLevel())
                            .intimacyDescription(req.getIntimacyDescription())
                            .notes(req.getNotes())
                            .build())
                    .toList();
        }

        UUID id = request.getId() != null ? UUID.fromString(request.getId()) : null;
        Daydream saved = daydreamService.saveDraft(id, userId, daydream, context, relationships);
        BigDecimal progress = daydreamService.calculateProgress(saved);
        return Result.success(DaydreamResponse.from(saved, progress));
    }

    @Operation(summary = "更新白日梦")
    @PutMapping("/{id}")
    public Result<DaydreamResponse> update(@PathVariable UUID id,
                                            @Valid @RequestBody UpdateDaydreamRequest request,
                                            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        Daydream daydream = Daydream.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .coverUrl(request.getCoverUrl())
                .status(request.getStatus())
                .privacy(request.getPrivacy())
                .build();

        Daydream updated = daydreamService.update(id, userId, daydream);
        BigDecimal progress = daydreamService.calculateProgress(updated);
        return Result.success(DaydreamResponse.from(updated, progress));
    }

    @Operation(summary = "获取白日梦详情")
    @GetMapping("/{id}")
    public Result<DaydreamResponse> getById(@PathVariable UUID id,
                                              Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        Optional<Daydream> daydreamOpt = daydreamService.findAccessibleById(id, userId);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在或无权限访问");
        }

        Daydream daydream = daydreamOpt.get();
        daydreamService.incrementViewCount(id);
        BigDecimal progress = daydreamService.calculateProgress(daydream);

        // 获取作者信息
        User author = userService.findById(daydream.getUserId()).orElse(null);

        // 检查点赞状态
        boolean isLiked = daydreamService.isLikedByUser(id, userId);

        return Result.success(DaydreamResponse.from(daydream, progress, author, isLiked));
    }

    @Operation(summary = "获取白日梦完整详情（包含上下文和关系）")
    @GetMapping("/{id}/detail")
    public Result<DaydreamDetailResponse> getDetailById(@PathVariable UUID id,
                                                          Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        Optional<Daydream> daydreamOpt = daydreamService.findAccessibleById(id, userId);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在或无权限访问");
        }

        Daydream daydream = daydreamOpt.get();
        daydreamService.incrementViewCount(id);
        BigDecimal progress = daydreamService.calculateProgress(daydream);

        DaydreamDetailResponse response = DaydreamDetailResponse.from(daydream, progress);

        // 只有所有者才能看到自己的全局属性
        if (daydream.getUserId().equals(userId)) {
            UserAttribute userAttribute = attributeService.getOrCreateAttribute(userId);
            response.setUserAttribute(UserAttributeResponse.from(userAttribute));
        }

        // 获取梦境上下文：优先找node_id为null的（草稿），没有则找最早的节点
        List<DreamContext> contexts = dreamContextService.getContextsByDreamId(id);
        if (!contexts.isEmpty()) {
            DreamContext contextToUse = null;
            // 优先找node_id为null的草稿上下文
            for (DreamContext ctx : contexts) {
                if (ctx.getNodeId() == null) {
                    contextToUse = ctx;
                    break;
                }
            }
            // 如果没有草稿上下文，找最早的节点上下文（按createdAt DESC排序，最早的在最后）
            if (contextToUse == null) {
                contextToUse = contexts.get(contexts.size() - 1);
            }
            DreamContextDetailResponse contextDetail = DreamContextDetailResponse.from(contextToUse);

            // 填充关联信息
            if (contextToUse.getIdentityId() != null) {
                userIdentityService.findById(contextToUse.getIdentityId())
                        .ifPresent(identity -> contextDetail.setIdentity(UserIdentityResponse.from(identity)));
            }
            if (contextToUse.getEducationLevelId() != null) {
                educationLevelService.findById(contextToUse.getEducationLevelId())
                        .ifPresent(level -> contextDetail.setEducationLevel(EducationLevelResponse.from(level)));
            }

            response.setContext(contextDetail);
        }

        // 获取社会关系列表：优先找node_id为null的（草稿），没有则找最早的节点
        List<DreamRelationship> relationships = dreamRelationshipService.getRelationshipsByDream(id);
        if (!relationships.isEmpty()) {
            // 检查是否有node_id为null的草稿关系
            boolean hasDraftRelationships = relationships.stream().anyMatch(rel -> rel.getNodeId() == null);
            if (hasDraftRelationships) {
                // 只使用node_id为null的草稿关系
                relationships = relationships.stream()
                        .filter(rel -> rel.getNodeId() == null)
                        .toList();
            } else {
                // 找最早节点的关系（需要先确定最早的节点）
                // 这里简化处理：使用所有关系，前端会处理
            }
        }
        List<DreamRelationshipDetailResponse> relationshipDetails = relationships.stream()
                .map(rel -> {
                    DreamRelationshipDetailResponse detail = DreamRelationshipDetailResponse.from(rel);
                    relationshipTypeService.findById(rel.getRelationshipTypeId())
                            .ifPresent(type -> detail.setRelationshipType(RelationshipTypeResponse.from(type)));
                    return detail;
                })
                .toList();
        response.setRelationships(relationshipDetails);

        return Result.success(response);
    }

    @Operation(summary = "获取公开白日梦详情（无需登录）")
    @GetMapping("/public/{id}")
    public Result<DaydreamResponse> getPublicById(@PathVariable UUID id) {
        Optional<Daydream> daydreamOpt = daydreamService.findById(id)
                .filter(d -> d.getIsPublic() && d.getStatus() == DreamStatus.ACTIVE);

        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在");
        }

        Daydream daydream = daydreamOpt.get();
        daydreamService.incrementViewCount(id);
        BigDecimal progress = daydreamService.calculateProgress(daydream);

        // 获取作者信息
        User author = userService.findById(daydream.getUserId()).orElse(null);

        return Result.success(DaydreamResponse.from(daydream, progress, author));
    }

    @Operation(summary = "结束白日梦")
    @PostMapping("/{id}/finish")
    public Result<DaydreamResponse> finish(@PathVariable UUID id,
                                            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Daydream daydream = daydreamService.finish(id, userId);
        BigDecimal progress = daydreamService.calculateProgress(daydream);
        return Result.success(DaydreamResponse.from(daydream, progress));
    }

    @Operation(summary = "归档白日梦")
    @DeleteMapping("/{id}")
    public Result<Void> archive(@PathVariable UUID id,
                                 Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.archive(id, userId);
        return Result.success();
    }

    @Operation(summary = "恢复已归档的白日梦")
    @PostMapping("/{id}/restore")
    public Result<DaydreamResponse> restore(@PathVariable UUID id,
                                             Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Daydream daydream = daydreamService.restore(id, userId);
        BigDecimal progress = daydreamService.calculateProgress(daydream);
        return Result.success(DaydreamResponse.from(daydream, progress));
    }

    @Operation(summary = "永久删除已归档的白日梦")
    @DeleteMapping("/{id}/permanent")
    public Result<Void> permanentDelete(@PathVariable UUID id,
                                         Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.permanentDelete(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取我的归档梦境列表")
    @GetMapping("/my/archived")
    public Result<Page<DaydreamResponse>> getMyArchivedDaydreams(
            @PageableDefault(size = 20, sort = "deletedAt") Pageable pageable,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Page<Daydream> daydreams = daydreamService.getArchivedDaydreams(userId, pageable);
        return Result.success(daydreams.map(d -> DaydreamResponse.from(d, daydreamService.calculateProgress(d))));
    }

    @Operation(summary = "公开分享白日梦")
    @PostMapping("/{id}/publish")
    public Result<DaydreamResponse> publish(@PathVariable UUID id,
                                              Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Daydream daydream = daydreamService.publish(id, userId);

        // 公开分享奖励 20 梦想币
        userWalletService.addCoins(userId, new BigDecimal("20"),
                "公开分享白日梦奖励", TransactionType.SHARE, id, "DAYDREAM_PUBLISH");

        BigDecimal progress = daydreamService.calculateProgress(daydream);
        return Result.success(DaydreamResponse.from(daydream, progress));
    }

    @Operation(summary = "取消公开")
    @PostMapping("/{id}/unpublish")
    public Result<DaydreamResponse> unpublish(@PathVariable UUID id,
                                                Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Daydream daydream = daydreamService.unpublish(id, userId);
        BigDecimal progress = daydreamService.calculateProgress(daydream);
        return Result.success(DaydreamResponse.from(daydream, progress));
    }

    @Operation(summary = "切换点赞状态（点赞/取消点赞）")
    @PostMapping("/{id}/toggle-like")
    public Result<LikeStatusResponse> toggleLike(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Object[] result = daydreamService.toggleLike(id, userId);
        LikeStatusResponse status = LikeStatusResponse.builder()
                .liked((Boolean) result[0])
                .likeCount((Long) result[1])
                .build();
        return Result.success(status);
    }

    @Operation(summary = "点赞白日梦（已废弃，请使用toggle-like）")
    @Deprecated
    @PostMapping("/{id}/like")
    public Result<Void> like(@PathVariable UUID id) {
        daydreamService.incrementLikeCount(id);
        return Result.success();
    }

    @Operation(summary = "取消点赞（已废弃，请使用toggle-like）")
    @Deprecated
    @PostMapping("/{id}/unlike")
    public Result<Void> unlike(@PathVariable UUID id) {
        daydreamService.decrementLikeCount(id);
        return Result.success();
    }

    @Operation(summary = "分享白日梦")
    @PostMapping("/{id}/share")
    public Result<Void> share(@PathVariable UUID id) {
        daydreamService.incrementShareCount(id);
        return Result.success();
    }

    @Operation(summary = "获取我的活跃白日梦列表")
    @GetMapping("/my/active")
    public Result<List<DaydreamResponse>> getMyActiveDaydreams(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<Daydream> daydreams = daydreamService.getActiveDaydreams(userId);
        List<DaydreamResponse> responses = daydreams.stream()
                .map(d -> DaydreamResponse.from(d, daydreamService.calculateProgress(d)))
                .toList();
        return Result.success(responses);
    }

    @Operation(summary = "获取我的白日梦列表")
    @GetMapping("/my")
    public Result<Page<DaydreamResponse>> getMyDaydreams(
            @RequestParam(required = false) Integer status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        Page<Daydream> daydreams;
        if (status != null) {
            DreamStatus dreamStatus = DreamStatus.fromCode(status);
            daydreams = daydreamService.getUserDaydreamsByStatus(userId, dreamStatus, pageable);
        } else {
            daydreams = daydreamService.getUserDaydreams(userId, pageable);
        }

        return Result.success(daydreams.map(d -> DaydreamResponse.from(d, daydreamService.calculateProgress(d))));
    }

    @Operation(summary = "获取公开白日梦列表")
    @GetMapping("/public")
    public Result<Page<DaydreamResponse>> getPublicDaydreams(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        Page<Daydream> daydreams = daydreamService.getPublicDaydreams(pageable);

        // 批量获取作者信息
        var userIds = daydreams.getContent().stream()
                .map(Daydream::getUserId)
                .collect(Collectors.toSet());
        var userMap = userService.findAllByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 批量获取点赞状态
        UUID currentUserId = authentication != null ? UUID.fromString(authentication.getName()) : null;
        java.util.Map<UUID, Boolean> likeStatusMap = currentUserId != null
                ? daydreamService.getLikeStatusBatch(
                        daydreams.getContent().stream().map(Daydream::getId).collect(Collectors.toList()),
                        currentUserId)
                : java.util.Collections.emptyMap();

        return Result.success(daydreams.map(d -> {
            BigDecimal progress = daydreamService.calculateProgress(d);
            User author = userMap.get(d.getUserId());
            Boolean isLiked = likeStatusMap.getOrDefault(d.getId(), false);
            return DaydreamResponse.from(d, progress, author, isLiked);
        }));
    }

    @Operation(summary = "搜索公开白日梦")
    @GetMapping("/search")
    public Result<Page<DaydreamResponse>> searchPublicDaydreams(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        Page<Daydream> daydreams = daydreamService.searchPublicDaydreams(keyword, pageable);

        // 批量获取作者信息
        var userIds = daydreams.getContent().stream()
                .map(Daydream::getUserId)
                .collect(Collectors.toSet());
        var userMap = userService.findAllByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 批量获取点赞状态
        UUID currentUserId = authentication != null ? UUID.fromString(authentication.getName()) : null;
        java.util.Map<UUID, Boolean> likeStatusMap = currentUserId != null
                ? daydreamService.getLikeStatusBatch(
                        daydreams.getContent().stream().map(Daydream::getId).collect(Collectors.toList()),
                        currentUserId)
                : java.util.Collections.emptyMap();

        return Result.success(daydreams.map(d -> {
            BigDecimal progress = daydreamService.calculateProgress(d);
            User author = userMap.get(d.getUserId());
            Boolean isLiked = likeStatusMap.getOrDefault(d.getId(), false);
            return DaydreamResponse.from(d, progress, author, isLiked);
        }));
    }

    @Operation(summary = "获取白日梦分支列表")
    @GetMapping("/{id}/branches")
    public Result<List<DreamBranch>> getBranches(@PathVariable UUID id,
                                                   Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findAccessibleById(id, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在或无权限访问"));
        return Result.success(daydreamService.getBranches(id));
    }

    @Operation(summary = "获取用户属性")
    @GetMapping("/attributes")
    public Result<UserAttributeResponse> getAttributes(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        UserAttribute attribute = attributeService.getOrCreateAttribute(userId);
        return Result.success(UserAttributeResponse.from(attribute));
    }

    @Operation(summary = "获取精选梦境（按点赞数排序）")
    @GetMapping("/featured")
    public Result<Page<DaydreamResponse>> getFeaturedDaydreams(
            @PageableDefault(size = 20, sort = "likeCount") Pageable pageable,
            Authentication authentication) {
        Page<Daydream> daydreams = daydreamRepository.findFeaturedDaydreams(pageable);

        // 批量获取作者信息
        var userIds = daydreams.getContent().stream()
                .map(Daydream::getUserId)
                .collect(Collectors.toSet());
        var userMap = userService.findAllByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 批量获取点赞状态
        UUID currentUserId = authentication != null ? UUID.fromString(authentication.getName()) : null;
        java.util.Map<UUID, Boolean> likeStatusMap = currentUserId != null
                ? daydreamService.getLikeStatusBatch(
                        daydreams.getContent().stream().map(Daydream::getId).collect(Collectors.toList()),
                        currentUserId)
                : java.util.Collections.emptyMap();

        return Result.success(daydreams.map(d -> {
            BigDecimal progress = daydreamService.calculateProgress(d);
            User author = userMap.get(d.getUserId());
            Boolean isLiked = likeStatusMap.getOrDefault(d.getId(), false);
            return DaydreamResponse.from(d, progress, author, isLiked);
        }));
    }
}
