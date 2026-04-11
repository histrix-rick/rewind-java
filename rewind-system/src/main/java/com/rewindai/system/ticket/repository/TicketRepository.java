package com.rewindai.system.ticket.repository;

import com.rewindai.system.ticket.entity.Ticket;
import com.rewindai.system.ticket.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 工单 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

    Page<Ticket> findByUserId(Long userId, Pageable pageable);

    Page<Ticket> findByAssignedAdminId(Long adminId, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "t.title LIKE %:keyword% OR " +
           "t.content LIKE %:keyword% OR " +
           "t.userNickname LIKE %:keyword%)")
    Page<Ticket> searchTickets(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    long countByStatus(@Param("status") TicketStatus status);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedAdminId IS NULL")
    long countUnassigned();
}
