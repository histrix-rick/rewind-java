package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.TimelineNode;
import com.rewindai.system.daydream.entity.TimelineNodeExtension;
import com.rewindai.system.daydream.repository.TimelineNodeExtensionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 时间轴节点扩展 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineNodeExtensionService {

    private final TimelineNodeExtensionRepository extensionRepository;
    private final DaydreamService daydreamService;
    private final TimelineService timelineService;

    public Optional<TimelineNodeExtension> findByNodeId(UUID nodeId) {
        return extensionRepository.findByNodeId(nodeId);
    }

    public Optional<TimelineNodeExtension> findById(UUID id) {
        return extensionRepository.findById(id);
    }

    /**
     * 获取或创建节点扩展
     */
    @Transactional
    public TimelineNodeExtension getOrCreateExtension(UUID userId, UUID nodeId) {
        return extensionRepository.findByNodeId(nodeId)
                .orElseGet(() -> {
                    // 验证节点属于用户
                    TimelineNode node = timelineService.findById(nodeId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "节点不存在"));
                    daydreamService.findByIdAndUserId(node.getDreamId(), userId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

                    TimelineNodeExtension extension = TimelineNodeExtension.builder()
                            .nodeId(nodeId)
                            .assetInfoCompleted(false)
                            .build();
                    return extensionRepository.save(extension);
                });
    }

    /**
     * 更新资产信息完成状态
     */
    @Transactional
    public TimelineNodeExtension updateAssetInfoCompleted(UUID userId, UUID nodeId, Boolean completed) {
        TimelineNodeExtension extension = getOrCreateExtension(userId, nodeId);
        extension.setAssetInfoCompleted(completed);
        TimelineNodeExtension saved = extensionRepository.save(extension);
        log.info("资产信息完成状态更新: nodeId={}, completed={}", nodeId, completed);
        return saved;
    }

    /**
     * 更新AI推理过程
     */
    @Transactional
    public TimelineNodeExtension updateAiReasoningTrace(UUID userId, UUID nodeId, String reasoningTrace) {
        TimelineNodeExtension extension = getOrCreateExtension(userId, nodeId);
        extension.setAiReasoningTrace(reasoningTrace);
        return extensionRepository.save(extension);
    }

    /**
     * 更新属性变更
     */
    @Transactional
    public TimelineNodeExtension updateAttributeUpdates(UUID userId, UUID nodeId, Map<String, Object> updates) {
        TimelineNodeExtension extension = getOrCreateExtension(userId, nodeId);
        extension.setAttributeUpdates(updates);
        return extensionRepository.save(extension);
    }

    /**
     * 更新关系变更
     */
    @Transactional
    public TimelineNodeExtension updateRelationshipUpdates(UUID userId, UUID nodeId, Map<String, Object> updates) {
        TimelineNodeExtension extension = getOrCreateExtension(userId, nodeId);
        extension.setRelationshipUpdates(updates);
        return extensionRepository.save(extension);
    }

    /**
     * 更新身份变更
     */
    @Transactional
    public TimelineNodeExtension updateIdentityUpdates(UUID userId, UUID nodeId, Map<String, Object> updates) {
        TimelineNodeExtension extension = getOrCreateExtension(userId, nodeId);
        extension.setIdentityUpdates(updates);
        return extensionRepository.save(extension);
    }

    /**
     * 完整更新扩展信息
     */
    @Transactional
    public TimelineNodeExtension updateExtension(UUID userId, UUID nodeId,
                                                   Boolean assetInfoCompleted,
                                                   String aiReasoningTrace,
                                                   Map<String, Object> attributeUpdates,
                                                   Map<String, Object> relationshipUpdates,
                                                   Map<String, Object> identityUpdates) {
        TimelineNodeExtension extension = getOrCreateExtension(userId, nodeId);

        if (assetInfoCompleted != null) {
            extension.setAssetInfoCompleted(assetInfoCompleted);
        }
        if (aiReasoningTrace != null) {
            extension.setAiReasoningTrace(aiReasoningTrace);
        }
        if (attributeUpdates != null) {
            extension.setAttributeUpdates(attributeUpdates);
        }
        if (relationshipUpdates != null) {
            extension.setRelationshipUpdates(relationshipUpdates);
        }
        if (identityUpdates != null) {
            extension.setIdentityUpdates(identityUpdates);
        }

        return extensionRepository.save(extension);
    }

    @Transactional
    public void deleteByNodeId(UUID nodeId) {
        extensionRepository.findByNodeId(nodeId).ifPresent(extensionRepository::delete);
    }
}
