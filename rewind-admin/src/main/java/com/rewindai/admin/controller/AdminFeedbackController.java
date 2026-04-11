package com.rewindai.admin.controller;

import com.rewindai.common.core.result.Result;
import com.rewindai.system.ticket.entity.FeedbackCategory;
import com.rewindai.system.ticket.entity.UserFeedback;
import com.rewindai.system.ticket.enums.FeedbackStatus;
import com.rewindai.system.ticket.service.UserFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 反馈管理控制器
 *
 * @author Rewind.ai Team
 */
@RestController
@RequestMapping("/admin/feedback")
@RequiredArgsConstructor
@Tag(name = "反馈管理", description = "用户反馈管理接口")
public class AdminFeedbackController {

    private final UserFeedbackService feedbackService;

    @GetMapping("/list")
    @Operation(summary = "获取反馈列表", description = "分页获取反馈列表")
    public Result<Page<UserFeedback>> getFeedbackList(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "反馈状态") @RequestParam(required = false) String status,
            @Parameter(description = "反馈分类") @RequestParam(required = false) String category,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createdTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<UserFeedback> feedbacks;

        if (keyword != null && !keyword.isEmpty()) {
            feedbacks = feedbackService.searchFeedbacks(keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            feedbacks = feedbackService.findByStatus(FeedbackStatus.fromCode(status), pageable);
        } else if (category != null && !category.isEmpty()) {
            feedbacks = feedbackService.findByCategory(category, pageable);
        } else {
            feedbacks = feedbackService.findAll(pageable);
        }

        return Result.success(feedbacks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取反馈详情", description = "根据ID获取反馈详情")
    public Result<UserFeedback> getFeedbackDetail(@PathVariable Long id) {
        return feedbackService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("反馈不存在"));
    }

    @PostMapping
    @Operation(summary = "创建反馈", description = "创建新反馈")
    public Result<UserFeedback> createFeedback(@RequestBody UserFeedback feedback) {
        return Result.success(feedbackService.createFeedback(feedback));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新反馈", description = "更新反馈信息")
    public Result<UserFeedback> updateFeedback(@PathVariable Long id, @RequestBody UserFeedback feedback) {
        feedback.setId(id);
        return Result.success(feedbackService.updateFeedback(feedback));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新反馈状态", description = "更新反馈状态")
    public Result<UserFeedback> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return Result.success(feedbackService.updateStatus(id, FeedbackStatus.fromCode(status)));
    }

    @PutMapping("/{id}/handle")
    @Operation(summary = "处理反馈", description = "处理用户反馈")
    public Result<UserFeedback> handleFeedback(
            @PathVariable Long id,
            @RequestParam Long handlerId,
            @RequestParam String handlerName,
            @RequestParam String status,
            @RequestParam(required = false) String handleNote) {
        return Result.success(feedbackService.handleFeedback(
                id, handlerId, handlerName, FeedbackStatus.fromCode(status), handleNote));
    }

    @GetMapping("/stats")
    @Operation(summary = "获取反馈统计", description = "获取反馈统计数据")
    public Result<Map<String, Object>> getFeedbackStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("submittedCount", feedbackService.countByStatus(FeedbackStatus.SUBMITTED));
        stats.put("reviewingCount", feedbackService.countByStatus(FeedbackStatus.REVIEWING));
        stats.put("acceptedCount", feedbackService.countByStatus(FeedbackStatus.ACCEPTED));
        stats.put("rejectedCount", feedbackService.countByStatus(FeedbackStatus.REJECTED));
        stats.put("implementedCount", feedbackService.countByStatus(FeedbackStatus.IMPLEMENTED));
        return Result.success(stats);
    }

    @GetMapping("/categories")
    @Operation(summary = "获取反馈分类列表", description = "获取所有反馈分类")
    public Result<List<FeedbackCategory>> getCategories() {
        return Result.success(feedbackService.getAllCategories());
    }

    @PostMapping("/categories")
    @Operation(summary = "创建反馈分类", description = "创建新的反馈分类")
    public Result<FeedbackCategory> createCategory(@RequestBody FeedbackCategory category) {
        return Result.success(feedbackService.createCategory(category));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "更新反馈分类", description = "更新反馈分类")
    public Result<FeedbackCategory> updateCategory(@PathVariable Long id, @RequestBody FeedbackCategory category) {
        category.setId(id);
        return Result.success(feedbackService.updateCategory(category));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "删除反馈分类", description = "删除反馈分类")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        feedbackService.deleteCategory(id);
        return Result.success();
    }
}
