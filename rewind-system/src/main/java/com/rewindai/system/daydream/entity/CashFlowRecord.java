package com.rewindai.system.daydream.entity;

import com.rewindai.system.daydream.enums.CashTransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 现金变动记录实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cash_flow_records", indexes = {
        @Index(name = "idx_cfr_dream_id", columnList = "dream_id"),
        @Index(name = "idx_cfr_node_id", columnList = "node_id")
})
public class CashFlowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dream_id", nullable = false)
    private UUID dreamId;

    @Column(name = "node_id", nullable = false)
    private UUID nodeId;

    @Column(name = "transaction_type", nullable = false, length = 32)
    @Convert(converter = CashTransactionTypeConverter.class)
    private CashTransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_before", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "related_asset_id")
    private UUID relatedAssetId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Converter
    public static class CashTransactionTypeConverter implements AttributeConverter<CashTransactionType, String> {
        @Override
        public String convertToDatabaseColumn(CashTransactionType type) {
            return type != null ? type.getCode() : CashTransactionType.SPEND.getCode();
        }

        @Override
        public CashTransactionType convertToEntityAttribute(String code) {
            return code != null ? CashTransactionType.fromCode(code) : CashTransactionType.SPEND;
        }
    }
}
