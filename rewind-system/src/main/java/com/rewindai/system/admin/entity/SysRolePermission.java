package com.rewindai.system.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * 角色-权限关联实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_role_permissions", indexes = {
        @Index(name = "idx_sys_role_permissions_role", columnList = "role_id"),
        @Index(name = "idx_sys_role_permissions_permission", columnList = "permission_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_sys_role_permissions_role_permission", columnNames = {"role_id", "permission_id"})
})
public class SysRolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
