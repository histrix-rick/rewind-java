package com.rewindai.app.dto;

import com.rewindai.system.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 用户详情页响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {

    private String id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private LocalDate birthDate;
    private String gender;
    private long followerCount;
    private long followingCount;
    private Boolean isFollowing;

    public static UserDetailResponse from(User user, long followerCount, long followingCount, Boolean isFollowing) {
        if (user == null) {
            return null;
        }
        return UserDetailResponse.builder()
                .id(user.getId() != null ? user.getId().toString() : null)
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .birthDate(user.getBirthDate())
                .gender(user.getGender() != null ? user.getGender().getDesc() : null)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .isFollowing(isFollowing)
                .build();
    }
}
