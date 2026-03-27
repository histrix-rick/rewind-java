package com.rewindai.system.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 用户登录日志表实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_login_logs")
public class UserLoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private UUID userId;

    @CreationTimestamp
    @Column(name = "login_time", nullable = false, updatable = false)
    private OffsetDateTime loginTime;

    @Column(name = "login_ip", length = 45)
    private String loginIp;

    @Column(name = "login_type", length = 20)
    private String loginType;

    @Column(name = "device_model", length = 100)
    private String deviceModel;

    @Column(name = "os_version", length = 50)
    private String osVersion;

    @Column(name = "app_version", length = 20)
    private String appVersion;

    @Column(name = "status")
    private Boolean status;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = true;
        }
    }
}
