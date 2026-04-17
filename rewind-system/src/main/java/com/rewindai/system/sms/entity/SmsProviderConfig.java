package com.rewindai.system.sms.entity;

import com.rewindai.system.sms.enums.SmsProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * 短信运营商配置实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sms_provider_configs", indexes = {
    @Index(name = "idx_sms_provider_code", columnList = "provider_code", unique = true),
    @Index(name = "idx_sms_provider_active", columnList = "is_active"),
    @Index(name = "idx_sms_provider_default", columnList = "is_default")
})
public class SmsProviderConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_code", nullable = false, unique = true, length = 50)
    private SmsProvider providerCode;

    @Column(name = "provider_name", nullable = false, length = 100)
    private String providerName;

    @Column(name = "access_key_id", length = 200)
    private String accessKeyId;

    @Column(name = "access_key_secret", length = 200)
    private String accessKeySecret;

    @Column(name = "sign_name", length = 100)
    private String signName;

    @Column(name = "template_code_login", length = 100)
    private String templateCodeLogin;

    @Column(name = "template_code_register", length = 100)
    private String templateCodeRegister;

    @Column(name = "template_code_verify", length = 100)
    private String templateCodeVerify;

    @Column(name = "endpoint", length = 200)
    private String endpoint;

    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    @Column(name = "created_by_admin_id")
    private Integer createdByAdminId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
