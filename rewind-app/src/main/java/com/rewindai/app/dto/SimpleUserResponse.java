package com.rewindai.app.dto;

import com.rewindai.system.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 简化用户信息响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUserResponse {
    private String id;
    private String username;
    private String nickname;
    private String avatarUrl;

    public static SimpleUserResponse from(User user) {
        if (user == null) {
            return null;
        }
        return SimpleUserResponse.builder()
                .id(user.getId() != null ? user.getId().toString() : null)
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
