package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.KnowledgeQuestion;
import com.rewindai.system.dream.enums.QuestionSubject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 知识题库 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface KnowledgeQuestionRepository extends JpaRepository<KnowledgeQuestion, Long> {

    List<KnowledgeQuestion> findByEducationLevelIdAndIsActiveTrue(Long educationLevelId, Pageable pageable);

    List<KnowledgeQuestion> findByEducationLevelIdAndSubjectAndIsActiveTrue(
            Long educationLevelId, QuestionSubject subject);

    @Query(value = "SELECT * FROM knowledge_questions WHERE education_level_id = :levelId AND is_active = true " +
            "ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<KnowledgeQuestion> findRandomQuestionsByLevel(@Param("levelId") Long levelId, @Param("limit") int limit);

    @Query(value = "SELECT * FROM knowledge_questions WHERE education_level_id = :levelId " +
            "AND subject = :subject AND is_active = true ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    KnowledgeQuestion findRandomQuestionByLevelAndSubject(
            @Param("levelId") Long levelId, @Param("subject") String subject);
}
