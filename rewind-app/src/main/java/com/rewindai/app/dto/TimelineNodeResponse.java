package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.TimelineNode;
import com.rewindai.system.daydream.enums.JudgmentStatus;
import com.rewindai.system.daydream.enums.NodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 时间轴节点响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineNodeResponse {

    private UUID id;
    private UUID dreamId;
    private UUID branchId;
    private Integer sequenceNum;
    private LocalDate nodeDate;
    private String userDecision;
    private String decisionSummary;
    private String aiFeedback;
    private String reasoningTrace;
    private Boolean isApproved;
    private JudgmentStatus judgmentStatus;
    private Map<String, Object> attributeSnapshot;
    private NodeType nodeType;
    private Boolean isPublic;
    private Integer likeCount;
    private OffsetDateTime createdAt;

    public static TimelineNodeResponse from(TimelineNode node) {
        return TimelineNodeResponse.builder()
                .id(node.getId())
                .dreamId(node.getDreamId())
                .branchId(node.getBranchId())
                .sequenceNum(node.getSequenceNum())
                .nodeDate(node.getNodeDate())
                .userDecision(node.getUserDecision())
                .decisionSummary(node.getDecisionSummary())
                .aiFeedback(node.getAiFeedback())
                .reasoningTrace(node.getReasoningTrace())
                .isApproved(node.getIsApproved())
                .judgmentStatus(node.getJudgmentStatus())
                .attributeSnapshot(node.getAttributeSnapshot())
                .nodeType(node.getNodeType())
                .isPublic(node.getIsPublic())
                .likeCount(node.getLikeCount())
                .createdAt(node.getCreatedAt())
                .build();
    }
}
