package com.rewindai.system.ticket.repository;

import com.rewindai.system.ticket.entity.TicketReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 工单回复 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface TicketReplyRepository extends JpaRepository<TicketReply, Long> {

    List<TicketReply> findByTicketIdOrderByCreatedTimeAsc(Long ticketId);

    Page<TicketReply> findByTicketId(Long ticketId, Pageable pageable);

    long countByTicketId(Long ticketId);
}
