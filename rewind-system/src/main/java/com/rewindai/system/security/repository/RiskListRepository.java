package com.rewindai.system.security.repository;

import com.rewindai.system.security.entity.RiskList;
import com.rewindai.system.security.enums.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 风险名单 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface RiskListRepository extends JpaRepository<RiskList, Long> {

    Optional<RiskList> findByListTypeAndTargetValue(String listType, String targetValue);

    boolean existsByListTypeAndTargetValue(String listType, String targetValue);

    List<RiskList> findByListType(String listType);

    Page<RiskList> findByListType(String listType, Pageable pageable);

    Page<RiskList> findByRiskLevel(RiskLevel riskLevel, Pageable pageable);

    @Query("SELECT r FROM RiskList r WHERE r.expiresAt IS NULL OR r.expiresAt > :now")
    List<RiskList> findActive(@Param("now") OffsetDateTime now);

    @Query("SELECT r FROM RiskList r WHERE r.listType = :listType AND (r.expiresAt IS NULL OR r.expiresAt > :now)")
    List<RiskList> findActiveByListType(@Param("listType") String listType, @Param("now") OffsetDateTime now);

    @Query("SELECT r FROM RiskList r WHERE r.targetValue LIKE %:keyword% OR r.reason LIKE %:keyword%")
    Page<RiskList> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("DELETE FROM RiskList r WHERE r.expiresAt IS NOT NULL AND r.expiresAt < :now")
    int deleteExpired(@Param("now") OffsetDateTime now);
}
