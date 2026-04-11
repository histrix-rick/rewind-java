package com.rewindai.system.config.repository;

import com.rewindai.system.config.entity.SensitiveWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 敏感词 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface SensitiveWordRepository extends JpaRepository<SensitiveWord, Long> {

    Optional<SensitiveWord> findByWord(String word);

    boolean existsByWord(String word);

    Page<SensitiveWord> findByWordContainingIgnoreCase(String word, Pageable pageable);

    Page<SensitiveWord> findByWordType(String wordType, Pageable pageable);

    Page<SensitiveWord> findBySeverity(String severity, Pageable pageable);

    @Query("SELECT w FROM SensitiveWord w WHERE w.word LIKE %:keyword% OR w.remark LIKE %:keyword%")
    Page<SensitiveWord> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT w.word FROM SensitiveWord w")
    List<String> findAllWords();

    @Modifying
    @Query("DELETE FROM SensitiveWord w WHERE w.word IN (:words)")
    int deleteByWords(@Param("words") List<String> words);
}
