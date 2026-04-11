package com.rewindai.system.config.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.config.entity.SensitiveWord;
import com.rewindai.system.config.repository.SensitiveWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 系统敏感词管理 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service("sysSensitiveWordService")
@RequiredArgsConstructor
public class SysSensitiveWordService {

    private final SensitiveWordRepository sensitiveWordRepository;

    public Optional<SensitiveWord> findById(Long id) {
        return sensitiveWordRepository.findById(id);
    }

    public Optional<SensitiveWord> findByWord(String word) {
        return sensitiveWordRepository.findByWord(word);
    }

    public boolean existsByWord(String word) {
        return sensitiveWordRepository.existsByWord(word);
    }

    public Page<SensitiveWord> findAll(Pageable pageable) {
        return sensitiveWordRepository.findAll(pageable);
    }

    public Page<SensitiveWord> searchByWord(String word, Pageable pageable) {
        return sensitiveWordRepository.findByWordContainingIgnoreCase(word, pageable);
    }

    public Page<SensitiveWord> searchByKeyword(String keyword, Pageable pageable) {
        return sensitiveWordRepository.searchByKeyword(keyword, pageable);
    }

    public Page<SensitiveWord> findByWordType(String wordType, Pageable pageable) {
        return sensitiveWordRepository.findByWordType(wordType, pageable);
    }

    public Page<SensitiveWord> findBySeverity(String severity, Pageable pageable) {
        return sensitiveWordRepository.findBySeverity(severity, pageable);
    }

    public List<String> getAllWords() {
        return sensitiveWordRepository.findAllWords();
    }

    @Transactional
    public SensitiveWord createWord(SensitiveWord word) {
        if (existsByWord(word.getWord())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "敏感词已存在");
        }
        SensitiveWord saved = sensitiveWordRepository.save(word);
        log.info("敏感词创建成功: word={}", saved.getWord());
        return saved;
    }

    @Transactional
    public SensitiveWord updateWord(Long id, SensitiveWord update) {
        SensitiveWord word = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "敏感词不存在"));

        if (update.getWord() != null && !update.getWord().equals(word.getWord())) {
            if (existsByWord(update.getWord())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "敏感词已存在");
            }
            word.setWord(update.getWord());
        }
        if (update.getWordType() != null) {
            word.setWordType(update.getWordType());
        }
        if (update.getSeverity() != null) {
            word.setSeverity(update.getSeverity());
        }
        if (update.getRemark() != null) {
            word.setRemark(update.getRemark());
        }

        SensitiveWord saved = sensitiveWordRepository.save(word);
        log.info("敏感词更新成功: word={}", saved.getWord());
        return saved;
    }

    @Transactional
    public void deleteWord(Long id) {
        SensitiveWord word = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "敏感词不存在"));
        sensitiveWordRepository.delete(word);
        log.info("敏感词删除成功: word={}", word.getWord());
    }

    @Transactional
    public int deleteWords(List<String> words) {
        int count = sensitiveWordRepository.deleteByWords(words);
        log.info("批量删除敏感词成功: count={}", count);
        return count;
    }

    @Transactional
    public List<SensitiveWord> batchImportWords(List<String> words, String wordType, String severity) {
        List<SensitiveWord> imported = new ArrayList<>();
        for (String wordText : words) {
            String trimmed = wordText.trim();
            if (trimmed.isEmpty() || existsByWord(trimmed)) {
                continue;
            }
            SensitiveWord word = SensitiveWord.builder()
                    .word(trimmed)
                    .wordType(wordType != null ? wordType : "NORMAL")
                    .severity(severity != null ? severity : "MEDIUM")
                    .build();
            imported.add(sensitiveWordRepository.save(word));
        }
        log.info("批量导入敏感词成功: count={}", imported.size());
        return imported;
    }

    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        List<String> allWords = getAllWords();
        for (String word : allWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    public String filterSensitiveWords(String text, String replacement) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String result = text;
        List<String> allWords = getAllWords();
        for (String word : allWords) {
            if (result.contains(word)) {
                String replace = replacement != null ? replacement : "*".repeat(word.length());
                result = result.replace(word, replace);
            }
        }
        return result;
    }
}
