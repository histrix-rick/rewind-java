package com.rewindai.system.wallet.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.wallet.entity.UserWallet;
import com.rewindai.system.wallet.entity.WalletTransaction;
import com.rewindai.system.wallet.enums.TransactionType;
import com.rewindai.system.wallet.repository.UserWalletRepository;
import com.rewindai.system.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户钱包 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserWalletService {

    private final UserWalletRepository userWalletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    /**
     * 获取用户钱包，不存在则创建
     */
    public UserWallet getOrCreateWallet(UUID userId) {
        return userWalletRepository.findByUserId(userId)
                .orElseGet(() -> createWallet(userId));
    }

    /**
     * 创建用户钱包
     */
    @Transactional
    public UserWallet createWallet(UUID userId) {
        if (userWalletRepository.existsByUserId(userId)) {
            return userWalletRepository.findByUserId(userId).orElseThrow();
        }
        UserWallet wallet = UserWallet.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .totalEarned(BigDecimal.ZERO)
                .totalSpent(BigDecimal.ZERO)
                .frozenAmount(BigDecimal.ZERO)
                .build();
        return userWalletRepository.save(wallet);
    }

    public Optional<UserWallet> findByUserId(UUID userId) {
        return userWalletRepository.findByUserId(userId);
    }

    /**
     * 增加梦想币
     */
    @Transactional
    public WalletTransaction addCoins(UUID userId, BigDecimal amount,
                                        String description, TransactionType type,
                                        UUID relatedId, String relatedType) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "金额必须大于0");
        }

        UserWallet wallet = userWalletRepository.findByUserIdWithLock(userId)
                .orElseGet(() -> createWallet(userId));

        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);

        wallet.setBalance(balanceAfter);
        wallet.setTotalEarned(wallet.getTotalEarned().add(amount));

        userWalletRepository.save(wallet);

        return createTransaction(userId, generateTransactionNo(), type, amount,
                balanceBefore, balanceAfter, description, relatedId, relatedType);
    }

    /**
     * 扣减梦想币
     */
    @Transactional
    public WalletTransaction deductCoins(UUID userId, BigDecimal amount,
                                          String description, TransactionType type,
                                          UUID relatedId, String relatedType) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "金额必须大于0");
        }

        UserWallet wallet = userWalletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));

        BigDecimal balanceBefore = wallet.getBalance();
        if (balanceBefore.compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        BigDecimal balanceAfter = balanceBefore.subtract(amount);

        wallet.setBalance(balanceAfter);
        wallet.setTotalSpent(wallet.getTotalSpent().add(amount));

        userWalletRepository.save(wallet);

        return createTransaction(userId, generateTransactionNo(), type, amount,
                balanceBefore, balanceAfter, description, relatedId, relatedType);
    }

    /**
     * 转账
     */
    @Transactional
    public void transfer(UUID fromUserId, UUID toUserId, BigDecimal amount, String description) {
        if (fromUserId.equals(toUserId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不能给自己转账");
        }

        // 先扣减
        deductCoins(fromUserId, amount, description, TransactionType.TRANSFER_OUT, null, "TRANSFER");
        // 再增加
        addCoins(toUserId, amount, description, TransactionType.TRANSFER_IN, null, "TRANSFER");
    }

    /**
     * 管理员发放梦想币
     */
    @Transactional
    public WalletTransaction adminGrant(UUID userId, BigDecimal amount, String description, UUID adminId) {
        return addCoins(userId, amount, description, TransactionType.ADMIN_GRANT, adminId, "ADMIN");
    }

    /**
     * 管理员扣除梦想币
     */
    @Transactional
    public WalletTransaction adminDeduct(UUID userId, BigDecimal amount, String description, UUID adminId) {
        return deductCoins(userId, amount, description, TransactionType.ADMIN_DEDUCT, adminId, "ADMIN");
    }

    /**
     * 获取交易记录分页
     */
    public Page<WalletTransaction> getTransactions(UUID userId, Pageable pageable) {
        return walletTransactionRepository.findByUserId(userId, pageable);
    }

    /**
     * 获取指定类型的交易记录
     */
    public Page<WalletTransaction> getTransactionsByType(UUID userId, TransactionType type, Pageable pageable) {
        return walletTransactionRepository.findByUserIdAndTransactionType(userId, type, pageable);
    }

    /**
     * 获取最近交易记录
     */
    public List<WalletTransaction> getRecentTransactions(UUID userId) {
        return walletTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    private WalletTransaction createTransaction(UUID userId, String transactionNo,
                                                  TransactionType type, BigDecimal amount,
                                                  BigDecimal balanceBefore, BigDecimal balanceAfter,
                                                  String description, UUID relatedId, String relatedType) {
        WalletTransaction transaction = WalletTransaction.builder()
                .userId(userId)
                .transactionNo(transactionNo)
                .transactionType(type)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .description(description)
                .relatedId(relatedId)
                .relatedType(relatedType)
                .build();
        return walletTransactionRepository.save(transaction);
    }

    private String generateTransactionNo() {
        return "TX" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
