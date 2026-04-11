package com.rewindai.system.security.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.security.entity.RiskList;
import com.rewindai.system.security.enums.RiskLevel;
import com.rewindai.system.security.repository.RiskListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 风险名单 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskListService {

    private final RiskListRepository riskListRepository;

    public Optional<RiskList> findById(Long id) {
        return riskListRepository.findById(id);
    }

    public Optional<RiskList> findByListTypeAndTargetValue(String listType, String targetValue) {
        return riskListRepository.findByListTypeAndTargetValue(listType, targetValue);
    }

    public boolean existsByListTypeAndTargetValue(String listType, String targetValue) {
        return riskListRepository.existsByListTypeAndTargetValue(listType, targetValue);
    }

    public boolean isBlacklisted(String listType, String targetValue) {
        Optional<RiskList> entry = findByListTypeAndTargetValue(listType, targetValue);
        if (entry.isEmpty()) {
            return false;
        }
        RiskList list = entry.get();
        return list.getExpiresAt() == null || list.getExpiresAt().isAfter(OffsetDateTime.now());
    }

    public List<RiskList> findActive() {
        return riskListRepository.findActive(OffsetDateTime.now());
    }

    public List<RiskList> findActiveByListType(String listType) {
        return riskListRepository.findActiveByListType(listType, OffsetDateTime.now());
    }

    public Page<RiskList> findAll(Pageable pageable) {
        return riskListRepository.findAll(pageable);
    }

    public Page<RiskList> findByListType(String listType, Pageable pageable) {
        return riskListRepository.findByListType(listType, pageable);
    }

    public Page<RiskList> findByRiskLevel(RiskLevel riskLevel, Pageable pageable) {
        return riskListRepository.findByRiskLevel(riskLevel, pageable);
    }

    public Page<RiskList> searchByKeyword(String keyword, Pageable pageable) {
        return riskListRepository.searchByKeyword(keyword, pageable);
    }

    @Transactional
    public RiskList addToRiskList(RiskList entry) {
        if (existsByListTypeAndTargetValue(entry.getListType(), entry.getTargetValue())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "该目标已在风险名单中");
        }
        RiskList saved = riskListRepository.save(entry);
        log.info("添加风险名单成功: listType={}, targetValue={}", saved.getListType(), saved.getTargetValue());
        return saved;
    }

    @Transactional
    public RiskList updateRiskList(Long id, RiskList update) {
        RiskList entry = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "风险名单不存在"));

        if (update.getRiskLevel() != null) {
            entry.setRiskLevel(update.getRiskLevel());
        }
        if (update.getReason() != null) {
            entry.setReason(update.getReason());
        }
        if (update.getExpiresAt() != null) {
            entry.setExpiresAt(update.getExpiresAt());
        }

        RiskList saved = riskListRepository.save(entry);
        log.info("更新风险名单成功: id={}", saved.getId());
        return saved;
    }

    @Transactional
    public void removeFromRiskList(Long id) {
        RiskList entry = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "风险名单不存在"));
        riskListRepository.delete(entry);
        log.info("移除风险名单成功: listType={}, targetValue={}", entry.getListType(), entry.getTargetValue());
    }

    @Transactional
    public int cleanExpired() {
        int count = riskListRepository.deleteExpired(OffsetDateTime.now());
        log.info("清理过期风险名单: count={}", count);
        return count;
    }
}
