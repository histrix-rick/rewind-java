package com.rewindai.app.controller;

import com.rewindai.app.dto.CreateDreamRequest;
import com.rewindai.app.dto.DreamResponse;
import com.rewindai.app.dto.UpdateDreamRequest;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.dream.entity.Dream;
import com.rewindai.system.dream.enums.DreamPrivacy;
import com.rewindai.system.dream.enums.DreamStatus;
import com.rewindai.system.dream.service.DreamService;
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
import java.util.Optional;
import java.util.UUID;

/**
 * 梦境 Controller
 *
 * @author Rewind.ai Team
 */
@Tag(name = "梦境管理", description = "梦境相关接口")
@RestController
@RequestMapping("/api/dreams")
@RequiredArgsConstructor
public class DreamController {

    private final DreamService dreamService;
    private final UserWalletService userWalletService;

    @Operation(summary = "创建梦境")
    @PostMapping
    public Result<DreamResponse> create(@Valid @RequestBody CreateDreamRequest request,
                                         Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        Dream dream = Dream.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .coverUrl(request.getCoverUrl())
                .dreamDate(request.getDreamDateAsOffsetDateTime())
                .status(request.getStatus())
                .privacy(request.getPrivacy())
                .isPublic(request.getPrivacy() == DreamPrivacy.PUBLIC)
                .tags(request.getTags())
                .mood(request.getMood())
                .weather(request.getWeather())
                .durationMinutes(request.getDurationMinutes())
                .isLucid(request.getIsLucid())
                .isRecurring(request.getIsRecurring())
                .isNightmare(request.getIsNightmare())
                .build();

        Dream saved = dreamService.create(dream);

        // 创建梦境奖励 10 梦想币
        userWalletService.addCoins(userId, new BigDecimal("10"),
                "创建梦境奖励", TransactionType.REWARD, saved.getId(), "DREAM");

        return Result.success(DreamResponse.from(saved));
    }

    @Operation(summary = "更新梦境")
    @PutMapping("/{id}")
    public Result<DreamResponse> update(@PathVariable UUID id,
                                         @Valid @RequestBody UpdateDreamRequest request,
                                         Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        Dream dream = Dream.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .coverUrl(request.getCoverUrl())
                .dreamDate(request.getDreamDateAsOffsetDateTime())
                .status(request.getStatus())
                .privacy(request.getPrivacy())
                .tags(request.getTags())
                .mood(request.getMood())
                .weather(request.getWeather())
                .durationMinutes(request.getDurationMinutes())
                .isLucid(request.getIsLucid())
                .isRecurring(request.getIsRecurring())
                .isNightmare(request.getIsNightmare())
                .build();

        Dream updated = dreamService.update(id, userId, dream);
        return Result.success(DreamResponse.from(updated));
    }

    @Operation(summary = "获取梦境详情")
    @GetMapping("/{id}")
    public Result<DreamResponse> getById(@PathVariable UUID id,
                                          Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        Optional<Dream> dreamOpt = dreamService.findByIdAndUserId(id, userId);
        if (dreamOpt.isEmpty()) {
            return Result.notFound("梦境不存在");
        }

        Dream dream = dreamOpt.get();
        dreamService.incrementViewCount(id);
        return Result.success(DreamResponse.from(dream));
    }

    @Operation(summary = "获取公开梦境详情（无需登录）")
    @GetMapping("/public/{id}")
    public Result<DreamResponse> getPublicById(@PathVariable UUID id) {
        Optional<Dream> dreamOpt = dreamService.findById(id)
                .filter(dream -> dream.getIsPublic() && dream.getStatus() == DreamStatus.ACTIVE);

        if (dreamOpt.isEmpty()) {
            return Result.notFound("梦境不存在");
        }

        Dream dream = dreamOpt.get();
        dreamService.incrementViewCount(id);
        return Result.success(DreamResponse.from(dream));
    }

    @Operation(summary = "删除梦境")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable UUID id,
                               Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        dreamService.delete(id, userId);
        return Result.success();
    }

    @Operation(summary = "公开分享梦境")
    @PostMapping("/{id}/publish")
    public Result<DreamResponse> publish(@PathVariable UUID id,
                                          Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Dream dream = dreamService.publish(id, userId);

        // 公开分享奖励 20 梦想币
        userWalletService.addCoins(userId, new BigDecimal("20"),
                "公开分享梦境奖励", TransactionType.SHARE, id, "DREAM_PUBLISH");

        return Result.success(DreamResponse.from(dream));
    }

    @Operation(summary = "取消公开")
    @PostMapping("/{id}/unpublish")
    public Result<DreamResponse> unpublish(@PathVariable UUID id,
                                            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Dream dream = dreamService.unpublish(id, userId);
        return Result.success(DreamResponse.from(dream));
    }

    @Operation(summary = "点赞梦境")
    @PostMapping("/{id}/like")
    public Result<Void> like(@PathVariable UUID id) {
        dreamService.incrementLikeCount(id);
        return Result.success();
    }

    @Operation(summary = "取消点赞")
    @PostMapping("/{id}/unlike")
    public Result<Void> unlike(@PathVariable UUID id) {
        dreamService.decrementLikeCount(id);
        return Result.success();
    }

    @Operation(summary = "分享梦境")
    @PostMapping("/{id}/share")
    public Result<Void> share(@PathVariable UUID id) {
        dreamService.incrementShareCount(id);
        return Result.success();
    }

    @Operation(summary = "获取我的梦境列表")
    @GetMapping("/my")
    public Result<Page<DreamResponse>> getMyDreams(
            @RequestParam(required = false) DreamStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        Page<Dream> dreams;
        if (status != null) {
            dreams = dreamService.getUserDreamsByStatus(userId, status, pageable);
        } else {
            dreams = dreamService.getUserDreams(userId, pageable);
        }

        return Result.success(dreams.map(DreamResponse::from));
    }

    @Operation(summary = "获取公开梦境列表")
    @GetMapping("/public")
    public Result<Page<DreamResponse>> getPublicDreams(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<Dream> dreams = dreamService.getPublicDreams(pageable);
        return Result.success(dreams.map(DreamResponse::from));
    }

    @Operation(summary = "搜索公开梦境")
    @GetMapping("/search")
    public Result<Page<DreamResponse>> searchPublicDreams(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<Dream> dreams = dreamService.searchPublicDreams(keyword, pageable);
        return Result.success(dreams.map(DreamResponse::from));
    }
}
