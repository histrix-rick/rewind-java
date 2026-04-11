package com.rewindai.system.admin.entity;

import com.rewindai.system.admin.enums.RoleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * 角色实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_roles", indexes = {
        @Index(name = "idx_sys_roles_status", columnList = "status")
})
public class SysRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private String roleName;

    @Column(name = "role_code", unique = true, nullable = false, length = 50)
    private String roleCode;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "status")
    @Convert(converter = RoleStatusConverter.class)
    private RoleStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = RoleStatus.ACTIVE;
        }
        if (this.sortOrder == null) {
            this.sortOrder = 0;
        }
    }

    @Converter
    public static class RoleStatusConverter implements AttributeConverter<RoleStatus, String> {
        @Override
        public String convertToDatabaseColumn(RoleStatus status) {
            return status != null ? status.getCode() : RoleStatus.ACTIVE.getCode();
        }

        @Override
        public RoleStatus convertToEntityAttribute(String code) {
            return code != null ? RoleStatus.fromCode(code) : RoleStatus.ACTIVE;
        }
    }
}
