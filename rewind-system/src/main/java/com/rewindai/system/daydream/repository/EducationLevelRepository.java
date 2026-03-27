package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.EducationLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学历知识水平配置 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface EducationLevelRepository extends JpaRepository<EducationLevel, Long> {

    List<EducationLevel> findByIsActiveTrueOrderBySortOrderAsc();

    Optional<EducationLevel> findByLevel(Integer level);
}
