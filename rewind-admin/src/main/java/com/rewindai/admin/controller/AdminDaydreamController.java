package com.rewindai.admin.controller;

import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.repository.DaydreamRepository;
import com.rewindai.system.dream.enums.DreamStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

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

    @GetMapping("/list")
    @Operation(summary = "获取白日梦列表", description = "分页获取所有白日梦列表（包括已结束、已删除的），支持搜索和状态筛选")
    public Result<Page<Daydream>> getDaydreamList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) DreamStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Daydream> daydreams;
        if (status != null) {
            daydreams = daydreamRepository.searchDaydreamsByStatus(status, keyword, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            daydreams = daydreamRepository.searchAllDaydreams(keyword, pageable);
        } else {
            daydreams = daydreamRepository.findAll(pageable);
        }
        return Result.success(daydreams);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取白日梦详情", description = "根据ID获取白日梦详情")
    public Result<Daydream> getDaydreamById(@PathVariable UUID id) {
        return daydreamRepository.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("白日梦不存在"));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新白日梦状态", description = "后台管理更新白日梦状态")
    public Result<Daydream> updateDaydreamStatus(
            @PathVariable UUID id,
            @RequestParam DreamStatus status) {
        return daydreamRepository.findById(id)
                .map(daydream -> {
                    daydream.setStatus(status);
                    if (status == DreamStatus.ARCHIVED) {
                        daydream.setIsFinished(true);
                        daydream.setIsActive(false);
                    } else if (status == DreamStatus.ACTIVE) {
                        daydream.setIsFinished(false);
                        daydream.setIsActive(true);
                    }
                    Daydream saved = daydreamRepository.save(daydream);
                    log.info("更新白日梦状态成功: id={}, status={}", id, status);
                    return Result.success(saved);
                })
                .orElse(Result.notFound("白日梦不存在"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除白日梦", description = "后台管理删除白日梦（软删除）")
    public Result<Void> deleteDaydream(@PathVariable UUID id) {
        Optional<Daydream> daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isPresent()) {
            Daydream daydream = daydreamOpt.get();
            daydream.setStatus(DreamStatus.ARCHIVED);
            daydream.setIsFinished(true);
            daydream.setIsActive(false);
            daydreamRepository.save(daydream);
            log.info("后台删除白日梦成功: id={}", id);
            return Result.success();
        }
        return Result.notFound("白日梦不存在");
    }
}
