package com.rewindai.system.daydream.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewindai.system.daydream.enums.JudgmentStatus;
import com.rewindai.system.daydream.enums.NodeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 时间轴节点表实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dream_timeline_nodes", indexes = {
        @Index(name = "idx_tn_dream_id", columnList = "dream_id"),
        @Index(name = "idx_tn_branch", columnList = "dream_id, branch_id"),
        @Index(name = "idx_tn_sequence", columnList = "dream_id, branch_id, sequence_num")
})
public class TimelineNode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dream_id", nullable = false)
    private UUID dreamId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "sequence_num", nullable = false)
    private Integer sequenceNum;

    @Column(name = "node_date", nullable = false)
    private LocalDate nodeDate;

    @Column(name = "user_decision", columnDefinition = "TEXT")
    private String userDecision;

    @Column(name = "decision_summary", length = 500)
    private String decisionSummary;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "reasoning_trace", columnDefinition = "TEXT")
    private String reasoningTrace;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "judgment_status", length = 32)
    @Convert(converter = JudgmentStatusConverter.class)
    @Builder.Default
    private JudgmentStatus judgmentStatus = JudgmentStatus.SUCCESS;

    @Column(name = "judgment_started_at")
    private OffsetDateTime judgmentStartedAt;

    @Column(name = "attribute_snapshot", columnDefinition = "TEXT")
    private String attributeSnapshotJson;

    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getAttributeSnapshot() {
        try {
            if (attributeSnapshotJson == null || attributeSnapshotJson.isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(attributeSnapshotJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public void setAttributeSnapshot(Map<String, Object> snapshot) {
        try {
            this.attributeSnapshotJson = objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            this.attributeSnapshotJson = "{}";
        }
    }

    @Column(name = "node_type", nullable = false, length = 20)
    @Convert(converter = NodeTypeConverter.class)
    @Builder.Default
    private NodeType nodeType = NodeType.NORMAL;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Converter
    public static class NodeTypeConverter implements AttributeConverter<NodeType, String> {
        @Override
        public String convertToDatabaseColumn(NodeType type) {
            return type != null ? type.name() : NodeType.NORMAL.name();
        }

        @Override
        public NodeType convertToEntityAttribute(String name) {
            return name != null ? NodeType.valueOf(name) : NodeType.NORMAL;
        }
    }

    @Converter
    public static class JudgmentStatusConverter implements AttributeConverter<JudgmentStatus, String> {
        @Override
        public String convertToDatabaseColumn(JudgmentStatus status) {
            return status != null ? status.getCode() : JudgmentStatus.PENDING.getCode();
        }

        @Override
        public JudgmentStatus convertToEntityAttribute(String code) {
            return code != null ? JudgmentStatus.fromCode(code) : JudgmentStatus.PENDING;
        }
    }
}
