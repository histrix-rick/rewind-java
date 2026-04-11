package com.rewindai.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 管理员更新请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String nickname;

    private String avatar;

    private String phoneNumber;

    private String email;

    private String status;

    private List<Long> roleIds;
}
