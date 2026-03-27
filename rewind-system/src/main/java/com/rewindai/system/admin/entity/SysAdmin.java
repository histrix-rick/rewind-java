package com.rewindai.system.admin.entity;

import com.rewindai.system.admin.enums.AdminStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * 管理员主体表实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_admins", indexes = {
        @Index(name = "idx_sys_admins_username", columnList = "username"),
        @Index(name = "idx_sys_admins_phone", columnList = "phoneNumber"),
        @Index(name = "idx_sys_admins_email", columnList = "email"),
        @Index(name = "idx_sys_admins_status", columnList = "status")
})
public class SysAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;

    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "status")
    @Convert(converter = AdminStatusConverter.class)
    private AdminStatus status;

    @Column(name = "is_default_password")
    private Boolean isDefaultPassword;

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;

    @Column(name = "last_pwd_change_at")
    private OffsetDateTime lastPwdChangeAt;

    @Column(name = "created_by_admin_id")
    private Integer createdByAdminId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = AdminStatus.NORMAL;
        }
        if (this.isDefaultPassword == null) {
            this.isDefaultPassword = true;
        }
    }

    @Converter
    public static class AdminStatusConverter implements AttributeConverter<AdminStatus, Integer> {
        @Override
        public Integer convertToDatabaseColumn(AdminStatus status) {
            return status != null ? status.getCode() : AdminStatus.NORMAL.getCode();
        }

        @Override
        public AdminStatus convertToEntityAttribute(Integer code) {
            return code != null ? AdminStatus.fromCode(code) : AdminStatus.NORMAL;
        }
    }
}
