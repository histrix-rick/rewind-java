package com.rewindai.admin.dto;

import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.enums.Gender;
import com.rewindai.system.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 后台管理 - 用户详情响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台用户详情响应")
public class AdminUserResponse {

    @Schema(description = "用户ID")
    private UUID id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "性别")
    private Gender gender;

    @Schema(description = "生日")
    private LocalDate birthDate;

    @Schema(description = "手机号")
    private String phoneNumber;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "用户状态")
    private UserStatus status;

    @Schema(description = "注册IP")
    private String registerIp;

    @Schema(description = "注册设备ID")
    private String registerDeviceId;

    @Schema(description = "最后登录时间")
    private OffsetDateTime lastLoginTime;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "最后登录设备")
    private String lastLoginDevice;

    @Schema(description = "创建时间")
    private OffsetDateTime createdAt;

    @Schema(description = "更新时间")
    private OffsetDateTime updatedAt;

    // 统计数据
    @Schema(description = "发布梦境数")
    private Long dreamCount;

    @Schema(description = "粉丝数")
    private Long followerCount;

    @Schema(description = "关注数")
    private Long followingCount;

    /**
     * 从实体转换（基础信息）
     */
    public static AdminUserResponse fromEntity(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .realName(user.getRealName())
                .status(user.getStatus())
                .registerIp(user.getRegisterIp())
                .registerDeviceId(user.getRegisterDeviceId())
                .lastLoginTime(user.getLastLoginTime())
                .lastLoginIp(user.getLastLoginIp())
                .lastLoginDevice(user.getLastLoginDevice())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
