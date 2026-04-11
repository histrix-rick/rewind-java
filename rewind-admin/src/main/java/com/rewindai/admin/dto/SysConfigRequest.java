package com.rewindai.admin.dto;

import com.rewindai.system.config.enums.ConfigCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统配置请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysConfigRequest {

    @NotBlank(message = "配置key不能为空")
    private String configKey;

    @NotBlank(message = "配置名称不能为空")
    private String configName;

    private String configValue;

    @NotNull(message = "配置分类不能为空")
    private ConfigCategory configCategory;

    private String valueType;

    private String description;

    private Boolean isEncrypted;

    private Integer sortOrder;
}
