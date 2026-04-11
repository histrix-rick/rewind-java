package com.rewindai.admin.dto;

import com.rewindai.system.admin.entity.SysAdmin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 管理员响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {

    private Integer id;
    private String username;
    private String nickname;
    private String avatar;
    private String phoneNumber;
    private String email;
    private String status;
    private Boolean isDefaultPassword;
    private OffsetDateTime lastLoginAt;
    private OffsetDateTime lastPwdChangeAt;
    private Integer createdByAdminId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<Long> roleIds;

    public static AdminResponse from(SysAdmin admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .nickname(admin.getNickname())
                .avatar(admin.getAvatar())
                .phoneNumber(admin.getPhoneNumber())
                .email(admin.getEmail())
                .status(admin.getStatus() != null ? admin.getStatus().getCode().toString() : null)
                .isDefaultPassword(admin.getIsDefaultPassword())
                .lastLoginAt(admin.getLastLoginAt())
                .lastPwdChangeAt(admin.getLastPwdChangeAt())
                .createdByAdminId(admin.getCreatedByAdminId())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();
    }

    public static AdminResponse fromWithRoles(SysAdmin admin, List<Long> roleIds) {
        AdminResponse response = from(admin);
        response.setRoleIds(roleIds);
        return response;
    }
}
