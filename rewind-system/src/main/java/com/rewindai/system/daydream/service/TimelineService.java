package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.DreamBranch;
import com.rewindai.system.daydream.entity.TimelineNode;
import com.rewindai.system.daydream.enums.JudgmentStatus;
import com.rewindai.system.daydream.enums.NodeType;
import com.rewindai.system.daydream.repository.DreamBranchRepository;
import com.rewindai.system.daydream.repository.TimelineNodeRepository;
import com.rewindai.system.user.entity.UserAttribute;
import com.rewindai.system.user.service.AttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 时间轴 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineService {

    private final TimelineNodeRepository timelineNodeRepository;
    private final DreamBranchRepository dreamBranchRepository;
    private final DaydreamService daydreamService;
    private final AttributeService attributeService;

    public Optional<TimelineNode> findById(UUID id) {
        return timelineNodeRepository.findById(id);
    }

    public List<TimelineNode> getTimeline(UUID dreamId, UUID branchId) {
        if (branchId == null) {
            // branchId为null时，返回该梦境所有节点（按时间和序号排序）
            return timelineNodeRepository.findByDreamId(
                    dreamId, Sort.by(Sort.Direction.ASC, "nodeDate", "sequenceNum"));
        }
        return timelineNodeRepository.findByDreamIdAndBranchId(
                dreamId, branchId, Sort.by(Sort.Direction.ASC, "sequenceNum"));
    }

    /**
     * 获取时间轴（过滤掉失败节点）
     * 用于访问他人梦境时，不显示判定失败的节点
     */
    public List<TimelineNode> getTimelineFiltered(UUID dreamId, UUID branchId) {
        List<TimelineNode> allNodes = getTimeline(dreamId, branchId);
        return allNodes.stream()
                .filter(node -> {
                    // 只显示成功或处理中的节点，不显示失败的节点
                    // 1. 如果是FAILED状态，过滤掉
                    if (node.getJudgmentStatus() == com.rewindai.system.daydream.enums.JudgmentStatus.FAILED) {
                        return false;
                    }
                    // 2. 如果isApproved明确为false，过滤掉
                    if (node.getIsApproved() != null && !node.getIsApproved()) {
                        return false;
                    }
                    // 3. 其他情况都显示（null、true、PENDING、PROCESSING、SUCCESS）
                    return true;
                })
                .toList();
    }

    public Page<TimelineNode> getTimelinePage(UUID dreamId, UUID branchId, Pageable pageable) {
        return timelineNodeRepository.findByDreamIdAndBranchId(dreamId, branchId, pageable);
    }

    /**
     * 添加时间轴节点
     */
    @Transactional
    public TimelineNode addNode(UUID userId, UUID dreamId, UUID branchId,
                                  String userDecision, String decisionSummary,
                                  String aiFeedback, String reasoningTrace,
                                  Boolean isApproved, LocalDate nodeDate) {
        Daydream daydream = daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (branchId == null) {
            branchId = daydream.getCurrentBranchId();
        }

        if (branchId == null) {
            DreamBranch defaultBranch = createBranch(dreamId, null, "主线");
            branchId = defaultBranch.getId();
            daydream.setCurrentBranchId(branchId);
            // 保存daydream
            daydreamService.save(daydream);
        }

        Integer nextSequence = getNextSequenceNum(dreamId, branchId);

        UserAttribute attribute = attributeService.getOrCreateAttribute(userId);
        Map<String, Object> attributeSnapshot = attributeService.getAttributeSnapshot(attribute);

        TimelineNode node = TimelineNode.builder()
                .dreamId(dreamId)
                .branchId(branchId)
                .sequenceNum(nextSequence)
                .nodeDate(nodeDate != null ? nodeDate : daydream.getCurrentDate())
                .userDecision(userDecision)
                .decisionSummary(decisionSummary)
                .aiFeedback(aiFeedback)
                .reasoningTrace(reasoningTrace)
                .isApproved(isApproved)
                .nodeType(NodeType.NORMAL)
                .isPublic(false)
                .build();
        node.setAttributeSnapshot(attributeSnapshot);

        TimelineNode saved = timelineNodeRepository.save(node);
        log.info("时间轴节点添加成功: daydreamId={}, nodeId={}", dreamId, saved.getId());

        return saved;
    }

    /**
     * 从指定节点创建新分支
     */
    @Transactional
    public DreamBranch createBranchFromNode(UUID userId, UUID dreamId, UUID nodeId, String branchName) {
        Daydream daydream = daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        TimelineNode fromNode = findById(nodeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "节点不存在"));

        if (!fromNode.getDreamId().equals(dreamId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "节点不属于该白日梦");
        }

        DreamBranch newBranch = createBranch(dreamId, nodeId, branchName);

        List<TimelineNode> nodesToCopy = timelineNodeRepository
                .findByDreamIdAndBranchIdAndSequenceNumLessThanEqualOrderBySequenceNumAsc(
                        dreamId, fromNode.getBranchId(), fromNode.getSequenceNum());

        for (TimelineNode node : nodesToCopy) {
            TimelineNode copied = TimelineNode.builder()
                    .dreamId(dreamId)
                    .branchId(newBranch.getId())
                    .sequenceNum(node.getSequenceNum())
                    .nodeDate(node.getNodeDate())
                    .userDecision(node.getUserDecision())
                    .decisionSummary(node.getDecisionSummary())
                    .aiFeedback(node.getAiFeedback())
                    .reasoningTrace(node.getReasoningTrace())
                    .isApproved(node.getIsApproved())
                    .nodeType(node.getNodeType())
                    .isPublic(node.getIsPublic())
                    .build();
            copied.setAttributeSnapshot(node.getAttributeSnapshot());
            timelineNodeRepository.save(copied);
        }

        daydream.setCurrentBranchId(newBranch.getId());
        daydream.setCurrentDate(fromNode.getNodeDate());

        log.info("分支创建成功: daydreamId={}, branchId={}", dreamId, newBranch.getId());

        return newBranch;
    }

    private DreamBranch createBranch(UUID dreamId, UUID parentNodeId, String branchName) {
        DreamBranch branch = DreamBranch.builder()
                .dreamId(dreamId)
                .parentNodeId(parentNodeId)
                .branchName(branchName)
                .build();
        return dreamBranchRepository.save(branch);
    }

    private Integer getNextSequenceNum(UUID dreamId, UUID branchId) {
        return timelineNodeRepository.findFirstByDreamIdAndBranchIdOrderBySequenceNumDesc(dreamId, branchId)
                .map(node -> node.getSequenceNum() + 1)
                .orElse(1);
    }

    /**
     * 撤销上一个决策
     */
    @Transactional
    public void undoLastNode(UUID userId, UUID dreamId, UUID branchId) {
        Daydream daydream = daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (branchId == null) {
            branchId = daydream.getCurrentBranchId();
        }

        if (branchId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "没有可撤销的决策");
        }

        // 获取最后一个节点
        Optional<TimelineNode> lastNodeOpt = timelineNodeRepository
                .findFirstByDreamIdAndBranchIdOrderBySequenceNumDesc(dreamId, branchId);

        if (lastNodeOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "没有可撤销的决策");
        }

        TimelineNode lastNode = lastNodeOpt.get();

        // 删除该节点
        timelineNodeRepository.delete(lastNode);

        // 获取前一个节点
        Optional<TimelineNode> prevNodeOpt = timelineNodeRepository
                .findFirstByDreamIdAndBranchIdOrderBySequenceNumDesc(dreamId, branchId);

        if (prevNodeOpt.isPresent()) {
            // 回退到前一个节点的日期
            daydream.setCurrentDate(prevNodeOpt.get().getNodeDate());
        } else {
            // 没有节点了，回退到梦境起始日期
            daydream.setCurrentDate(daydream.getStartDate());
        }

        log.info("撤销决策成功: daydreamId={}, nodeId={}", dreamId, lastNode.getId());
    }

    /**
     * 获取最后一个节点
     */
    public Optional<TimelineNode> getLastNode(UUID dreamId, UUID branchId) {
        return timelineNodeRepository.findFirstByDreamIdAndBranchIdOrderBySequenceNumDesc(dreamId, branchId);
    }

    /**
     * 保存节点
     */
    @Transactional
    public TimelineNode save(TimelineNode node) {
        return timelineNodeRepository.save(node);
    }

    /**
     * 添加待处理节点（PROCESSING状态）
     */
    @Transactional
    public TimelineNode addPendingNode(UUID userId, UUID dreamId, UUID branchId,
                                         String userDecision, LocalDate nodeDate) {
        Daydream daydream = daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (branchId == null) {
            branchId = daydream.getCurrentBranchId();
        }

        if (branchId == null) {
            DreamBranch defaultBranch = createBranch(dreamId, null, "主线");
            branchId = defaultBranch.getId();
            daydream.setCurrentBranchId(branchId);
            daydreamService.save(daydream);
        }

        Integer nextSequence = getNextSequenceNum(dreamId, branchId);

        UserAttribute attribute = attributeService.getOrCreateAttribute(userId);
        Map<String, Object> attributeSnapshot = attributeService.getAttributeSnapshot(attribute);

        TimelineNode node = TimelineNode.builder()
                .dreamId(dreamId)
                .branchId(branchId)
                .sequenceNum(nextSequence)
                .nodeDate(nodeDate != null ? nodeDate : daydream.getCurrentDate())
                .userDecision(userDecision)
                .judgmentStatus(JudgmentStatus.PROCESSING)
                .judgmentStartedAt(OffsetDateTime.now())
                .nodeType(NodeType.NORMAL)
                .isPublic(false)
                .build();
        node.setAttributeSnapshot(attributeSnapshot);

        TimelineNode saved = timelineNodeRepository.save(node);
        log.info("待处理节点添加成功: daydreamId={}, nodeId={}", dreamId, saved.getId());

        return saved;
    }

    /**
     * 完成节点判定（更新为SUCCESS状态）
     */
    @Transactional
    public TimelineNode completeNode(UUID nodeId, String decisionSummary, String aiFeedback,
                                       String reasoningTrace, Boolean isApproved) {
        TimelineNode node = timelineNodeRepository.findById(nodeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "节点不存在"));

        node.setDecisionSummary(decisionSummary);
        node.setAiFeedback(aiFeedback);
        node.setReasoningTrace(reasoningTrace);
        node.setIsApproved(isApproved);
        node.setJudgmentStatus(JudgmentStatus.SUCCESS);

        TimelineNode saved = timelineNodeRepository.save(node);
        log.info("节点判定完成: nodeId={}, approved={}", nodeId, isApproved);

        return saved;
    }

    /**
     * 更新节点为失败状态
     */
    @Transactional
    public TimelineNode failNode(UUID nodeId, String errorMessage) {
        TimelineNode node = timelineNodeRepository.findById(nodeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "节点不存在"));

        node.setAiFeedback(errorMessage);
        node.setJudgmentStatus(JudgmentStatus.FAILED);

        TimelineNode saved = timelineNodeRepository.save(node);
        log.warn("节点判定失败: nodeId={}, error={}", nodeId, errorMessage);

        return saved;
    }

    /**
     * 回滚到指定节点
     * 删除该节点之后的所有节点，保留该节点及之前的节点
     */
    @Transactional
    public void rollbackToNode(UUID userId, UUID dreamId, UUID nodeId) {
        Daydream daydream = daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        TimelineNode targetNode = findById(nodeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "节点不存在"));

        if (!targetNode.getDreamId().equals(dreamId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "节点不属于该白日梦");
        }

        UUID branchId = targetNode.getBranchId();
        if (branchId == null) {
            branchId = daydream.getCurrentBranchId();
        }

        // 删除该节点之后的所有节点（sequenceNum > targetNode.sequenceNum）
        List<TimelineNode> nodesToDelete = timelineNodeRepository
                .findByDreamIdAndBranchIdAndSequenceNumGreaterThanOrderBySequenceNumDesc(
                        dreamId, branchId, targetNode.getSequenceNum());

        for (TimelineNode node : nodesToDelete) {
            timelineNodeRepository.delete(node);
        }

        // 更新梦境的currentDate为目标节点的日期
        daydream.setCurrentDate(targetNode.getNodeDate());

        // 如果目标节点有属性快照，回滚用户属性到该快照状态
        if (targetNode.getAttributeSnapshot() != null) {
            try {
                attributeService.restoreAttributeFromSnapshot(userId, targetNode.getAttributeSnapshot());
            } catch (Exception e) {
                log.error("回滚用户属性失败", e);
                // 不阻断回滚流程，只记录日志
            }
        }

        log.info("时间轴回滚成功: daydreamId={}, targetNodeId={}, deletedNodes={}",
                dreamId, nodeId, nodesToDelete.size());
    }
}
