package com.rewindai.system.ticket.repository;

import com.rewindai.system.ticket.entity.FeedbackCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 反馈分类 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface FeedbackCategoryRepository extends JpaRepository<FeedbackCategory, Long> {

    Optional<FeedbackCategory> findByCode(String code);

    List<FeedbackCategory> findByIsEnabledTrueOrderBySortOrderAsc();

    boolean existsByCode(String code);
}
