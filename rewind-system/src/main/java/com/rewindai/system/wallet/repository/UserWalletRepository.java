package com.rewindai.system.wallet.repository;

import com.rewindai.system.wallet.entity.UserWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM UserWallet w WHERE w.userId = :userId")
    Optional<UserWallet> findByUserIdWithLock(UUID userId);

    boolean existsByUserId(UUID userId);
}
