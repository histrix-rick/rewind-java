package com.rewindai.system.daydream.service;

import com.rewindai.system.daydream.entity.EducationLevel;
import com.rewindai.system.daydream.repository.EducationLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 学历知识水平配置 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EducationLevelService {

    private final EducationLevelRepository educationLevelRepository;

    public Optional<EducationLevel> findById(Long id) {
        return educationLevelRepository.findById(id);
    }

    public List<EducationLevel> getActiveLevels() {
        List<EducationLevel> levels = educationLevelRepository.findByIsActiveTrueOrderBySortOrderAsc();
        log.info("查询学历水平数据，数量: {}", levels.size());
        return levels;
    }
}
