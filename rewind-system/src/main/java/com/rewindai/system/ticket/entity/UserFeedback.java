package com.rewindai.system.ticket.entity;

import com.rewindai.system.common.entity.BaseEntity;
import com.rewindai.system.ticket.enums.FeedbackStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户反馈实体
 *
 * @author Rewind.ai Team
 */
@Getter
@Setter
@Entity
@Table(name = "user_feedback")
public class UserFeedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_nickname", length = 100)
    private String userNickname;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "contact_info", length = 200)
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private FeedbackStatus status;

    @Column(name = "handler_id")
    private Long handlerId;

    @Column(name = "handler_name", length = 100)
    private String handlerName;

    @Column(name = "handle_note", columnDefinition = "TEXT")
    private String handleNote;

    @Column(name = "handle_time")
    private java.time.LocalDateTime handleTime;
}
