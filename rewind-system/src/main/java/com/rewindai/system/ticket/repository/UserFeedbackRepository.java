package com.rewindai.system.ticket.repository;

import com.rewindai.system.ticket.entity.UserFeedback;
import com.rewindai.system.ticket.enums.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户反馈 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface UserFeedbackRepository extends JpaRepository<UserFeedback, Long> {

    Page<UserFeedback> findByStatus(FeedbackStatus status, Pageable pageable);

    Page<UserFeedback> findByCategory(String category, Pageable pageable);

    Page<UserFeedback> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT f FROM UserFeedback f WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "f.title LIKE %:keyword% OR " +
           "f.content LIKE %:keyword% OR " +
           "f.userNickname LIKE %:keyword%)")
    Page<UserFeedback> searchFeedbacks(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(f) FROM UserFeedback f WHERE f.status = :status")
    long countByStatus(@Param("status") FeedbackStatus status);
}
