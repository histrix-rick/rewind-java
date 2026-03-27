package com.rewindai.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 答题校验请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerCheckRequest {

    @NotNull(message = "学历水平ID不能为空")
    private Long educationLevelId;

    @NotNull(message = "答案不能为空")
    private List<QuestionAnswer> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionAnswer {
        @NotNull(message = "题目ID不能为空")
        private Long questionId;

        @NotBlank(message = "答案不能为空")
        private String answer;
    }
}
