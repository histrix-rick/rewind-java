package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.EducationLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 学历知识水平配置响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationLevelResponse {

    private Long id;
    private String name;
    private String description;
    private Integer level;
    private Integer questionCount;
    private Integer passingScore;
    private Integer sortOrder;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static EducationLevelResponse from(EducationLevel level) {
        return EducationLevelResponse.builder()
                .id(level.getId())
                .name(level.getName())
                .description(level.getDescription())
                .level(level.getLevel())
                .questionCount(level.getQuestionCount())
                .passingScore(level.getPassingScore())
                .sortOrder(level.getSortOrder())
                .isActive(level.getIsActive())
                .createdAt(level.getCreatedAt())
                .updatedAt(level.getUpdatedAt())
                .build();
    }
}
