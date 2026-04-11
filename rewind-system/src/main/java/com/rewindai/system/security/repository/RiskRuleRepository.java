package com.rewindai.system.security.repository;

import com.rewindai.system.security.entity.RiskRule;
import com.rewindai.system.security.enums.RiskType;
import com.rewindai.system.security.enums.RuleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 风控规则 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface RiskRuleRepository extends JpaRepository<RiskRule, Long> {

    Optional<RiskRule> findByRuleCode(String ruleCode);

    boolean existsByRuleCode(String ruleCode);

    List<RiskRule> findByStatus(RuleStatus status);

    Page<RiskRule> findByStatus(RuleStatus status, Pageable pageable);

    Page<RiskRule> findByRiskType(RiskType riskType, Pageable pageable);

    @Query("SELECT r FROM RiskRule r WHERE r.ruleName LIKE %:keyword% OR r.ruleCode LIKE %:keyword% OR r.description LIKE %:keyword%")
    Page<RiskRule> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
