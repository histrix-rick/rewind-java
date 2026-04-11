package com.rewindai.system.ticket.entity;

import com.rewindai.system.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 工单回复实体
 *
 * @author Rewind.ai Team
 */
@Getter
@Setter
@Entity
@Table(name = "ticket_reply")
public class TicketReply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "replyer_id", nullable = false)
    private Long replyerId;

    @Column(name = "replyer_name", length = 100)
    private String replyerName;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
