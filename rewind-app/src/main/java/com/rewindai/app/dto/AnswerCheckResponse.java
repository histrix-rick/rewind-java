package com.rewindai.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 答题校验响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerCheckResponse {

    private Boolean passed;
    private Integer totalQuestions;
    private Integer correctCount;
    private Integer score;
    private Integer passingScore;
    private List<QuestionResult> results;
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResult {
        private Long questionId;
        private Boolean correct;
        private String userAnswer;
        private String correctAnswer;
        private String explanation;
    }
}
