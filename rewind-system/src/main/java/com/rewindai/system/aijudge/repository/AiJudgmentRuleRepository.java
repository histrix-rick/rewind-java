package com.rewindai.system.aijudge.repository;

import com.rewindai.system.aijudge.entity.AiJudgmentRule;
import com.rewindai.system.aijudge.enums.RuleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI 判定规则 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface AiJudgmentRuleRepository extends JpaRepository<AiJudgmentRule, Long> {

    List<AiJudgmentRule> findByRuleTypeAndIsActiveTrueOrderByPriorityDesc(RuleType ruleType);

    List<AiJudgmentRule> findByIsActiveTrueOrderByPriorityDesc();
}
