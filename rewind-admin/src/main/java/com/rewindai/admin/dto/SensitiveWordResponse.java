package com.rewindai.admin.dto;

import com.rewindai.system.config.entity.SensitiveWord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 敏感词响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveWordResponse {

    private Long id;
    private String word;
    private String wordType;
    private String severity;
    private String remark;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static SensitiveWordResponse from(SensitiveWord word) {
        return SensitiveWordResponse.builder()
                .id(word.getId())
                .word(word.getWord())
                .wordType(word.getWordType())
                .severity(word.getSeverity())
                .remark(word.getRemark())
                .createdAt(word.getCreatedAt())
                .updatedAt(word.getUpdatedAt())
                .build();
    }
}
