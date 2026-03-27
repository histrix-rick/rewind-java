package com.rewindai.app.controller;

import com.rewindai.app.dto.CreateBranchRequest;
import com.rewindai.app.dto.TimelineNodeRequest;
import com.rewindai.app.dto.TimelineNodeResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.aijudge.service.AiJudgeService;
import com.rewindai.system.daydream.entity.DreamBranch;
import com.rewindai.system.daydream.entity.TimelineNode;
import com.rewindai.system.daydream.service.DaydreamService;
import com.rewindai.system.daydream.service.TimelineService;
import com.rewindai.system.user.entity.UserAttribute;
import com.rewindai.system.user.service.AttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 时间轴 Controller
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Tag(name = "时间轴管理", description = "时间轴相关接口")
@RestController
@RequestMapping("/api/daydreams/{daydreamId}/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;
    private final DaydreamService daydreamService;
    private final AiJudgeService aiJudgeService;
    private final AttributeService attributeService;

    @Operation(summary = "获取时间轴")
    @GetMapping
    public Result<List<TimelineNodeResponse>> getTimeline(
            @PathVariable UUID daydreamId,
            @RequestParam(required = false) UUID branchId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findByIdAndUserId(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在"));

        List<TimelineNode> nodes = timelineService.getTimeline(daydreamId, branchId);
        return Result.success(nodes.stream().map(TimelineNodeResponse::from).toList());
    }

    @Operation(summary = "获取时间轴（分页）")
    @GetMapping("/page")
    public Result<Page<TimelineNodeResponse>> getTimelinePage(
            @PathVariable UUID daydreamId,
            @RequestParam(required = false) UUID branchId,
            @PageableDefault(size = 20, sort = "sequenceNum", direction = Sort.Direction.ASC) Pageable pageable,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findByIdAndUserId(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在"));

        Page<TimelineNode> nodes = timelineService.getTimelinePage(daydreamId, branchId, pageable);
        return Result.success(nodes.map(TimelineNodeResponse::from));
    }

    @Operation(summary = "获取推理进度（SSE流式）")
    @GetMapping(value = "/stream/reasoning", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> streamReasoning() {
        List<String> steps = List.of(
                "正在检索历史档案...",
                "正在分析历史背景...",
                "正在验证逻辑合理性...",
                "正在进行概率计算...",
                "正在生成判定结果..."
        );

        return Flux.interval(Duration.ofMillis(400))
                .take(steps.size())
                .map(i -> Map.of(
                        "step", i.intValue() + 1,
                        "total", steps.size(),
                        "message", steps.get(i.intValue()),
                        "done", i.intValue() == steps.size() - 1
                ));
    }

    @Operation(summary = "添加时间轴节点（触发AI判定，支持5秒超时）")
    @PostMapping
    public Result<TimelineNodeResponse> addNode(
            @PathVariable UUID daydreamId,
            @Valid @RequestBody TimelineNodeRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        var daydream = daydreamService.findByIdAndUserId(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在"));

        // 1. 先创建一个PROCESSING状态的节点，立即返回ID
        TimelineNode pendingNode = timelineService.addPendingNode(
                userId,
                daydreamId,
                request.getBranchId(),
                request.getUserDecision(),
                request.getNodeDate() != null ? request.getNodeDate() : daydream.getCurrentDate()
        );

        // 2. 异步执行AI判定
        CompletableFuture<AiJudgeService.AiJudgmentResultEnhanced> judgmentFuture = CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始异步AI判定: nodeId={}", pendingNode.getId());
                return aiJudgeService.judgeEnhanced(
                        request.getUserDecision(),
                        request.getNodeDate() != null ? request.getNodeDate() : daydream.getCurrentDate(),
                        daydreamId,
                        userId
                );
            } catch (Exception e) {
                log.error("AI判定异常", e);
                throw new RuntimeException("AI判定失败", e);
            }
        });

        // 3. 等待最多5秒
        AiJudgeService.AiJudgmentResultEnhanced judgment = null;
        boolean completedInTime = false;

        try {
            judgment = judgmentFuture.get(5, TimeUnit.SECONDS);
            completedInTime = true;
            log.info("AI判定在5秒内完成: nodeId={}", pendingNode.getId());
        } catch (Exception e) {
            log.warn("AI判定超时（5秒），将在后台继续处理: nodeId={}", pendingNode.getId());
            // 超时了，但后台继续处理，稍后更新
            judgmentFuture.thenAcceptAsync(asyncJudgment -> {
                try {
                    log.info("后台AI判定完成，更新节点: nodeId={}", pendingNode.getId());
                    completeNodeJudgment(pendingNode.getId(), userId, asyncJudgment, request.getDecisionSummary());
                } catch (Exception ex) {
                    log.error("更新节点失败", ex);
                }
            });
        }

        // 4. 如果5秒内完成，完整处理；否则，返回PROCESSING状态的节点
        if (completedInTime && judgment != null) {
            // 5秒内完成，更新节点为SUCCESS状态
            return completeNodeJudgment(pendingNode.getId(), userId, judgment, request.getDecisionSummary());
        } else {
            // 超时，返回PROCESSING状态的节点
            TimelineNodeResponse response = TimelineNodeResponse.from(pendingNode);
            Map<String, Object> extra = Map.of(
                    "isProcessing", true,
                    "message", "AI正在判定中，请稍后查看结果"
            );
            return Result.successWithExtra(response, extra);
        }
    }

    /**
     * 完成节点判定（更新节点状态、属性等）
     */
    private Result<TimelineNodeResponse> completeNodeJudgment(
            UUID nodeId,
            UUID userId,
            AiJudgeService.AiJudgmentResultEnhanced judgment,
            String decisionSummary) {

        String summary = decisionSummary;
        if (judgment.summary != null && !judgment.summary.isEmpty()) {
            summary = judgment.summary;
        } else if (summary == null || summary.isEmpty()) {
            summary = judgment.feedback.length() > 100
                    ? judgment.feedback.substring(0, 100) + "..."
                    : judgment.feedback;
        }

        // 优先使用AI真实推理过程，如果没有则使用模板推理
        String reasoningTraceStr;
        if (judgment.aiReasoningTrace != null && !judgment.aiReasoningTrace.isEmpty()) {
            reasoningTraceStr = judgment.aiReasoningTrace;
        } else if (judgment.reasoningTrace != null) {
            reasoningTraceStr = String.join("\n", judgment.reasoningTrace);
        } else {
            reasoningTraceStr = null;
        }

        TimelineNode node = timelineService.completeNode(
                nodeId,
                summary,
                judgment.feedback,
                reasoningTraceStr,
                judgment.approved
        );

        // 更新属性
        if (judgment.attributeChanges != null && !judgment.attributeChanges.isEmpty()) {
            attributeService.updateAttribute(
                    userId,
                    judgment.attributeChanges.get("financialPower"),
                    judgment.attributeChanges.get("intelligence"),
                    judgment.attributeChanges.get("physicalPower"),
                    judgment.attributeChanges.get("charisma"),
                    judgment.attributeChanges.get("luck")
            );
        }

        if (judgment.approved && node.getNodeDate() != null) {
            daydreamService.updateCurrentDate(node.getDreamId(), userId, node.getNodeDate());
        }

        TimelineNodeResponse response = TimelineNodeResponse.from(node);

        // 在响应中添加增强信息
        Map<String, Object> extra = new java.util.HashMap<>();
        extra.put("attributeChanges", judgment.attributeChanges != null ? judgment.attributeChanges : Map.of());
        extra.put("aiReasoningTrace", judgment.aiReasoningTrace);
        extra.put("financialImpact", judgment.financialImpact);
        extra.put("suggestedAssets", judgment.suggestedAssets);

        // 检查是否需要完善资产信息
        boolean needsAssetInfo = judgment.suggestedAssets != null &&
                judgment.suggestedAssets.stream().anyMatch(a -> a.needsLocation);
        extra.put("needsAssetInfo", needsAssetInfo);

        return Result.successWithExtra(response, extra);
    }

    /**
     * 查询节点判定状态
     */
    @Operation(summary = "查询节点判定状态")
    @GetMapping("/node/{nodeId}/status")
    public Result<Map<String, Object>> getNodeJudgmentStatus(
            @PathVariable UUID daydreamId,
            @PathVariable UUID nodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findByIdAndUserId(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在"));

        TimelineNode node = timelineService.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("节点不存在"));

        if (!node.getDreamId().equals(daydreamId)) {
            throw new RuntimeException("节点不属于该白日梦");
        }

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("status", node.getJudgmentStatus());
        result.put("node", TimelineNodeResponse.from(node));

        return Result.success(result);
    }

    @Operation(summary = "撤销上一个决策")
    @DeleteMapping("/undo")
    public Result<Void> undoLastNode(
            @PathVariable UUID daydreamId,
            @RequestParam(required = false) UUID branchId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        timelineService.undoLastNode(userId, daydreamId, branchId);
        return Result.success();
    }

    @Operation(summary = "重新判定某个节点")
    @PostMapping("/{nodeId}/rejudge")
    public Result<TimelineNodeResponse> rejudgeNode(
            @PathVariable UUID daydreamId,
            @PathVariable UUID nodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        daydreamService.findByIdAndUserId(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在"));

        TimelineNode node = timelineService.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("节点不存在"));

        if (!node.getDreamId().equals(daydreamId)) {
            throw new RuntimeException("节点不属于该白日梦");
        }

        // 重新进行AI判定（增强版）
        AiJudgeService.AiJudgmentResultEnhanced judgment = aiJudgeService.judgeEnhanced(
                node.getUserDecision(),
                node.getNodeDate(),
                daydreamId,
                userId
        );

        // 更新节点
        node.setAiFeedback(judgment.feedback);
        node.setIsApproved(judgment.approved);
        node.setDecisionSummary(judgment.summary != null ? judgment.summary : node.getDecisionSummary());

        // 优先使用AI真实推理过程
        if (judgment.aiReasoningTrace != null && !judgment.aiReasoningTrace.isEmpty()) {
            node.setReasoningTrace(judgment.aiReasoningTrace);
        } else if (judgment.reasoningTrace != null) {
            node.setReasoningTrace(String.join("\n", judgment.reasoningTrace));
        }

        // 重新获取属性快照
        UserAttribute attribute = attributeService.getOrCreateAttribute(userId);
        Map<String, Object> attributeSnapshot = attributeService.getAttributeSnapshot(attribute);
        node.setAttributeSnapshot(attributeSnapshot);

        // 保存更新后的节点
        TimelineNode savedNode = timelineService.save(node);

        if (judgment.attributeChanges != null && !judgment.attributeChanges.isEmpty()) {
            attributeService.updateAttribute(
                    userId,
                    judgment.attributeChanges.get("financialPower"),
                    judgment.attributeChanges.get("intelligence"),
                    judgment.attributeChanges.get("physicalPower"),
                    judgment.attributeChanges.get("charisma"),
                    judgment.attributeChanges.get("luck")
            );
        }

        TimelineNodeResponse response = TimelineNodeResponse.from(savedNode);
        Map<String, Object> extra = new java.util.HashMap<>();
        extra.put("attributeChanges", judgment.attributeChanges != null ? judgment.attributeChanges : Map.of());
        extra.put("aiReasoningTrace", judgment.aiReasoningTrace);
        extra.put("financialImpact", judgment.financialImpact);
        extra.put("suggestedAssets", judgment.suggestedAssets);
        return Result.successWithExtra(response, extra);
    }

    @Operation(summary = "从指定节点创建新分支（时间轴回溯）")
    @PostMapping("/{nodeId}/branch")
    public Result<DreamBranch> createBranch(
            @PathVariable UUID daydreamId,
            @PathVariable UUID nodeId,
            @Valid @RequestBody CreateBranchRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        DreamBranch branch = timelineService.createBranchFromNode(userId, daydreamId, nodeId, request.getBranchName());
        return Result.success(branch);
    }

    @Operation(summary = "获取节点详情")
    @GetMapping("/{nodeId}")
    public Result<TimelineNodeResponse> getNode(
            @PathVariable UUID daydreamId,
            @PathVariable UUID nodeId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        daydreamService.findByIdAndUserId(daydreamId, userId)
                .orElseThrow(() -> new RuntimeException("白日梦不存在"));

        return timelineService.findById(nodeId)
                .map(TimelineNodeResponse::from)
                .map(Result::success)
                .orElse(Result.notFound("节点不存在"));
    }
}
