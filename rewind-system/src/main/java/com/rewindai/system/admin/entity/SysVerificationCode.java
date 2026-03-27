package com.rewindai.system.admin.entity;

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
        @Index(name = "idx_ver_target_scene", columnList = "target, scene")
})
public class SysVerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target", nullable = false, length = 100)
    private String target;

    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Column(name = "scene", nullable = false, length = 20)
    private String scene;

    @Column(name = "is_used")
    private Boolean isUsed;

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
    }
}
