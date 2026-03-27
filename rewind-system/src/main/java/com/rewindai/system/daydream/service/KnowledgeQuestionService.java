package com.rewindai.system.daydream.service;

import com.rewindai.system.daydream.entity.KnowledgeQuestion;
import com.rewindai.system.daydream.repository.KnowledgeQuestionRepository;
import com.rewindai.system.dream.enums.QuestionSubject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 知识题库 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeQuestionService {

    private final KnowledgeQuestionRepository knowledgeQuestionRepository;

    public Optional<KnowledgeQuestion> findById(Long id) {
        return knowledgeQuestionRepository.findById(id);
    }

    /**
     * 随机获取指定学历水平的题目
     */
    public List<KnowledgeQuestion> getRandomQuestionsByLevel(Long levelId, int limit) {
        return knowledgeQuestionRepository.findRandomQuestionsByLevel(levelId, limit);
    }

    /**
     * 随机获取指定学历水平和科目的一道题目
     */
    public KnowledgeQuestion getRandomQuestionByLevelAndSubject(Long levelId, QuestionSubject subject) {
        return knowledgeQuestionRepository.findRandomQuestionByLevelAndSubject(levelId, subject.getCode());
    }

    /**
     * 获取指定学历水平和科目的所有题目
     */
    public List<KnowledgeQuestion> getQuestionsByLevelAndSubject(Long levelId, QuestionSubject subject) {
        return knowledgeQuestionRepository.findByEducationLevelIdAndSubjectAndIsActiveTrue(levelId, subject);
    }

    /**
     * 分页获取题目
     */
    public List<KnowledgeQuestion> getRandomQuestionsByLevelWithPage(Long levelId, Pageable pageable) {
        return knowledgeQuestionRepository.findByEducationLevelIdAndIsActiveTrue(levelId, pageable);
    }
}
