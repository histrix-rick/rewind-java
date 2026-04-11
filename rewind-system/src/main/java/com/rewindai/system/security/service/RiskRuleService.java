package com.rewindai.system.security.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.security.entity.RiskRule;
import com.rewindai.system.security.enums.RiskType;
import com.rewindai.system.security.enums.RuleStatus;
import com.rewindai.system.security.repository.RiskRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 风控规则 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskRuleService {

    private final RiskRuleRepository riskRuleRepository;

    public Optional<RiskRule> findById(Long id) {
        return riskRuleRepository.findById(id);
    }

    public Optional<RiskRule> findByRuleCode(String ruleCode) {
        return riskRuleRepository.findByRuleCode(ruleCode);
    }

    public boolean existsByRuleCode(String ruleCode) {
        return riskRuleRepository.existsByRuleCode(ruleCode);
    }

    public List<RiskRule> findActiveRules() {
        return riskRuleRepository.findByStatus(RuleStatus.ACTIVE);
    }

    public Page<RiskRule> findAll(Pageable pageable) {
        return riskRuleRepository.findAll(pageable);
    }

    public Page<RiskRule> findByStatus(RuleStatus status, Pageable pageable) {
        return riskRuleRepository.findByStatus(status, pageable);
    }

    public Page<RiskRule> findByRiskType(RiskType riskType, Pageable pageable) {
        return riskRuleRepository.findByRiskType(riskType, pageable);
    }

    public Page<RiskRule> searchByKeyword(String keyword, Pageable pageable) {
        return riskRuleRepository.searchByKeyword(keyword, pageable);
    }

    @Transactional
    public RiskRule createRule(RiskRule rule) {
        if (existsByRuleCode(rule.getRuleCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "规则编码已存在");
        }
        RiskRule saved = riskRuleRepository.save(rule);
        log.info("风控规则创建成功: ruleCode={}", saved.getRuleCode());
        return saved;
    }

    @Transactional
    public RiskRule updateRule(Long id, RiskRule update) {
        RiskRule rule = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "风控规则不存在"));

        if (update.getRuleCode() != null && !update.getRuleCode().equals(rule.getRuleCode())) {
            if (existsByRuleCode(update.getRuleCode())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "规则编码已存在");
            }
            rule.setRuleCode(update.getRuleCode());
        }
        if (update.getRuleName() != null) {
            rule.setRuleName(update.getRuleName());
        }
        if (update.getRiskType() != null) {
            rule.setRiskType(update.getRiskType());
        }
        if (update.getRiskLevel() != null) {
            rule.setRiskLevel(update.getRiskLevel());
        }
        if (update.getRuleConfig() != null) {
            rule.setRuleConfig(update.getRuleConfig());
        }
        if (update.getDescription() != null) {
            rule.setDescription(update.getDescription());
        }
        if (update.getStatus() != null) {
            rule.setStatus(update.getStatus());
        }
        if (update.getSortOrder() != null) {
            rule.setSortOrder(update.getSortOrder());
        }

        RiskRule saved = riskRuleRepository.save(rule);
        log.info("风控规则更新成功: ruleCode={}", saved.getRuleCode());
        return saved;
    }

    @Transactional
    public void deleteRule(Long id) {
        RiskRule rule = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "风控规则不存在"));
        riskRuleRepository.delete(rule);
        log.info("风控规则删除成功: ruleCode={}", rule.getRuleCode());
    }
}
