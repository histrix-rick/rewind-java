package com.rewindai.admin.dto;

import com.rewindai.system.admin.entity.SysPermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysPermissionResponse {

    private Long id;
    private String permissionName;
    private String permissionCode;
    private String permissionModule;
    private String permissionType;
    private Long parentId;
    private String description;
    private Integer sortOrder;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<SysPermissionResponse> children;

    public static SysPermissionResponse from(SysPermission permission) {
        return SysPermissionResponse.builder()
                .id(permission.getId())
                .permissionName(permission.getPermissionName())
                .permissionCode(permission.getPermissionCode())
                .permissionModule(permission.getPermissionModule() != null ? permission.getPermissionModule().getCode() : null)
                .permissionType(permission.getPermissionType() != null ? permission.getPermissionType().getCode() : null)
                .parentId(permission.getParentId())
                .description(permission.getDescription())
                .sortOrder(permission.getSortOrder())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .children(new ArrayList<>())
                .build();
    }

    public static List<SysPermissionResponse> buildTree(List<SysPermission> permissions) {
        List<SysPermissionResponse> rootNodes = new ArrayList<>();
        List<SysPermissionResponse> allNodes = new ArrayList<>();

        for (SysPermission permission : permissions) {
            allNodes.add(from(permission));
        }

        for (SysPermissionResponse node : allNodes) {
            if (node.getParentId() == null) {
                rootNodes.add(node);
            } else {
                for (SysPermissionResponse parent : allNodes) {
                    if (parent.getId().equals(node.getParentId())) {
                        parent.getChildren().add(node);
                        break;
                    }
                }
            }
        }

        return rootNodes;
    }
}
