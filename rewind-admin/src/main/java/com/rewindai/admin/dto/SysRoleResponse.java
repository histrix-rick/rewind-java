package com.rewindai.admin.dto;

import com.rewindai.system.admin.entity.SysRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 角色响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysRoleResponse {

    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer sortOrder;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<Long> permissionIds;

    public static SysRoleResponse from(SysRole role) {
        return SysRoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .roleCode(role.getRoleCode())
                .description(role.getDescription())
                .sortOrder(role.getSortOrder())
                .status(role.getStatus() != null ? role.getStatus().getCode() : null)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    public static SysRoleResponse fromWithPermissions(SysRole role, List<Long> permissionIds) {
        SysRoleResponse response = from(role);
        response.setPermissionIds(permissionIds);
        return response;
    }
}
