package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.KnowledgeQuestion;
import com.rewindai.system.dream.enums.QuestionSubject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 知识题库响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeQuestionResponse {

    private Long id;
    private Long educationLevelId;
    private QuestionSubject subject;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String explanation;
    private Integer difficulty;
    private Boolean isActive;
    private OffsetDateTime createdAt;

    public static KnowledgeQuestionResponse from(KnowledgeQuestion question) {
        return KnowledgeQuestionResponse.builder()
                .id(question.getId())
                .educationLevelId(question.getEducationLevelId())
                .subject(question.getSubject())
                .questionText(question.getQuestionText())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .correctAnswer(question.getCorrectAnswer())
                .explanation(question.getExplanation())
                .difficulty(question.getDifficulty())
                .isActive(question.getIsActive())
                .createdAt(question.getCreatedAt())
                .build();
    }
}
