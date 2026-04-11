package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.NodeLike;
import com.rewindai.system.daydream.entity.TimelineNode;
import com.rewindai.system.daydream.repository.NodeLikeRepository;
import com.rewindai.system.daydream.repository.TimelineNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 时间轴节点点赞 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NodeLikeService {

    private final NodeLikeRepository nodeLikeRepository;
    private final TimelineNodeRepository timelineNodeRepository;

    /**
     * 检查用户是否已点赞
     */
    public boolean hasLiked(UUID nodeId, UUID userId) {
        return nodeLikeRepository.existsByNodeIdAndUserId(nodeId, userId);
    }

    /**
     * 获取点赞数
     */
    public long getLikeCount(UUID nodeId) {
        return nodeLikeRepository.countByNodeId(nodeId);
    }

    /**
     * 点赞节点
     */
    @Transactional
    public void likeNode(UUID nodeId, UUID userId) {
        TimelineNode node = timelineNodeRepository.findById(nodeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "节点不存在"));

        if (hasLiked(nodeId, userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "已经点赞过了");
        }

        NodeLike like = NodeLike.builder()
                .nodeId(nodeId)
                .dreamId(node.getDreamId())
                .userId(userId)
                .build();
        nodeLikeRepository.save(like);

        // 更新点赞数
        node.setLikeCount(node.getLikeCount() + 1);
        timelineNodeRepository.save(node);

        log.info("节点点赞成功: nodeId={}, userId={}", nodeId, userId);
    }

    /**
     * 取消点赞
     */
    @Transactional
    public void unlikeNode(UUID nodeId, UUID userId) {
        TimelineNode node = timelineNodeRepository.findById(nodeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "节点不存在"));

        if (!hasLiked(nodeId, userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "还没有点赞");
        }

        nodeLikeRepository.deleteByNodeIdAndUserId(nodeId, userId);

        // 更新点赞数
        if (node.getLikeCount() > 0) {
            node.setLikeCount(node.getLikeCount() - 1);
            timelineNodeRepository.save(node);
        }

        log.info("节点取消点赞成功: nodeId={}, userId={}", nodeId, userId);
    }
}
