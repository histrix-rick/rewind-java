package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.DreamRelationship;
import com.rewindai.system.daydream.repository.DreamRelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 梦境人物关系 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DreamRelationshipService {

    private final DreamRelationshipRepository dreamRelationshipRepository;
    private final DaydreamService daydreamService;

    public Optional<DreamRelationship> findById(UUID id) {
        return dreamRelationshipRepository.findById(id);
    }

    public List<DreamRelationship> getRelationshipsByDreamAndNode(UUID dreamId, UUID nodeId) {
        return dreamRelationshipRepository.findByDreamIdAndNodeIdOrderByCreatedAtAsc(dreamId, nodeId);
    }

    public List<DreamRelationship> getRelationshipsByDream(UUID dreamId) {
        return dreamRelationshipRepository.findByDreamIdOrderByCreatedAtAsc(dreamId);
    }

    /**
     * 添加人物关系
     */
    @Transactional
    public DreamRelationship addRelationship(UUID userId, UUID dreamId, UUID nodeId, DreamRelationship relationship) {
        Daydream daydream = daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        relationship.setDreamId(dreamId);
        relationship.setNodeId(nodeId);
        if (relationship.getIntimacyLevel() == null) {
            relationship.setIntimacyLevel(1);
        }

        DreamRelationship saved = dreamRelationshipRepository.save(relationship);
        log.info("人物关系添加成功: dreamId={}, nodeId={}, personName={}", dreamId, nodeId, relationship.getPersonName());

        return saved;
    }

    /**
     * 更新人物关系
     */
    @Transactional
    public DreamRelationship updateRelationship(UUID userId, UUID id, DreamRelationship update) {
        DreamRelationship relationship = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "关系不存在"));

        daydreamService.findByIdAndUserId(relationship.getDreamId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (update.getPersonName() != null) {
            relationship.setPersonName(update.getPersonName());
        }
        if (update.getRelationshipTypeId() != null) {
            relationship.setRelationshipTypeId(update.getRelationshipTypeId());
        }
        if (update.getIntimacyLevel() != null) {
            relationship.setIntimacyLevel(update.getIntimacyLevel());
        }
        if (update.getIntimacyDescription() != null) {
            relationship.setIntimacyDescription(update.getIntimacyDescription());
        }
        if (update.getNotes() != null) {
            relationship.setNotes(update.getNotes());
        }

        return dreamRelationshipRepository.save(relationship);
    }

    /**
     * 删除人物关系
     */
    @Transactional
    public void deleteRelationship(UUID userId, UUID id) {
        DreamRelationship relationship = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "关系不存在"));

        daydreamService.findByIdAndUserId(relationship.getDreamId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        dreamRelationshipRepository.delete(relationship);
        log.info("人物关系删除成功: relationshipId={}", id);
    }

    /**
     * 复制关系到新节点
     */
    @Transactional
    public List<DreamRelationship> copyToNode(UUID userId, UUID dreamId, UUID fromNodeId, UUID toNodeId) {
        daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        List<DreamRelationship> sourceRelationships = getRelationshipsByDreamAndNode(dreamId, fromNodeId);

        for (DreamRelationship source : sourceRelationships) {
            DreamRelationship newRelationship = DreamRelationship.builder()
                    .dreamId(dreamId)
                    .nodeId(toNodeId)
                    .personName(source.getPersonName())
                    .relationshipTypeId(source.getRelationshipTypeId())
                    .intimacyLevel(source.getIntimacyLevel())
                    .intimacyDescription(source.getIntimacyDescription())
                    .notes(source.getNotes())
                    .build();
            dreamRelationshipRepository.save(newRelationship);
        }

        log.info("人物关系复制成功: dreamId={}, fromNode={}, toNode={}", dreamId, fromNodeId, toNodeId);

        return getRelationshipsByDreamAndNode(dreamId, toNodeId);
    }

    @Transactional
    public void deleteByDreamIdAndNodeId(UUID dreamId, UUID nodeId) {
        dreamRelationshipRepository.deleteByDreamIdAndNodeId(dreamId, nodeId);
    }
}
