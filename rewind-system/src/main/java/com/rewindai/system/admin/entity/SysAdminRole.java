package com.rewindai.system.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * 管理员-角色关联实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_admin_roles", indexes = {
        @Index(name = "idx_sys_admin_roles_admin", columnList = "admin_id"),
        @Index(name = "idx_sys_admin_roles_role", columnList = "role_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_sys_admin_roles_admin_role", columnNames = {"admin_id", "role_id"})
})
public class SysAdminRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", nullable = false)
    private Integer adminId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
