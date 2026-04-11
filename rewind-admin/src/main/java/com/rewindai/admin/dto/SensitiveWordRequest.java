package com.rewindai.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 敏感词请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveWordRequest {

    @NotBlank(message = "敏感词不能为空")
    private String word;

    private String wordType;

    private String severity;

    private String remark;
}
