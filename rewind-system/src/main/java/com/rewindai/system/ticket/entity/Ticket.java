package com.rewindai.system.ticket.entity;

import com.rewindai.system.common.entity.BaseEntity;
import com.rewindai.system.ticket.enums.TicketCategory;
import com.rewindai.system.ticket.enums.TicketPriority;
import com.rewindai.system.ticket.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 工单实体
 *
 * @author Rewind.ai Team
 */
@Getter
@Setter
@Entity
@Table(name = "ticket")
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "user_nickname", length = 100)
    private String userNickname;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 50)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private TicketStatus status;

    @Column(name = "assigned_admin_id")
    private Long assignedAdminId;

    @Column(name = "assigned_admin_name", length = 100)
    private String assignedAdminName;

    @Column(name = "last_reply_time")
    private java.time.LocalDateTime lastReplyTime;

    @Column(name = "reply_count", nullable = false)
    private Integer replyCount = 0;
}
