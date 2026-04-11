package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.DreamBranch;
import com.rewindai.system.daydream.entity.DreamContext;
import com.rewindai.system.daydream.entity.DreamRelationship;
import com.rewindai.system.daydream.entity.TimelineNode;
import com.rewindai.system.daydream.enums.NodeType;
import com.rewindai.system.daydream.repository.DaydreamRepository;
import com.rewindai.system.daydream.repository.DreamBranchRepository;
import com.rewindai.system.daydream.repository.DreamContextRepository;
import com.rewindai.system.daydream.repository.DreamRelationshipRepository;
import com.rewindai.system.daydream.repository.TimelineNodeRepository;
import com.rewindai.system.daydream.enums.DreamPrivacy;
import com.rewindai.system.daydream.enums.DreamStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 白日梦 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DaydreamService {

    private static final int MAX_ACTIVE_DAYDREAMS = 3;

    private final DaydreamRepository daydreamRepository;
    private final DreamBranchRepository dreamBranchRepository;
    private final TimelineNodeRepository timelineNodeRepository;
    private final DreamContextRepository dreamContextRepository;
    private final DreamRelationshipRepository dreamRelationshipRepository;
    private final DreamFollowService dreamFollowService;

    public Optional<Daydream> findById(UUID id) {
        return daydreamRepository.findById(id);
    }

    public Optional<Daydream> findByIdAndUserId(UUID id, UUID userId) {
        return daydreamRepository.findByIdAndUserId(id, userId);
    }

    /**
     * 获取可访问的白日梦（公开梦境任何人可访问，私有梦境仅所有者可访问）
     *
     * @param id 白日梦ID
     * @param userId 当前用户ID
     * @return 可访问的白日梦
     */
    public Optional<Daydream> findAccessibleById(UUID id, UUID userId) {
        Optional<Daydream> daydreamOpt = daydreamRepository.findById(id);
        if (daydreamOpt.isEmpty()) {
            return Optional.empty();
        }
        Daydream daydream = daydreamOpt.get();
        // 如果是公开梦境，或者是所有者，则可以访问
        if (Boolean.TRUE.equals(daydream.getIsPublic()) || daydream.getUserId().equals(userId)) {
            return daydreamOpt;
        }
        return Optional.empty();
    }

    /**
     * 检查用户是否可以修改该白日梦（仅所有者可修改）
     *
     * @param id 白日梦ID
     * @param userId 当前用户ID
     * @return 可修改的白日梦
     */
    public Optional<Daydream> findModifiableById(UUID id, UUID userId) {
        return daydreamRepository.findByIdAndUserId(id, userId);
    }

    /**
     * 创建白日梦
     */
    @Transactional
    public Daydream create(UUID userId, Daydream daydream, LocalDate userBirthDate) {
        long activeCount = daydreamRepository.countActiveDaydreams(userId);
        if (activeCount >= MAX_ACTIVE_DAYDREAMS) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    String.format("最多只能同时运行 %d 个白日梦", MAX_ACTIVE_DAYDREAMS));
        }

        if (daydream.getStartDate().isBefore(userBirthDate)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "起始时间不能早于出生日期");
        }

        if (daydream.getCurrentDate() == null) {
            daydream.setCurrentDate(daydream.getStartDate());
        }

        daydream.setUserId(userId);
        daydream.setIsActive(true);
        daydream.setIsFinished(false);
        daydream.setStatus(DreamStatus.ACTIVE);
        // 只有在用户没有设置隐私状态时才设置默认值
        if (daydream.getPrivacy() == null) {
            daydream.setPrivacy(DreamPrivacy.PRIVATE);
            daydream.setIsPublic(false);
        } else {
            daydream.setIsPublic(daydream.getPrivacy() == DreamPrivacy.PUBLIC);
        }
        // 确保isPublic和privacy保持一致
        if (daydream.getIsPublic() == null) {
            daydream.setIsPublic(daydream.getPrivacy() == DreamPrivacy.PUBLIC);
        }
        daydream.setViewCount(0);
        daydream.setLikeCount(0);
        daydream.setShareCount(0);
        daydream.setCommentCount(0);
        daydream.setRewardAmount(BigDecimal.ZERO);

        Daydream saved = daydreamRepository.save(daydream);
        log.info("白日梦创建成功: userId={}, daydreamId={}", userId, saved.getId());

        return saved;
    }

    /**
     * 更新白日梦基本信息
     */
    @Transactional
    public Daydream update(UUID id, UUID userId, Daydream update) {
        Daydream daydream = findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (daydream.getIsFinished()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "已结束的白日梦无法修改");
        }

        if (update.getTitle() != null) {
            daydream.setTitle(update.getTitle());
        }
        if (update.getDescription() != null) {
            daydream.setDescription(update.getDescription());
        }
        if (update.getCoverUrl() != null) {
            daydream.setCoverUrl(update.getCoverUrl());
        }
        if (update.getStatus() != null) {
            daydream.setStatus(update.getStatus());
        }
        if (update.getPrivacy() != null) {
            daydream.setPrivacy(update.getPrivacy());
            daydream.setIsPublic(update.getPrivacy() == DreamPrivacy.PUBLIC);
        }

        return daydreamRepository.save(daydream);
    }

    /**
     * 更新白日梦当前时间
     */
    @Transactional
    public Daydream updateCurrentDate(UUID id, UUID userId, LocalDate currentDate) {
        Daydream daydream = findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (currentDate.isBefore(daydream.getStartDate())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "当前时间不能早于起始时间");
        }

        daydream.setCurrentDate(currentDate);

        if (currentDate.isEqual(LocalDate.now())) {
            daydream.setIsFinished(true);
            daydream.setStatus(DreamStatus.COMPLETED);
        }

        return daydreamRepository.save(daydream);
    }

    /**
     * 计算进度百分比
     */
    public BigDecimal calculateProgress(Daydream daydream) {
        LocalDate start = daydream.getStartDate();
        LocalDate current = daydream.getCurrentDate();
        LocalDate now = LocalDate.now();

        if (current.isBefore(start)) {
            return BigDecimal.ZERO;
        }
        if (current.isEqual(now) || current.isAfter(now)) {
            return new BigDecimal("100.00");
        }

        long totalDays = ChronoUnit.DAYS.between(start, now);
        long elapsedDays = ChronoUnit.DAYS.between(start, current);

        if (totalDays <= 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(elapsedDays * 100.0 / totalDays)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 公开分享白日梦
     */
    @Transactional
    public Daydream publish(UUID id, UUID userId) {
        Daydream daydream = findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        daydream.setPrivacy(DreamPrivacy.PUBLIC);
        daydream.setIsPublic(true);

        return daydreamRepository.save(daydream);
    }

    /**
     * 取消公开
     */
    @Transactional
    public Daydream unpublish(UUID id, UUID userId) {
        Daydream daydream = findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        daydream.setPrivacy(DreamPrivacy.PRIVATE);
        daydream.setIsPublic(false);

        return daydreamRepository.save(daydream);
    }

    /**
     * 结束白日梦
     */
    @Transactional
    public Daydream finish(UUID id, UUID userId) {
        Daydream daydream = findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (daydream.getIsFinished()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "该白日梦已结束");
        }

        daydream.setIsFinished(true);
        daydream.setIsActive(false);
        daydream.setStatus(DreamStatus.COMPLETED);

        Daydream saved = daydreamRepository.save(daydream);
        log.info("白日梦已结束: userId={}, daydreamId={}", userId, id);
        return saved;
    }

    /**
     * 归档白日梦（软删除）
     */
    @Transactional
    public void archive(UUID id, UUID userId) {
        Daydream daydream = findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        daydream.setIsActive(false);
        daydream.setStatus(DreamStatus.ARCHIVED);
        daydream.setDeletedAt(java.time.OffsetDateTime.now());

        daydreamRepository.save(daydream);
        log.info("白日梦已归档: userId={}, daydreamId={}", userId, id);
    }

    /**
     * 恢复已归档的白日梦
     */
    @Transactional
    public Daydream restore(UUID id, UUID userId) {
        Daydream daydream = daydreamRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        // 验证权限
        if (!daydream.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "无权操作此梦境");
        }

        daydream.setIsActive(true);
        daydream.setStatus(DreamStatus.ACTIVE);
        daydream.setDeletedAt(null);

        Daydream saved = daydreamRepository.save(daydream);
        log.info("白日梦已恢复: userId={}, daydreamId={}", userId, id);
        return saved;
    }

    /**
     * 永久删除白日梦（不可恢复）
     */
    @Transactional
    public void permanentDelete(UUID id, UUID userId) {
        Daydream daydream = daydreamRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        // 验证权限
        if (!daydream.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "无权操作此梦境");
        }

        // 验证必须是已归档状态
        if (daydream.getDeletedAt() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "只能永久删除已归档的梦境");
        }

        // 解除该梦境的所有关注
        dreamFollowService.unfollowAllForDream(id);

        daydreamRepository.delete(daydream);
        log.info("白日梦已永久删除: userId={}, daydreamId={}", userId, id);
    }

    /**
     * 获取用户的归档梦境列表
     */
    public Page<Daydream> getArchivedDaydreams(UUID userId, Pageable pageable) {
        return daydreamRepository.findArchivedByUserId(userId, pageable);
    }

    public List<Daydream> getActiveDaydreams(UUID userId) {
        return daydreamRepository.findByUserIdAndIsActiveTrueAndIsFinishedFalse(userId);
    }

    public Page<Daydream> getUserDaydreams(UUID userId, Pageable pageable) {
        return daydreamRepository.findByUserIdExcludeArchived(userId, pageable);
    }

    public Page<Daydream> getUserDaydreamsByStatus(UUID userId, DreamStatus status, Pageable pageable) {
        return daydreamRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    public Page<Daydream> getPublicDaydreams(Pageable pageable) {
        return daydreamRepository.findByIsPublicTrueAndStatusOrderByCreatedAtDesc(DreamStatus.ACTIVE, pageable);
    }

    public Page<Daydream> searchPublicDaydreams(String keyword, Pageable pageable) {
        return daydreamRepository.searchPublicDaydreams(keyword, DreamStatus.ACTIVE, pageable);
    }

    /**
     * 获取指定用户的公开梦境列表
     */
    public Page<Daydream> getPublicDaydreamsByUserId(UUID userId, Pageable pageable) {
        return daydreamRepository.findPublicByUserId(userId, pageable);
    }

    @Transactional
    public void incrementViewCount(UUID id) {
        daydreamRepository.incrementViewCount(id);
    }

    @Transactional
    public void incrementLikeCount(UUID id) {
        daydreamRepository.incrementLikeCount(id);
    }

    @Transactional
    public void decrementLikeCount(UUID id) {
        daydreamRepository.decrementLikeCount(id);
    }

    @Transactional
    public void incrementShareCount(UUID id) {
        daydreamRepository.incrementShareCount(id);
    }

    public List<DreamBranch> getBranches(UUID daydreamId) {
        return dreamBranchRepository.findByDreamId(daydreamId);
    }

    /**
     * 保存白日梦
     */
    @Transactional
    public Daydream save(Daydream daydream) {
        return daydreamRepository.save(daydream);
    }

    /**
     * 完整创建白日梦（包含初始上下文和关系）
     */
    @Transactional
    public Daydream createFull(UUID userId, Daydream daydream, LocalDate userBirthDate,
                                DreamContext context, List<DreamRelationship> relationships) {
        // 1. 创建白日梦
        Daydream savedDaydream = create(userId, daydream, userBirthDate);

        // 2. 创建初始时间轴节点
        TimelineNode initialNode = TimelineNode.builder()
                .dreamId(savedDaydream.getId())
                .sequenceNum(1)
                .nodeDate(savedDaydream.getStartDate())
                .userDecision("开启梦境")
                .decisionSummary("初始节点")
                .aiFeedback("欢迎来到你的白日梦！")
                .isApproved(true)
                .nodeType(NodeType.NORMAL)
                .isPublic(false)
                .build();
        TimelineNode savedNode = timelineNodeRepository.save(initialNode);
        log.info("初始时间轴节点创建成功: dreamId={}, nodeId={}", savedDaydream.getId(), savedNode.getId());

        // 3. 创建梦境上下文
        if (context != null) {
            context.setDreamId(savedDaydream.getId());
            context.setNodeId(savedNode.getId());
            if (context.getFinancialAmount() == null) {
                context.setFinancialAmount(BigDecimal.ZERO);
            }
            dreamContextRepository.save(context);
            log.info("梦境上下文创建成功: dreamId={}", savedDaydream.getId());
        }

        // 4. 创建社会关系
        if (relationships != null && !relationships.isEmpty()) {
            for (DreamRelationship rel : relationships) {
                rel.setDreamId(savedDaydream.getId());
                rel.setNodeId(savedNode.getId());
                if (rel.getIntimacyLevel() == null) {
                    rel.setIntimacyLevel(3);
                }
                dreamRelationshipRepository.save(rel);
            }
            log.info("社会关系创建成功: dreamId={}, count={}", savedDaydream.getId(), relationships.size());
        }

        return savedDaydream;
    }
}
