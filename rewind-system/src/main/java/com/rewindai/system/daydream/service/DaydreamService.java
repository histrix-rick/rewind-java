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
import com.rewindai.system.daydream.repository.DreamLikeRepository;
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
    private final DreamLikeRepository dreamLikeRepository;
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
     * 暂存白日梦（草稿）
     */
    @Transactional
    public Daydream saveDraft(UUID id, UUID userId, Daydream update) {
        return saveDraft(id, userId, update, null, null);
    }

    /**
     * 暂存白日梦（草稿，包含上下文和关系）
     */
    @Transactional
    public Daydream saveDraft(UUID id, UUID userId, Daydream update,
                              com.rewindai.system.daydream.entity.DreamContext context,
                              List<com.rewindai.system.daydream.entity.DreamRelationship> relationships) {
        if (id != null) {
            // 更新已有草稿
            Daydream daydream = findByIdAndUserId(id, userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

            if (update.getTitle() != null) {
                daydream.setTitle(update.getTitle());
            }
            if (update.getDescription() != null) {
                daydream.setDescription(update.getDescription());
            }
            if (update.getCoverUrl() != null) {
                daydream.setCoverUrl(update.getCoverUrl());
            }
            if (update.getStartDate() != null) {
                daydream.setStartDate(update.getStartDate());
            }
            if (update.getPrivacy() != null) {
                daydream.setPrivacy(update.getPrivacy());
                daydream.setIsPublic(update.getPrivacy() == DreamPrivacy.PUBLIC);
            }

            // 更新作者修改时间
            daydream.setAuthorUpdatedAt(java.time.OffsetDateTime.now());

            Daydream saved = daydreamRepository.save(daydream);

            // 更新上下文（如果有）- 草稿状态下node_id为null
            if (context != null) {
                // 删除旧的上下文
                dreamContextRepository.deleteByDreamId(id);
                // 保存新的上下文 - node_id为null（草稿状态）
                context.setDreamId(id);
                context.setNodeId(null);
                if (context.getFinancialAmount() == null) {
                    context.setFinancialAmount(java.math.BigDecimal.ZERO);
                }
                dreamContextRepository.save(context);
            }

            // 更新关系（如果有）- 草稿状态下node_id为null
            if (relationships != null) {
                // 删除旧的关系
                dreamRelationshipRepository.deleteByDreamId(id);
                // 保存新的关系 - node_id为null（草稿状态）
                for (com.rewindai.system.daydream.entity.DreamRelationship rel : relationships) {
                    rel.setDreamId(id);
                    rel.setNodeId(null);
                    if (rel.getIntimacyLevel() == null) {
                        rel.setIntimacyLevel(3);
                    }
                    dreamRelationshipRepository.save(rel);
                }
            }

            return saved;
        } else {
            // 创建新草稿
            LocalDate defaultStartDate = update.getStartDate() != null ? update.getStartDate() : LocalDate.now();
            Daydream draft = Daydream.builder()
                    .title(update.getTitle())
                    .description(update.getDescription())
                    .coverUrl(update.getCoverUrl())
                    .startDate(defaultStartDate)
                    .currentDate(defaultStartDate)
                    .status(DreamStatus.DRAFT)
                    .isActive(false)
                    .isFinished(false)
                    .userId(userId)
                    .build();

            if (update.getPrivacy() != null) {
                draft.setPrivacy(update.getPrivacy());
                draft.setIsPublic(update.getPrivacy() == DreamPrivacy.PUBLIC);
            } else {
                draft.setPrivacy(DreamPrivacy.PRIVATE);
                draft.setIsPublic(false);
            }

            draft.setViewCount(0);
            draft.setLikeCount(0);
            draft.setShareCount(0);
            draft.setCommentCount(0);
            draft.setRewardAmount(java.math.BigDecimal.ZERO);

            Daydream saved = daydreamRepository.save(draft);

            // 保存上下文（如果有）- 草稿状态下node_id为null
            if (context != null) {
                context.setDreamId(saved.getId());
                context.setNodeId(null);
                if (context.getFinancialAmount() == null) {
                    context.setFinancialAmount(java.math.BigDecimal.ZERO);
                }
                dreamContextRepository.save(context);
            }

            // 保存关系（如果有）- 草稿状态下node_id为null
            if (relationships != null && !relationships.isEmpty()) {
                for (com.rewindai.system.daydream.entity.DreamRelationship rel : relationships) {
                    rel.setDreamId(saved.getId());
                    rel.setNodeId(null);
                    if (rel.getIntimacyLevel() == null) {
                        rel.setIntimacyLevel(3);
                    }
                    dreamRelationshipRepository.save(rel);
                }
            }

            return saved;
        }
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

        // 更新作者修改时间
        daydream.setAuthorUpdatedAt(java.time.OffsetDateTime.now());

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

    /**
     * 切换点赞状态（点赞/取消点赞）
     *
     * @param dreamId 梦境ID
     * @param userId 用户ID
     * @return 点赞状态数组：[是否已点赞, 当前点赞数]
     */
    @Transactional
    public Object[] toggleLike(UUID dreamId, UUID userId) {
        boolean exists = dreamLikeRepository.existsByDreamIdAndUserId(dreamId, userId);

        if (exists) {
            // 取消点赞
            dreamLikeRepository.deleteByDreamIdAndUserId(dreamId, userId);
            decrementLikeCount(dreamId);
            long count = dreamLikeRepository.countByDreamId(dreamId);
            // 同步数据库中的likeCount
            daydreamRepository.updateLikeCount(dreamId, (int) count);
            return new Object[]{false, count};
        } else {
            // 点赞
            com.rewindai.system.daydream.entity.DreamLike dreamLike =
                    com.rewindai.system.daydream.entity.DreamLike.builder()
                            .dreamId(dreamId)
                            .userId(userId)
                            .build();
            dreamLikeRepository.save(dreamLike);
            incrementLikeCount(dreamId);
            long count = dreamLikeRepository.countByDreamId(dreamId);
            // 同步数据库中的likeCount
            daydreamRepository.updateLikeCount(dreamId, (int) count);
            return new Object[]{true, count};
        }
    }

    /**
     * 检查用户是否已点赞某个梦境
     */
    public boolean isLikedByUser(UUID dreamId, UUID userId) {
        return dreamLikeRepository.existsByDreamIdAndUserId(dreamId, userId);
    }

    /**
     * 批量获取梦境的点赞状态
     */
    public java.util.Map<UUID, Boolean> getLikeStatusBatch(java.util.List<UUID> dreamIds, UUID userId) {
        if (dreamIds == null || dreamIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        java.util.Map<UUID, Boolean> result = new java.util.HashMap<>();
        for (UUID dreamId : dreamIds) {
            result.put(dreamId, isLikedByUser(dreamId, userId));
        }
        return result;
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
        return createFullFromDraft(null, userId, daydream, userBirthDate, context, relationships);
    }

    /**
     * 从草稿完整开启白日梦（包含初始上下文和关系）
     */
    @Transactional
    public Daydream createFullFromDraft(UUID draftId, UUID userId, Daydream daydream, LocalDate userBirthDate,
                                        DreamContext context, List<DreamRelationship> relationships) {
        // 校验最多3个活跃梦境
        long activeCount = daydreamRepository.countActiveDaydreams(userId);
        if (activeCount >= MAX_ACTIVE_DAYDREAMS) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    String.format("最多只能同时运行 %d 个白日梦", MAX_ACTIVE_DAYDREAMS));
        }

        Daydream savedDaydream;

        if (draftId != null) {
            // 从草稿开启：更新现有草稿梦境
            Optional<Daydream> draftOpt = daydreamRepository.findByIdAndUserId(draftId, userId);
            if (draftOpt.isEmpty()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "草稿梦境不存在或无权限访问");
            }
            savedDaydream = draftOpt.get();

            // 更新梦境基本信息
            savedDaydream.setTitle(daydream.getTitle());
            savedDaydream.setDescription(daydream.getDescription());
            savedDaydream.setCoverUrl(daydream.getCoverUrl());
            savedDaydream.setStartDate(daydream.getStartDate());
            if (daydream.getPrivacy() != null) {
                savedDaydream.setPrivacy(daydream.getPrivacy());
                savedDaydream.setIsPublic(daydream.getPrivacy() == DreamPrivacy.PUBLIC);
            }
            // 关键：将草稿状态改为活跃状态
            savedDaydream.setStatus(com.rewindai.system.daydream.enums.DreamStatus.ACTIVE);
            savedDaydream.setIsActive(true);
            savedDaydream.setIsFinished(false);

            // 保存更新
            savedDaydream = daydreamRepository.save(savedDaydream);
            log.info("从草稿开启梦境成功: draftId={}", draftId);

            // 清理草稿的旧时间轴数据（如果有）
            timelineNodeRepository.deleteByDreamId(draftId);
            log.info("清理草稿旧时间轴数据完成: draftId={}", draftId);
        } else {
            // 创建新梦境
            savedDaydream = create(userId, daydream, userBirthDate);
        }

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

        // 3. 更新或创建梦境上下文 - 设置node_id
        if (context != null) {
            // 检查是否已有草稿上下文（node_id为null）
            List<DreamContext> existingContexts = dreamContextRepository.findByDreamIdOrderByCreatedAtDesc(savedDaydream.getId());
            if (!existingContexts.isEmpty()) {
                // 更新已有上下文的node_id
                DreamContext existingContext = existingContexts.get(0);
                existingContext.setNodeId(savedNode.getId());
                // 更新其他字段
                if (context.getIdentityId() != null) existingContext.setIdentityId(context.getIdentityId());
                if (context.getFinancialAmount() != null) existingContext.setFinancialAmount(context.getFinancialAmount());
                if (context.getEducationLevelId() != null) existingContext.setEducationLevelId(context.getEducationLevelId());
                if (context.getBirthProvince() != null) existingContext.setBirthProvince(context.getBirthProvince());
                if (context.getBirthCity() != null) existingContext.setBirthCity(context.getBirthCity());
                if (context.getBirthDistrict() != null) existingContext.setBirthDistrict(context.getBirthDistrict());
                if (context.getBirthAddress() != null) existingContext.setBirthAddress(context.getBirthAddress());
                if (context.getDreamProvince() != null) existingContext.setDreamProvince(context.getDreamProvince());
                if (context.getDreamCity() != null) existingContext.setDreamCity(context.getDreamCity());
                if (context.getDreamDistrict() != null) existingContext.setDreamDistrict(context.getDreamDistrict());
                if (context.getDreamAddress() != null) existingContext.setDreamAddress(context.getDreamAddress());
                dreamContextRepository.save(existingContext);
                log.info("梦境上下文更新成功: dreamId={}", savedDaydream.getId());
            } else {
                // 创建新的上下文
                context.setDreamId(savedDaydream.getId());
                context.setNodeId(savedNode.getId());
                if (context.getFinancialAmount() == null) {
                    context.setFinancialAmount(BigDecimal.ZERO);
                }
                dreamContextRepository.save(context);
                log.info("梦境上下文创建成功: dreamId={}", savedDaydream.getId());
            }
        }

        // 4. 更新或创建社会关系 - 设置node_id
        if (relationships != null && !relationships.isEmpty()) {
            // 检查是否已有草稿关系（node_id为null）
            List<DreamRelationship> existingRelationships = dreamRelationshipRepository.findByDreamIdOrderByCreatedAtAsc(savedDaydream.getId());
            if (!existingRelationships.isEmpty()) {
                // 删除旧关系，用新的替换
                dreamRelationshipRepository.deleteByDreamId(savedDaydream.getId());
            }
            // 创建新关系（或替换旧关系）
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
