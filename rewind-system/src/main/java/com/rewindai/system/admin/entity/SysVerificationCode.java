package com.rewindai.system.admin.entity;

import com.rewindai.system.sms.enums.SmsProvider;
import com.rewindai.system.sms.enums.SmsSendStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * 验证码记录表实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_verification_codes", indexes = {
        @Index(name = "idx_ver_target_scene", columnList = "receiver, type"),
        @Index(name = "idx_ver_target_type", columnList = "target_type"),
        @Index(name = "idx_ver_send_status", columnList = "send_status"),
        @Index(name = "idx_ver_provider", columnList = "provider_code"),
        @Index(name = "idx_ver_expire_at", columnList = "expire_at")
})
public class SysVerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receiver", nullable = false, length = 100)
    private String receiver;

    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "target_type", length = 20)
    @Builder.Default
    private String targetType = "PHONE";

    @Enumerated(EnumType.STRING)
    @Column(name = "send_status", length = 20)
    @Builder.Default
    private SmsSendStatus sendStatus = SmsSendStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_code", length = 50)
    private SmsProvider providerCode;

    @Column(name = "send_result", columnDefinition = "TEXT")
    private String sendResult;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "max_retry_at")
    private OffsetDateTime maxRetryAt;

    @Column(name = "is_used")
    private Boolean isUsed;

    @Column(name = "used_at")
    private OffsetDateTime usedAt;

    @Column(name = "expire_at", nullable = false)
    private OffsetDateTime expireAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.isUsed == null) {
            this.isUsed = false;
        }
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        if (this.sendStatus == null) {
            this.sendStatus = SmsSendStatus.PENDING;
        }
        if (this.targetType == null) {
            this.targetType = "PHONE";
        }
    }
}
