package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.CashFlowRecord;
import com.rewindai.system.daydream.enums.CashTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 现金变动记录响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowRecordResponse {

    private UUID id;
    private UUID dreamId;
    private UUID nodeId;
    private CashTransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private UUID relatedAssetId;
    private OffsetDateTime createdAt;

    public static CashFlowRecordResponse from(CashFlowRecord record) {
        return CashFlowRecordResponse.builder()
                .id(record.getId())
                .dreamId(record.getDreamId())
                .nodeId(record.getNodeId())
                .transactionType(record.getTransactionType())
                .amount(record.getAmount())
                .balanceBefore(record.getBalanceBefore())
                .balanceAfter(record.getBalanceAfter())
                .description(record.getDescription())
                .relatedAssetId(record.getRelatedAssetId())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
