package com.rewindai.app.controller;

import com.rewindai.common.core.result.Result;
import com.rewindai.system.customer.agent.CustomerServiceAgent;
import com.rewindai.system.ticket.entity.FeedbackCategory;
import com.rewindai.system.ticket.entity.KnowledgeBase;
import com.rewindai.system.ticket.entity.Ticket;
import com.rewindai.system.ticket.entity.TicketReply;
import com.rewindai.system.ticket.entity.UserFeedback;
import com.rewindai.system.ticket.enums.TicketCategory;
import com.rewindai.system.ticket.enums.TicketPriority;
import com.rewindai.system.ticket.service.FeedbackCategoryService;
import com.rewindai.system.ticket.service.KnowledgeBaseService;
import com.rewindai.system.ticket.service.TicketService;
import com.rewindai.system.ticket.service.UserFeedbackService;
import com.rewindai.system.user.service.UserService;
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
 * 前端API - 客服中心控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/api/customer-service")
@RequiredArgsConstructor
@Tag(name = "前端API-客服中心", description = "客服中心相关接口")
public class AppCustomerServiceController {

    private final TicketService ticketService;
    private final UserFeedbackService userFeedbackService;
    private final KnowledgeBaseService knowledgeBaseService;
    private final FeedbackCategoryService feedbackCategoryService;
    private final UserService userService;
    private final CustomerServiceAgent customerServiceAgent;

    // ==================== 工单相关 ====================

    @PostMapping("/tickets")
    @Operation(summary = "提交工单", description = "用户提交工单")
    public Result<Ticket> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            Authentication authentication) {
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);
        log.info("用户提交工单: userId={}, title={}", userId, request.getTitle());

        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setTitle(request.getTitle());
        ticket.setContent(request.getDescription());

        userService.findById(userId).ifPresent(user -> {
            ticket.setUserNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });

        if (request.getCategory() != null) {
            try {
                ticket.setCategory(TicketCategory.valueOf(request.getCategory()));
            } catch (IllegalArgumentException e) {
                log.warn("无效的工单分类: {}, 使用默认值 TECHNICAL", request.getCategory());
                ticket.setCategory(TicketCategory.TECHNICAL);
            }
        } else {
            ticket.setCategory(TicketCategory.TECHNICAL);
        }

        if (request.getPriority() != null) {
            try {
                ticket.setPriority(TicketPriority.valueOf(request.getPriority()));
            } catch (IllegalArgumentException e) {
                log.warn("无效的工单优先级: {}, 使用默认值 MEDIUM", request.getPriority());
                ticket.setPriority(TicketPriority.MEDIUM);
            }
        } else {
            ticket.setPriority(TicketPriority.MEDIUM);
        }

        Ticket saved = ticketService.create(ticket);
        return Result.success(saved);
    }

    @GetMapping("/tickets")
    @Operation(summary = "获取我的工单列表", description = "获取当前用户的工单列表")
    public Result<List<Ticket>> getMyTickets(Authentication authentication) {
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);
        List<Ticket> tickets = ticketService.findByUserId(userId);
        return Result.success(tickets);
    }

    @GetMapping("/tickets/{ticketId}")
    @Operation(summary = "获取工单详情", description = "获取指定工单的详情")
    public Result<Ticket> getTicketDetail(
            @PathVariable Long ticketId,
            Authentication authentication) {
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);
        return ticketService.findById(ticketId)
                .filter(ticket -> ticket.getUserId().equals(userId))
                .map(Result::success)
                .orElse(Result.error(404, "工单不存在"));
    }

    @GetMapping("/tickets/{ticketId}/replies")
    @Operation(summary = "获取工单回复列表", description = "获取指定工单的所有回复")
    public Result<List<TicketReply>> getTicketReplies(
            @PathVariable Long ticketId,
            Authentication authentication) {
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);
        return ticketService.findById(ticketId)
                .filter(ticket -> ticket.getUserId().equals(userId))
                .map(ticket -> Result.success(ticketService.getReplies(ticketId)))
                .orElse(Result.error(404, "工单不存在"));
    }

    // ==================== 意见反馈相关 ====================

    @PostMapping("/feedback")
    @Operation(summary = "提交意见反馈", description = "用户提交意见反馈")
    public Result<UserFeedback> createFeedback(
            @Valid @RequestBody CreateFeedbackRequest request,
            Authentication authentication) {
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);
        log.info("用户提交意见反馈: userId={}", userId);

        UserFeedback feedback = new UserFeedback();
        feedback.setUserId(userId);
        feedback.setCategoryId(request.getCategoryId());
        feedback.setContent(request.getContent());
        feedback.setContact(request.getContact());

        UserFeedback saved = userFeedbackService.create(feedback);
        return Result.success(saved);
    }

    @GetMapping("/feedback/categories")
    @Operation(summary = "获取反馈分类列表", description = "获取所有反馈分类")
    public Result<List<FeedbackCategory>> getFeedbackCategories() {
        List<FeedbackCategory> categories = feedbackCategoryService.findAllActive();
        return Result.success(categories);
    }

    @GetMapping("/feedback")
    @Operation(summary = "获取我的反馈列表", description = "获取当前用户的反馈列表")
    public Result<List<UserFeedback>> getMyFeedback(Authentication authentication) {
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);
        List<UserFeedback> feedbacks = userFeedbackService.findByUserId(userId);
        return Result.success(feedbacks);
    }

    // ==================== AI 智能客服相关 ====================

    @PostMapping("/agent/chat")
    @Operation(summary = "发送消息给智能客服", description = "与AI智能客服对话")
    public Result<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("AI智能客服收到消息: {}", request.getMessage());
        String answer = customerServiceAgent.chat(request.getMessage());
        return Result.success(new ChatResponse(answer));
    }

    // ==================== 知识库相关 ====================

    @GetMapping("/knowledge")
    @Operation(summary = "获取知识库列表", description = "获取公开的知识库文章")
    public Result<List<KnowledgeBase>> getKnowledgeBase(
            @RequestParam(required = false) Long categoryId) {
        List<KnowledgeBase> articles;
        if (categoryId != null) {
            articles = knowledgeBaseService.findByCategoryId(categoryId);
        } else {
            articles = knowledgeBaseService.findAllActive();
        }
        return Result.success(articles);
    }

    @GetMapping("/knowledge/{id}")
    @Operation(summary = "获取知识库详情", description = "获取指定知识库文章的详情")
    public Result<KnowledgeBase> getKnowledgeDetail(@PathVariable Long id) {
        return knowledgeBaseService.findById(id)
                .map(Result::success)
                .orElse(Result.error(404, "文章不存在"));
    }

    // ==================== 请求DTO ====================

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CreateTicketRequest {
        private String title;
        private String description;
        private String category;
        private String priority;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CreateFeedbackRequest {
        private Long categoryId;
        private String content;
        private String contact;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ChatRequest {
        @jakarta.validation.constraints.NotBlank(message = "消息不能为空")
        private String message;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ChatResponse {
        private String answer;
    }
}
