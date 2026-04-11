package com.rewindai.admin.controller;

import com.rewindai.admin.dto.AdminDaydreamResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.common.core.util.CsvExportUtil;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.repository.DaydreamRepository;
import com.rewindai.system.daydream.enums.DreamStatus;
import com.rewindai.system.daydream.enums.ReviewStatus;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 后台管理 - 白日梦管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/daydream")
@RequiredArgsConstructor
@Tag(name = "后台管理-白日梦管理", description = "后台管理系统白日梦管理接口")
public class AdminDaydreamController {

    private final DaydreamRepository daydreamRepository;
    private final UserRepository userRepository;

    @GetMapping("/list")
    @Operation(summary = "获取白日梦列表", description = "分页获取所有白日梦列表（包括已结束、已删除的），支持搜索和状态筛选")
    public Result<Page<AdminDaydreamResponse>> getDaydreamList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) DreamStatus status,
            @RequestParam(required = false) Boolean isPublic,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Daydream> daydreams;
        if (status != null && isPublic != null) {
            daydreams = daydreamRepository.searchDaydreamsByStatusAndPublic(status, isPublic, keyword, pageable);
        } else if (status != null) {
            daydreams = daydreamRepository.searchDaydreamsByStatus(status, keyword, pageable);
        } else if (isPublic != null) {
            daydreams = daydreamRepository.searchDaydreamsByPublic(isPublic, keyword, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            daydreams = daydreamRepository.searchAllDaydreams(keyword, pageable);
        } else {
            daydreams = daydreamRepository.findAll(pageable);
        }

        // 批量获取用户信息
        var userIds = daydreams.getContent().stream()
                .map(Daydream::getUserId)
                .collect(Collectors.toSet());
        var users = userRepository.findAllByIdIn(userIds);
        var userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 转换为响应DTO并填充用户信息
        Page<AdminDaydreamResponse> responsePage = daydreams.map(daydream -> {
            var response = AdminDaydreamResponse.fromEntity(daydream);
            var user = userMap.get(daydream.getUserId());
            if (user != null) {
                response.setUserNickname(user.getNickname());
                response.setUserAvatar(user.getAvatarUrl());
            }
            return response;
        });

        return Result.success(responsePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取白日梦详情", description = "根据ID获取白日梦详情")
    public Result<AdminDaydreamResponse> getDaydreamById(@PathVariable UUID id) {
        var daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在");
        }
        var daydream = daydreamOpt.get();
        var response = AdminDaydreamResponse.fromEntity(daydream);
        // 填充用户信息
        userRepository.findById(daydream.getUserId()).ifPresent(user -> {
            response.setUserNickname(user.getNickname());
            response.setUserAvatar(user.getAvatarUrl());
        });
        return Result.success(response);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新白日梦状态", description = "后台管理更新白日梦状态")
    public Result<AdminDaydreamResponse> updateDaydreamStatus(
            @PathVariable UUID id,
            @RequestParam DreamStatus status) {
        var daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在");
        }
        var daydream = daydreamOpt.get();
        daydream.setStatus(status);
        if (status == DreamStatus.ARCHIVED || status == DreamStatus.DELETED) {
            daydream.setIsFinished(true);
            daydream.setIsActive(false);
        } else if (status == DreamStatus.ACTIVE) {
            daydream.setIsFinished(false);
            daydream.setIsActive(true);
        }
        Daydream saved = daydreamRepository.save(daydream);
        log.info("更新白日梦状态成功: id={}, status={}", id, status);

        var response = AdminDaydreamResponse.fromEntity(saved);
        return Result.success(response);
    }

    @PutMapping("/{id}/privacy")
    @Operation(summary = "更新白日梦公开状态", description = "设置白日梦是否公开")
    public Result<AdminDaydreamResponse> updateDaydreamPrivacy(
            @PathVariable UUID id,
            @RequestParam Boolean isPublic) {
        var daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在");
        }
        var daydream = daydreamOpt.get();
        daydream.setIsPublic(isPublic);
        daydreamRepository.save(daydream);
        log.info("后台更新白日梦公开状态: id={}, isPublic={}", id, isPublic);

        var response = AdminDaydreamResponse.fromEntity(daydream);
        return Result.success(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除白日梦", description = "后台管理删除白日梦（软删除）")
    public Result<Void> deleteDaydream(@PathVariable UUID id) {
        var daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在");
        }
        var daydream = daydreamOpt.get();
        daydream.setDeletedAt(OffsetDateTime.now());
        daydream.setStatus(DreamStatus.DELETED);
        daydream.setIsFinished(true);
        daydream.setIsActive(false);
        daydreamRepository.save(daydream);
        log.info("后台删除白日梦成功: id={}", id);
        return Result.success();
    }

    @GetMapping("/stats")
    @Operation(summary = "获取白日梦统计", description = "获取白日梦总数、公开数等统计")
    public Result<Map<String, Object>> getDaydreamStats() {
        long totalCount = daydreamRepository.count();
        long publicCount = daydreamRepository.countByIsPublicTrue();
        long totalLikes = daydreamRepository.sumLikeCount();
        long totalComments = daydreamRepository.sumCommentCount();
        var totalRewardAmount = daydreamRepository.sumRewardAmount();

        return Result.success(Map.of(
                "totalCount", totalCount,
                "publicCount", publicCount,
                "totalLikes", totalLikes,
                "totalComments", totalComments,
                "totalRewardAmount", totalRewardAmount
        ));
    }

    // ========== 内容审核功能 ==========

    @PutMapping("/{id}/review/approve")
    @Operation(summary = "通过内容审核", description = "管理员审核通过白日梦内容")
    public Result<AdminDaydreamResponse> approveReview(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        var daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在");
        }
        var daydream = daydreamOpt.get();
        daydream.setReviewStatus(ReviewStatus.APPROVED);
        daydream.setReviewedAt(OffsetDateTime.now());
        daydream.setReviewReason(reason);
        // 审核通过后设置为公开
        daydream.setIsPublic(true);
        Daydream saved = daydreamRepository.save(daydream);
        log.info("审核通过白日梦: id={}, reason={}", id, reason);

        var response = AdminDaydreamResponse.fromEntity(saved);
        userRepository.findById(saved.getUserId()).ifPresent(user -> {
            response.setUserNickname(user.getNickname());
            response.setUserAvatar(user.getAvatarUrl());
        });
        return Result.success(response);
    }

    @PutMapping("/{id}/review/reject")
    @Operation(summary = "驳回内容审核", description = "管理员驳回白日梦内容")
    public Result<AdminDaydreamResponse> rejectReview(
            @PathVariable UUID id,
            @RequestParam String reason) {
        var daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在");
        }
        var daydream = daydreamOpt.get();
        daydream.setReviewStatus(ReviewStatus.REJECTED);
        daydream.setReviewedAt(OffsetDateTime.now());
        daydream.setReviewReason(reason);
        // 审核驳回后设置为私有
        daydream.setIsPublic(false);
        Daydream saved = daydreamRepository.save(daydream);
        log.info("审核驳回白日梦: id={}, reason={}", id, reason);

        var response = AdminDaydreamResponse.fromEntity(saved);
        userRepository.findById(saved.getUserId()).ifPresent(user -> {
            response.setUserNickname(user.getNickname());
            response.setUserAvatar(user.getAvatarUrl());
        });
        return Result.success(response);
    }

    // ========== 精选功能 ==========

    @PutMapping("/{id}/featured")
    @Operation(summary = "设为精选", description = "将白日梦设为精选")
    public Result<AdminDaydreamResponse> setFeatured(@PathVariable UUID id) {
        var daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在");
        }
        var daydream = daydreamOpt.get();
        daydream.setIsFeatured(true);
        daydream.setFeaturedAt(OffsetDateTime.now());
        Daydream saved = daydreamRepository.save(daydream);
        log.info("设为精选白日梦: id={}", id);

        var response = AdminDaydreamResponse.fromEntity(saved);
        userRepository.findById(saved.getUserId()).ifPresent(user -> {
            response.setUserNickname(user.getNickname());
            response.setUserAvatar(user.getAvatarUrl());
        });
        return Result.success(response);
    }

    @DeleteMapping("/{id}/featured")
    @Operation(summary = "取消精选", description = "取消白日梦的精选状态")
    public Result<AdminDaydreamResponse> unsetFeatured(@PathVariable UUID id) {
        var daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在");
        }
        var daydream = daydreamOpt.get();
        daydream.setIsFeatured(false);
        daydream.setFeaturedAt(null);
        Daydream saved = daydreamRepository.save(daydream);
        log.info("取消精选白日梦: id={}", id);

        var response = AdminDaydreamResponse.fromEntity(saved);
        userRepository.findById(saved.getUserId()).ifPresent(user -> {
            response.setUserNickname(user.getNickname());
            response.setUserAvatar(user.getAvatarUrl());
        });
        return Result.success(response);
    }

    // ========== 置顶功能 ==========

    @PutMapping("/{id}/pinned")
    @Operation(summary = "设为置顶", description = "将白日梦设为置顶")
    public Result<AdminDaydreamResponse> setPinned(@PathVariable UUID id) {
        var daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在");
        }
        var daydream = daydreamOpt.get();
        daydream.setIsPinned(true);
        daydream.setPinnedAt(OffsetDateTime.now());
        Daydream saved = daydreamRepository.save(daydream);
        log.info("设为置顶白日梦: id={}", id);

        var response = AdminDaydreamResponse.fromEntity(saved);
        userRepository.findById(saved.getUserId()).ifPresent(user -> {
            response.setUserNickname(user.getNickname());
            response.setUserAvatar(user.getAvatarUrl());
        });
        return Result.success(response);
    }

    @DeleteMapping("/{id}/pinned")
    @Operation(summary = "取消置顶", description = "取消白日梦的置顶状态")
    public Result<AdminDaydreamResponse> unsetPinned(@PathVariable UUID id) {
        var daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isEmpty()) {
            return Result.notFound("白日梦不存在");
        }
        var daydream = daydreamOpt.get();
        daydream.setIsPinned(false);
        daydream.setPinnedAt(null);
        Daydream saved = daydreamRepository.save(daydream);
        log.info("取消置顶白日梦: id={}", id);

        var response = AdminDaydreamResponse.fromEntity(saved);
        userRepository.findById(saved.getUserId()).ifPresent(user -> {
            response.setUserNickname(user.getNickname());
            response.setUserAvatar(user.getAvatarUrl());
        });
        return Result.success(response);
    }

    @GetMapping("/export")
    @Operation(summary = "导出梦境数据", description = "导出指定时间范围内的梦境数据为CSV")
    public ResponseEntity<byte[]> exportDaydreams(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) {

        List<Daydream> daydreams = daydreamRepository.findByDateRangeForExport(startTime, endTime);

        String[] headers = {
            "梦境ID", "用户ID", "标题", "描述", "状态", "是否公开", "是否精选", "是否置顶",
            "浏览数", "点赞数", "评论数", "打赏金额", "审核状态", "审核时间",
            "创建时间", "更新时间"
        };

        return CsvExportUtil.export("daydreams", headers, daydreams, d -> new String[]{
            d.getId() != null ? d.getId().toString() : "",
            d.getUserId() != null ? d.getUserId().toString() : "",
            CsvExportUtil.escape(d.getTitle()),
            CsvExportUtil.escape(d.getDescription()),
            d.getStatus() != null ? d.getStatus().name() : "",
            d.getIsPublic() != null ? d.getIsPublic().toString() : "",
            d.getIsFeatured() != null ? d.getIsFeatured().toString() : "",
            d.getIsPinned() != null ? d.getIsPinned().toString() : "",
            d.getViewCount() != null ? d.getViewCount().toString() : "0",
            d.getLikeCount() != null ? d.getLikeCount().toString() : "0",
            d.getCommentCount() != null ? d.getCommentCount().toString() : "0",
            d.getRewardAmount() != null ? d.getRewardAmount().toString() : "0",
            d.getReviewStatus() != null ? d.getReviewStatus().name() : "",
            CsvExportUtil.formatDateTime(d.getReviewedAt()),
            CsvExportUtil.formatDateTime(d.getCreatedAt()),
            CsvExportUtil.formatDateTime(d.getUpdatedAt())
        });
    }
}
