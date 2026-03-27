package com.rewindai.system.aijudge.entity;

import com.rewindai.system.aijudge.enums.RuleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * AI 判定规则库实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_judgment_rules")
public class AiJudgmentRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_type", nullable = false, length = 30)
    @Convert(converter = RuleTypeConverter.class)
    private RuleType ruleType;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "condition_pattern", columnDefinition = "TEXT")
    private String conditionPattern;

    @Column(name = "judgment_result", nullable = false)
    private Boolean judgmentResult;

    @Column(name = "reasoning_template", columnDefinition = "TEXT")
    private String reasoningTemplate;

    @Column(name = "example_question", columnDefinition = "TEXT")
    private String exampleQuestion;

    @Column(name = "example_answer", columnDefinition = "TEXT")
    private String exampleAnswer;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "priority", nullable = false)
    @Builder.Default
    private Integer priority = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Converter
    public static class RuleTypeConverter implements AttributeConverter<RuleType, Integer> {
        @Override
        public Integer convertToDatabaseColumn(RuleType type) {
            return type != null ? type.getCode() : RuleType.PERSONAL_FATE.getCode();
        }

        @Override
        public RuleType convertToEntityAttribute(Integer code) {
            return code != null ? RuleType.fromCode(code) : RuleType.PERSONAL_FATE;
        }
    }
}
