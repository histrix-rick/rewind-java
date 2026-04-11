package com.rewindai.system.wallet.repository;

import com.rewindai.system.wallet.entity.UserWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户钱包 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface UserWalletRepository extends JpaRepository<UserWallet, UUID> {

    Optional<UserWallet> findByUserId(UUID userId);

    Page<UserWallet> findByUserId(UUID userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM UserWallet w WHERE w.userId = :userId")
    Optional<UserWallet> findByUserIdWithLock(UUID userId);

    boolean existsByUserId(UUID userId);

    // ========== 后台管理查询方法 ==========

    @Query("SELECT COALESCE(SUM(w.balance), 0) FROM UserWallet w")
    BigDecimal sumAllBalance();

    @Query("SELECT COALESCE(SUM(w.totalEarned), 0) FROM UserWallet w")
    BigDecimal sumAllEarned();

    @Query("SELECT COALESCE(SUM(w.totalSpent), 0) FROM UserWallet w")
    BigDecimal sumAllSpent();
}
