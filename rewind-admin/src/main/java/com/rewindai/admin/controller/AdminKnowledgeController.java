package com.rewindai.admin.controller;

import com.rewindai.common.core.result.Result;
import com.rewindai.system.ticket.entity.KnowledgeBase;
import com.rewindai.system.ticket.entity.KnowledgeCategory;
import com.rewindai.system.ticket.service.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库管理控制器
 *
 * @author Rewind.ai Team
 */
@RestController
@RequestMapping("/admin/knowledge")
@RequiredArgsConstructor
@Tag(name = "知识库管理", description = "知识库管理接口")
public class AdminKnowledgeController {

    private final KnowledgeBaseService knowledgeService;

    @GetMapping("/list")
    @Operation(summary = "获取知识库列表", description = "分页获取知识库列表")
    public Result<Page<KnowledgeBase>> getKnowledgeList(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "是否只查看已发布") @RequestParam(defaultValue = "false") Boolean publishedOnly,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "sortOrder") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "ASC") Sort.Direction direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<KnowledgeBase> knowledgeList;

        if (keyword != null && !keyword.isEmpty()) {
            if (publishedOnly) {
                knowledgeList = knowledgeService.searchPublishedKnowledge(keyword, pageable);
            } else {
                knowledgeList = knowledgeService.searchKnowledge(keyword, pageable);
            }
        } else if (category != null && !category.isEmpty()) {
            knowledgeList = knowledgeService.findByCategory(category, pageable);
        } else if (publishedOnly) {
            knowledgeList = knowledgeService.findPublished(pageable);
        } else {
            knowledgeList = knowledgeService.findAll(pageable);
        }

        return Result.success(knowledgeList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取知识库详情", description = "根据ID获取知识库详情")
    public Result<KnowledgeBase> getKnowledgeDetail(@PathVariable Long id) {
        return knowledgeService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("知识库不存在"));
    }

    @PostMapping
    @Operation(summary = "创建知识库", description = "创建新的知识库")
    public Result<KnowledgeBase> createKnowledge(@RequestBody KnowledgeBase knowledge) {
        return Result.success(knowledgeService.createKnowledge(knowledge));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新知识库", description = "更新知识库信息")
    public Result<KnowledgeBase> updateKnowledge(@PathVariable Long id, @RequestBody KnowledgeBase knowledge) {
        knowledge.setId(id);
        return Result.success(knowledgeService.updateKnowledge(knowledge));
    }

    @PutMapping("/{id}/view")
    @Operation(summary = "增加浏览次数", description = "增加知识库的浏览次数")
    public Result<KnowledgeBase> incrementViewCount(@PathVariable Long id) {
        KnowledgeBase knowledge = knowledgeService.incrementViewCount(id);
        if (knowledge != null) {
            return Result.success(knowledge);
        }
        return Result.notFound("知识库不存在");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除知识库", description = "删除知识库")
    public Result<Void> deleteKnowledge(@PathVariable Long id) {
        knowledgeService.deleteKnowledge(id);
        return Result.success();
    }

    @GetMapping("/categories")
    @Operation(summary = "获取知识库分类列表", description = "获取所有知识库分类")
    public Result<List<KnowledgeCategory>> getCategories() {
        return Result.success(knowledgeService.getAllCategories());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "按分类获取已发布的知识库", description = "按分类获取已发布的知识库列表")
    public Result<List<KnowledgeBase>> getPublishedByCategory(@PathVariable String category) {
        return Result.success(knowledgeService.findPublishedByCategory(category));
    }

    @PostMapping("/categories")
    @Operation(summary = "创建知识库分类", description = "创建新的知识库分类")
    public Result<KnowledgeCategory> createCategory(@RequestBody KnowledgeCategory category) {
        return Result.success(knowledgeService.createCategory(category));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "更新知识库分类", description = "更新知识库分类")
    public Result<KnowledgeCategory> updateCategory(@PathVariable Long id, @RequestBody KnowledgeCategory category) {
        category.setId(id);
        return Result.success(knowledgeService.updateCategory(category));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "删除知识库分类", description = "删除知识库分类")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        knowledgeService.deleteCategory(id);
        return Result.success();
    }
}
