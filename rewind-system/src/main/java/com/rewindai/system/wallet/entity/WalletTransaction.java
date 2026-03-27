package com.rewindai.system.wallet.entity;

import com.rewindai.system.wallet.enums.TransactionType;
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
 * 钱包交易记录实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallet_transactions", indexes = {
        @Index(name = "idx_tx_user_id", columnList = "user_id"),
        @Index(name = "idx_tx_type", columnList = "transaction_type"),
        @Index(name = "idx_tx_created_at", columnList = "created_at")
})
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "transaction_no", unique = true, nullable = false, length = 64)
    private String transactionNo;

    @Column(name = "transaction_type", nullable = false)
    @Convert(converter = TransactionTypeConverter.class)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_before", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "related_id")
    private UUID relatedId;

    @Column(name = "related_type", length = 50)
    private String relatedType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Converter
    public static class TransactionTypeConverter implements AttributeConverter<TransactionType, Integer> {
        @Override
        public Integer convertToDatabaseColumn(TransactionType type) {
            return type != null ? type.getCode() : TransactionType.REWARD.getCode();
        }

        @Override
        public TransactionType convertToEntityAttribute(Integer code) {
            return code != null ? TransactionType.fromCode(code) : TransactionType.REWARD;
        }
    }
}
