package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.DreamReward;
import com.rewindai.system.daydream.repository.DreamRewardRepository;
import com.rewindai.system.notification.service.NotificationService;
import com.rewindai.system.wallet.entity.WalletTransaction;
import com.rewindai.system.wallet.enums.TransactionType;
import com.rewindai.system.wallet.service.UserWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 梦境打赏 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DreamRewardService {

    private final DreamRewardRepository dreamRewardRepository;
    private final DaydreamService daydreamService;
    private final UserWalletService userWalletService;
    private final NotificationService notificationService;

    /**
     * 打赏梦境
     */
    @Transactional
    public DreamReward rewardDream(UUID senderId, UUID dreamId, BigDecimal amount, String message) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "打赏金额必须大于0");
        }

        // 获取梦境
        Daydream dream = daydreamService.findById(dreamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "梦境不存在"));

        // 不能打赏自己的梦境
        if (dream.getUserId().equals(senderId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不能打赏自己的梦境");
        }

        // 1. 扣减打赏者余额
        WalletTransaction sendTx = userWalletService.deductCoins(
                senderId,
                amount,
                "打赏梦境「" + dream.getTitle() + "」",
                TransactionType.REWARD_SEND,
                dreamId,
                "DREAM"
        );

        // 2. 增加被打赏者余额
        WalletTransaction receiveTx = userWalletService.addCoins(
                dream.getUserId(),
                amount,
                "收到来自梦境「" + dream.getTitle() + "」的打赏",
                TransactionType.REWARD_RECEIVE,
                dreamId,
                "DREAM"
        );

        // 3. 创建打赏记录
        DreamReward reward = DreamReward.builder()
                .dreamId(dreamId)
                .senderId(senderId)
                .receiverId(dream.getUserId())
                .amount(amount)
                .message(message)
                .build();

        DreamReward savedReward = dreamRewardRepository.save(reward);

        // 4. 发送通知（异步处理，避免失败影响主流程）
        try {
            notificationService.createDreamRewardNotification(dream.getUserId(), senderId, dreamId, amount);
        } catch (Exception e) {
            log.error("发送打赏通知失败", e);
        }

        return savedReward;
    }

    /**
     * 获取梦境打赏记录
     */
    public Page<DreamReward> getDreamRewards(UUID dreamId, Pageable pageable) {
        return dreamRewardRepository.findByDreamId(dreamId, pageable);
    }

    /**
     * 获取用户收到的打赏
     */
    public Page<DreamReward> getReceivedRewards(UUID receiverId, Pageable pageable) {
        return dreamRewardRepository.findByReceiverId(receiverId, pageable);
    }

    /**
     * 获取用户发送的打赏
     */
    public Page<DreamReward> getSentRewards(UUID senderId, Pageable pageable) {
        return dreamRewardRepository.findBySenderId(senderId, pageable);
    }

    /**
     * 获取梦境总打赏金额
     */
    public BigDecimal getTotalRewardAmount(UUID dreamId) {
        return dreamRewardRepository.sumAmountByDreamId(dreamId);
    }

    /**
     * 获取用户总收到打赏金额
     */
    public BigDecimal getTotalReceivedAmount(UUID userId) {
        return dreamRewardRepository.sumAmountByReceiverId(userId);
    }

    /**
     * 获取梦境打赏次数
     */
    public long getRewardCount(UUID dreamId) {
        return dreamRewardRepository.countByDreamId(dreamId);
    }
}
