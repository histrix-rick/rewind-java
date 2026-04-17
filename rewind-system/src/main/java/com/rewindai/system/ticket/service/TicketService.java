package com.rewindai.system.ticket.service;

import com.rewindai.system.ticket.entity.Ticket;
import com.rewindai.system.ticket.entity.TicketReply;
import com.rewindai.system.ticket.enums.TicketStatus;
import com.rewindai.system.ticket.repository.TicketRepository;
import com.rewindai.system.ticket.repository.TicketReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 工单 Service
 *
 * @author Rewind.ai Team
 */
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketReplyRepository ticketReplyRepository;

    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    public Page<Ticket> findAll(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    public Page<Ticket> findByStatus(TicketStatus status, Pageable pageable) {
        return ticketRepository.findByStatus(status, pageable);
    }

    public List<Ticket> findByUserId(UUID userId) {
        return ticketRepository.findByUserId(userId);
    }

    public Page<Ticket> searchTickets(String keyword, Pageable pageable) {
        return ticketRepository.searchTickets(keyword, pageable);
    }

    public Page<Ticket> findByAssignedAdmin(Long adminId, Pageable pageable) {
        return ticketRepository.findByAssignedAdminId(adminId, pageable);
    }

    @Transactional
    public Ticket create(Ticket ticket) {
        ticket.setStatus(TicketStatus.PENDING);
        ticket.setReplyCount(0);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket createTicket(Ticket ticket) {
        return create(ticket);
    }

    @Transactional
    public Ticket updateTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket assignTicket(Long ticketId, Long adminId, String adminName) {
        return ticketRepository.findById(ticketId).map(ticket -> {
            ticket.setAssignedAdminId(adminId);
            ticket.setAssignedAdminName(adminName);
            ticket.setStatus(TicketStatus.PROCESSING);
            return ticketRepository.save(ticket);
        }).orElseThrow(() -> new IllegalArgumentException("工单不存在"));
    }

    @Transactional
    public Ticket updateStatus(Long ticketId, TicketStatus status) {
        return ticketRepository.findById(ticketId).map(ticket -> {
            ticket.setStatus(status);
            return ticketRepository.save(ticket);
        }).orElseThrow(() -> new IllegalArgumentException("工单不存在"));
    }

    @Transactional
    public TicketReply addReply(Long ticketId, Long replyerId, String replyerName,
                                 Boolean isAdmin, String content) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("工单不存在"));

        TicketReply reply = new TicketReply();
        reply.setTicketId(ticketId);
        reply.setReplyerId(replyerId);
        reply.setReplyerName(replyerName);
        reply.setIsAdmin(isAdmin);
        reply.setContent(content);
        TicketReply savedReply = ticketReplyRepository.save(reply);

        ticket.setLastReplyTime(LocalDateTime.now());
        ticket.setReplyCount(ticket.getReplyCount() + 1);
        if (isAdmin && ticket.getStatus() == TicketStatus.PENDING) {
            ticket.setStatus(TicketStatus.PROCESSING);
        }
        ticketRepository.save(ticket);

        return savedReply;
    }

    public List<TicketReply> getReplies(Long ticketId) {
        return ticketReplyRepository.findByTicketIdOrderByCreatedTimeAsc(ticketId);
    }

    public long countByStatus(TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }

    public long countUnassigned() {
        return ticketRepository.countUnassigned();
    }
}
