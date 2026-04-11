package com.rewindai.system.admin.entity;

import com.rewindai.system.admin.enums.PermissionModule;
import com.rewindai.system.admin.enums.PermissionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * 权限实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_permissions", indexes = {
        @Index(name = "idx_sys_permissions_module", columnList = "permission_module"),
        @Index(name = "idx_sys_permissions_parent", columnList = "parent_id")
})
public class SysPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;

    @Column(name = "permission_code", unique = true, nullable = false, length = 100)
    private String permissionCode;

    @Column(name = "permission_module", nullable = false, length = 50)
    @Convert(converter = PermissionModuleConverter.class)
    private PermissionModule permissionModule;

    @Column(name = "permission_type", nullable = false, length = 20)
    @Convert(converter = PermissionTypeConverter.class)
    private PermissionType permissionType;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.sortOrder == null) {
            this.sortOrder = 0;
        }
    }

    @Converter
    public static class PermissionModuleConverter implements AttributeConverter<PermissionModule, String> {
        @Override
        public String convertToDatabaseColumn(PermissionModule module) {
            return module != null ? module.getCode() : PermissionModule.USER.getCode();
        }

        @Override
        public PermissionModule convertToEntityAttribute(String code) {
            return code != null ? PermissionModule.fromCode(code) : PermissionModule.USER;
        }
    }

    @Converter
    public static class PermissionTypeConverter implements AttributeConverter<PermissionType, String> {
        @Override
        public String convertToDatabaseColumn(PermissionType type) {
            return type != null ? type.getCode() : PermissionType.BUTTON.getCode();
        }

        @Override
        public PermissionType convertToEntityAttribute(String code) {
            return code != null ? PermissionType.fromCode(code) : PermissionType.BUTTON;
        }
    }
}
