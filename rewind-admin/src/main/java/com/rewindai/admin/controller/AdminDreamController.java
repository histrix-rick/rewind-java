package com.rewindai.admin.controller;

import com.rewindai.common.core.result.Result;
import com.rewindai.system.dream.entity.Dream;
import com.rewindai.system.dream.enums.DreamStatus;
import com.rewindai.system.dream.repository.DreamRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 后台管理 - 梦境管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/dream")
@RequiredArgsConstructor
@Tag(name = "后台管理-梦境管理", description = "后台管理系统梦境管理接口")
public class AdminDreamController {

    private final DreamRepository dreamRepository;

    @GetMapping("/list")
    @Operation(summary = "获取梦境列表", description = "分页获取梦境列表，支持搜索")
    public Result<Page<Dream>> getDreamList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) DreamStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<Dream> dreams;
        if (status != null) {
            dreams = dreamRepository.searchDreamsByStatus(status, keyword, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            dreams = dreamRepository.searchAllDreams(keyword, pageable);
        } else {
            dreams = dreamRepository.findAll(pageable);
        }
        return Result.success(dreams);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取梦境详情", description = "根据ID获取梦境详情")
    public Result<Dream> getDreamById(@PathVariable UUID id) {
        return dreamRepository.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("梦境不存在"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除梦境", description = "删除指定梦境")
    public Result<Void> deleteDream(@PathVariable UUID id) {
        if (dreamRepository.existsById(id)) {
            dreamRepository.deleteById(id);
            return Result.success();
        }
        return Result.notFound("梦境不存在");
    }
}
