package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.DreamContext;
import com.rewindai.system.daydream.repository.DreamContextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 梦境上下文 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DreamContextService {

    private final DreamContextRepository dreamContextRepository;
    private final DaydreamService daydreamService;

    public Optional<DreamContext> findById(UUID id) {
        return dreamContextRepository.findById(id);
    }

    public Optional<DreamContext> findByDreamIdAndNodeId(UUID dreamId, UUID nodeId) {
        return dreamContextRepository.findByDreamIdAndNodeId(dreamId, nodeId);
    }

    public List<DreamContext> getContextsByDreamId(UUID dreamId) {
        return dreamContextRepository.findByDreamIdOrderByCreatedAtDesc(dreamId);
    }

    /**
     * 创建梦境上下文
     */
    @Transactional
    public DreamContext createContext(UUID userId, UUID dreamId, UUID nodeId, DreamContext context) {
        Daydream daydream = daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        context.setDreamId(dreamId);
        context.setNodeId(nodeId);
        if (context.getFinancialAmount() == null) {
            context.setFinancialAmount(BigDecimal.ZERO);
        }

        DreamContext saved = dreamContextRepository.save(context);
        log.info("梦境上下文创建成功: dreamId={}, nodeId={}", dreamId, nodeId);

        return saved;
    }

    /**
     * 更新梦境上下文
     */
    @Transactional
    public DreamContext updateContext(UUID userId, UUID id, DreamContext update) {
        DreamContext context = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "上下文不存在"));

        daydreamService.findByIdAndUserId(context.getDreamId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (update.getIdentityId() != null) {
            context.setIdentityId(update.getIdentityId());
        }
        if (update.getFinancialAmount() != null) {
            context.setFinancialAmount(update.getFinancialAmount());
        }
        if (update.getEducationLevelId() != null) {
            context.setEducationLevelId(update.getEducationLevelId());
        }
        if (update.getBirthProvince() != null) {
            context.setBirthProvince(update.getBirthProvince());
        }
        if (update.getBirthCity() != null) {
            context.setBirthCity(update.getBirthCity());
        }
        if (update.getBirthDistrict() != null) {
            context.setBirthDistrict(update.getBirthDistrict());
        }
        if (update.getBirthAddress() != null) {
            context.setBirthAddress(update.getBirthAddress());
        }
        if (update.getDreamProvince() != null) {
            context.setDreamProvince(update.getDreamProvince());
        }
        if (update.getDreamCity() != null) {
            context.setDreamCity(update.getDreamCity());
        }
        if (update.getDreamDistrict() != null) {
            context.setDreamDistrict(update.getDreamDistrict());
        }
        if (update.getDreamAddress() != null) {
            context.setDreamAddress(update.getDreamAddress());
        }

        return dreamContextRepository.save(context);
    }

    /**
     * 复制上下文到新节点
     */
    @Transactional
    public DreamContext copyToNode(UUID userId, UUID dreamId, UUID fromNodeId, UUID toNodeId) {
        daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        DreamContext sourceContext = findByDreamIdAndNodeId(dreamId, fromNodeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "源上下文不存在"));

        DreamContext newContext = DreamContext.builder()
                .dreamId(sourceContext.getDreamId())
                .nodeId(toNodeId)
                .identityId(sourceContext.getIdentityId())
                .financialAmount(sourceContext.getFinancialAmount())
                .educationLevelId(sourceContext.getEducationLevelId())
                .birthProvince(sourceContext.getBirthProvince())
                .birthCity(sourceContext.getBirthCity())
                .birthDistrict(sourceContext.getBirthDistrict())
                .birthAddress(sourceContext.getBirthAddress())
                .dreamProvince(sourceContext.getDreamProvince())
                .dreamCity(sourceContext.getDreamCity())
                .dreamDistrict(sourceContext.getDreamDistrict())
                .dreamAddress(sourceContext.getDreamAddress())
                .build();

        return dreamContextRepository.save(newContext);
    }

    @Transactional
    public void deleteByDreamId(UUID dreamId) {
        dreamContextRepository.deleteByDreamId(dreamId);
    }
}
