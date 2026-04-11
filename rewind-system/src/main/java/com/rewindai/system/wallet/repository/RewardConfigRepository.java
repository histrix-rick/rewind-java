package com.rewindai.system.wallet.repository;

import com.rewindai.system.wallet.entity.RewardConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 奖励配置 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface RewardConfigRepository extends JpaRepository<RewardConfig, Long> {

    Optional<RewardConfig> findByRewardType(String rewardType);

    List<RewardConfig> findByIsActiveTrueOrderBySortOrderAsc();

    Page<RewardConfig> findByRewardTypeContaining(String rewardType, Pageable pageable);

    Page<RewardConfig> findByIsActive(Boolean isActive, Pageable pageable);
}
