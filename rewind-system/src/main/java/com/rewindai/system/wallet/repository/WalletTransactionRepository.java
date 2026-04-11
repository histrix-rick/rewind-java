package com.rewindai.system.wallet.repository;

import com.rewindai.system.wallet.entity.WalletTransaction;
import com.rewindai.system.wallet.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 钱包交易记录 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {

    Optional<WalletTransaction> findByTransactionNo(String transactionNo);

    Page<WalletTransaction> findByUserId(UUID userId, Pageable pageable);

    List<WalletTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Page<WalletTransaction> findByUserIdAndTransactionType(UUID userId, TransactionType type, Pageable pageable);

    boolean existsByTransactionNo(String transactionNo);

    /**
     * 后台管理：分页查询所有交易记录
     */
    @Override
    Page<WalletTransaction> findAll(Pageable pageable);

    /**
     * 后台管理：查询所有交易记录（用于统计）
     */
    @Override
    List<WalletTransaction> findAll(Sort sort);

    /**
     * 后台管理：按类型查询交易记录
     */
    Page<WalletTransaction> findByTransactionType(TransactionType type, Pageable pageable);
}
