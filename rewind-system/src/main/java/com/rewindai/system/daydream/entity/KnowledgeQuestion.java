package com.rewindai.system.daydream.entity;

import com.rewindai.system.daydream.enums.QuestionSubject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * 知识题库实体（AI生成）
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "knowledge_questions", indexes = {
        @Index(name = "idx_q_edu_level", columnList = "education_level_id"),
        @Index(name = "idx_q_subject", columnList = "subject"),
        @Index(name = "idx_q_active", columnList = "is_active")
})
public class KnowledgeQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "education_level_id", nullable = false)
    private Long educationLevelId;

    @Column(name = "subject", nullable = false, length = 20)
    @Convert(converter = QuestionSubjectConverter.class)
    private QuestionSubject subject;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "option_a", length = 500)
    private String optionA;

    @Column(name = "option_b", length = 500)
    private String optionB;

    @Column(name = "option_c", length = 500)
    private String optionC;

    @Column(name = "option_d", length = 500)
    private String optionD;

    @Column(name = "correct_answer", nullable = false, length = 1)
    private String correctAnswer;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "difficulty", nullable = false)
    @Builder.Default
    private Integer difficulty = 1;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Converter
    public static class QuestionSubjectConverter implements AttributeConverter<QuestionSubject, String> {
        @Override
        public String convertToDatabaseColumn(QuestionSubject subject) {
            return subject != null ? subject.getCode() : QuestionSubject.CHINESE.getCode();
        }

        @Override
        public QuestionSubject convertToEntityAttribute(String code) {
            return code != null ? QuestionSubject.fromCode(code) : QuestionSubject.CHINESE;
        }
    }
}
