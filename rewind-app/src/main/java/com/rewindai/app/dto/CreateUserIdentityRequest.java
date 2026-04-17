package com.rewindai.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建用户自定义身份请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserIdentityRequest {

    @NotBlank(message = "身份名称不能为空")
    private String name;

    private String icon;

    private String description;

    @NotNull(message = "最小年龄不能为空")
    private Integer minAge;

    @NotNull(message = "最大年龄不能为空")
    private Integer maxAge;
}
