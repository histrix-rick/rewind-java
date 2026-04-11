package com.rewindai.admin.controller;

import com.rewindai.common.core.result.Result;
import com.rewindai.system.ticket.entity.Ticket;
import com.rewindai.system.ticket.entity.TicketReply;
import com.rewindai.system.ticket.enums.TicketStatus;
import com.rewindai.system.ticket.service.TicketService;
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
 * 工单管理控制器
 *
 * @author Rewind.ai Team
 */
@RestController
@RequestMapping("/admin/ticket")
@RequiredArgsConstructor
@Tag(name = "工单管理", description = "工单管理接口")
public class AdminTicketController {

    private final TicketService ticketService;

    @GetMapping("/list")
    @Operation(summary = "获取工单列表", description = "分页获取工单列表")
    public Result<Page<Ticket>> getTicketList(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "工单状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createdTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Ticket> tickets;

        if (keyword != null && !keyword.isEmpty()) {
            tickets = ticketService.searchTickets(keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            tickets = ticketService.findByStatus(TicketStatus.fromCode(status), pageable);
        } else {
            tickets = ticketService.findAll(pageable);
        }

        return Result.success(tickets);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取工单详情", description = "根据ID获取工单详情")
    public Result<Ticket> getTicketDetail(@PathVariable Long id) {
        return ticketService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("工单不存在"));
    }

    @GetMapping("/{id}/replies")
    @Operation(summary = "获取工单回复列表", description = "获取工单的所有回复")
    public Result<List<TicketReply>> getTicketReplies(@PathVariable Long id) {
        return Result.success(ticketService.getReplies(id));
    }

    @PostMapping
    @Operation(summary = "创建工单", description = "创建新工单")
    public Result<Ticket> createTicket(@RequestBody Ticket ticket) {
        return Result.success(ticketService.createTicket(ticket));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新工单", description = "更新工单信息")
    public Result<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
        ticket.setId(id);
        return Result.success(ticketService.updateTicket(ticket));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新工单状态", description = "更新工单状态")
    public Result<Ticket> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return Result.success(ticketService.updateStatus(id, TicketStatus.fromCode(status)));
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "分配工单", description = "将工单分配给管理员")
    public Result<Ticket> assignTicket(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestParam String adminName) {
        return Result.success(ticketService.assignTicket(id, adminId, adminName));
    }

    @PostMapping("/{id}/reply")
    @Operation(summary = "添加工单回复", description = "为工单添加回复")
    public Result<TicketReply> addReply(
            @PathVariable Long id,
            @RequestParam Long replyerId,
            @RequestParam String replyerName,
            @RequestParam(defaultValue = "true") Boolean isAdmin,
            @RequestBody String content) {
        return Result.success(ticketService.addReply(id, replyerId, replyerName, isAdmin, content));
    }

    @GetMapping("/stats")
    @Operation(summary = "获取工单统计", description = "获取工单统计数据")
    public Result<Map<String, Object>> getTicketStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingCount", ticketService.countByStatus(TicketStatus.PENDING));
        stats.put("processingCount", ticketService.countByStatus(TicketStatus.PROCESSING));
        stats.put("resolvedCount", ticketService.countByStatus(TicketStatus.RESOLVED));
        stats.put("closedCount", ticketService.countByStatus(TicketStatus.CLOSED));
        stats.put("unassignedCount", ticketService.countUnassigned());
        return Result.success(stats);
    }
}
