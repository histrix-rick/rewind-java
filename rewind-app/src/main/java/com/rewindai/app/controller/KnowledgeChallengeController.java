package com.rewindai.app.controller;

import com.rewindai.app.dto.AnswerCheckRequest;
import com.rewindai.app.dto.AnswerCheckResponse;
import com.rewindai.app.dto.KnowledgeQuestionResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.EducationLevel;
import com.rewindai.system.daydream.entity.KnowledgeQuestion;
import com.rewindai.system.daydream.repository.EducationLevelRepository;
import com.rewindai.system.daydream.service.KnowledgeQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 知识问答挑战 Controller
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Tag(name = "知识问答挑战", description = "知识验证挑战接口")
@RestController
@RequestMapping("/api/knowledge-challenge")
@RequiredArgsConstructor
public class KnowledgeChallengeController {

    private final KnowledgeQuestionService knowledgeQuestionService;
    private final EducationLevelRepository educationLevelRepository;

    @Operation(summary = "获取指定学历水平的挑战题目")
    @GetMapping("/questions/{level}")
    public Result<List<KnowledgeQuestionResponse>> getChallengeQuestions(
            @PathVariable Integer level,
            @RequestParam(defaultValue = "3") int limit) {

        Optional<EducationLevel> educationLevelOpt = educationLevelRepository.findByLevel(level);
        if (educationLevelOpt.isEmpty()) {
            return Result.error(400, "学历水平不存在");
        }

        Long levelId = educationLevelOpt.get().getId();
        List<KnowledgeQuestion> questions = knowledgeQuestionService.getRandomQuestionsByLevel(levelId, limit);

        // 返回题目时不包含正确答案
        List<KnowledgeQuestionResponse> response = questions.stream()
                .map(q -> {
                    KnowledgeQuestionResponse r = KnowledgeQuestionResponse.from(q);
                    r.setCorrectAnswer(null);
                    r.setExplanation(null);
                    return r;
                })
                .toList();

        return Result.success(response);
    }

    @Operation(summary = "提交答案并校验")
    @PostMapping("/check")
    public Result<AnswerCheckResponse> checkAnswers(
            @Valid @RequestBody AnswerCheckRequest request) {

        Optional<EducationLevel> educationLevelOpt = educationLevelRepository.findById(request.getEducationLevelId());
        if (educationLevelOpt.isEmpty()) {
            return Result.error(400, "学历水平不存在");
        }

        EducationLevel educationLevel = educationLevelOpt.get();
        int passingScore = educationLevel.getPassingScore();
        int totalQuestions = request.getAnswers().size();

        // 获取所有题目
        List<Long> questionIds = request.getAnswers().stream()
                .map(AnswerCheckRequest.QuestionAnswer::getQuestionId)
                .toList();

        Map<Long, KnowledgeQuestion> questionMap = questionIds.stream()
                .map(id -> knowledgeQuestionService.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(KnowledgeQuestion::getId, q -> q));

        List<AnswerCheckResponse.QuestionResult> results = new ArrayList<>();
        int correctCount = 0;

        for (AnswerCheckRequest.QuestionAnswer answer : request.getAnswers()) {
            KnowledgeQuestion question = questionMap.get(answer.getQuestionId());
            if (question == null) {
                continue;
            }

            boolean correct = question.getCorrectAnswer().equalsIgnoreCase(answer.getAnswer());
            if (correct) {
                correctCount++;
            }

            results.add(AnswerCheckResponse.QuestionResult.builder()
                    .questionId(question.getId())
                    .correct(correct)
                    .userAnswer(answer.getAnswer())
                    .correctAnswer(question.getCorrectAnswer())
                    .explanation(question.getExplanation())
                    .build());
        }

        // 计算分数（百分制）
        int score = totalQuestions > 0 ? (correctCount * 100 / totalQuestions) : 0;
        boolean passed = score >= passingScore;

        String message = passed
                ? "恭喜通过！现代记忆觉醒，智力属性提升！"
                : "很遗憾未通过，建议加强学习后再试。";

        AnswerCheckResponse response = AnswerCheckResponse.builder()
                .passed(passed)
                .totalQuestions(totalQuestions)
                .correctCount(correctCount)
                .score(score)
                .passingScore(passingScore)
                .results(results)
                .message(message)
                .build();

        log.info("知识问答结果: levelId={}, score={}/{}, passed={}",
                request.getEducationLevelId(), score, passingScore, passed);

        return Result.success(response);
    }
}
