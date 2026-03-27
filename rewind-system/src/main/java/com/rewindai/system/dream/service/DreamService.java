package com.rewindai.system.dream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.dream.entity.Dream;
import com.rewindai.system.dream.enums.DreamPrivacy;
import com.rewindai.system.dream.enums.DreamStatus;
import com.rewindai.system.dream.repository.DreamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 梦境 Service
 *
 * @author Rewind.ai Team
 */
@Service
@RequiredArgsConstructor
public class DreamService {

    private final DreamRepository dreamRepository;

    public Optional<Dream> findById(UUID id) {
        return dreamRepository.findById(id);
    }

    public Optional<Dream> findByIdAndUserId(UUID id, UUID userId) {
        return dreamRepository.findByIdAndUserId(id, userId);
    }

    /**
     * 创建梦境
     */
    @Transactional
    public Dream create(Dream dream) {
        if (dream.getDreamDate() == null) {
            dream.setDreamDate(OffsetDateTime.now());
        }
        return dreamRepository.save(dream);
    }

    /**
     * 更新梦境
     */
    @Transactional
    public Dream update(UUID id, UUID userId, Dream dreamUpdate) {
        Dream dream = findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (dream.getStatus() == DreamStatus.DELETED) {
            throw new BusinessException(ErrorCode.DREAM_ALREADY_DELETED);
        }

        if (dreamUpdate.getTitle() != null) {
            dream.setTitle(dreamUpdate.getTitle());
        }
        if (dreamUpdate.getContent() != null) {
            dream.setContent(dreamUpdate.getContent());
        }
        if (dreamUpdate.getCoverUrl() != null) {
            dream.setCoverUrl(dreamUpdate.getCoverUrl());
        }
        if (dreamUpdate.getDreamDate() != null) {
            dream.setDreamDate(dreamUpdate.getDreamDate());
        }
        if (dreamUpdate.getStatus() != null) {
            dream.setStatus(dreamUpdate.getStatus());
        }
        if (dreamUpdate.getPrivacy() != null) {
            dream.setPrivacy(dreamUpdate.getPrivacy());
            dream.setIsPublic(dreamUpdate.getPrivacy() == DreamPrivacy.PUBLIC);
        }
        if (dreamUpdate.getTags() != null) {
            dream.setTags(dreamUpdate.getTags());
        }
        if (dreamUpdate.getMood() != null) {
            dream.setMood(dreamUpdate.getMood());
        }
        if (dreamUpdate.getWeather() != null) {
            dream.setWeather(dreamUpdate.getWeather());
        }
        if (dreamUpdate.getDurationMinutes() != null) {
            dream.setDurationMinutes(dreamUpdate.getDurationMinutes());
        }
        if (dreamUpdate.getIsLucid() != null) {
            dream.setIsLucid(dreamUpdate.getIsLucid());
        }
        if (dreamUpdate.getIsRecurring() != null) {
            dream.setIsRecurring(dreamUpdate.getIsRecurring());
        }
        if (dreamUpdate.getIsNightmare() != null) {
            dream.setIsNightmare(dreamUpdate.getIsNightmare());
        }

        return dreamRepository.save(dream);
    }

    /**
     * 公开分享梦境
     */
    @Transactional
    public Dream publish(UUID id, UUID userId) {
        Dream dream = findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        dream.setPrivacy(DreamPrivacy.PUBLIC);
        dream.setIsPublic(true);
        dream.setStatus(DreamStatus.ACTIVE);

        return dreamRepository.save(dream);
    }

    /**
     * 取消公开
     */
    @Transactional
    public Dream unpublish(UUID id, UUID userId) {
        Dream dream = findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        dream.setPrivacy(DreamPrivacy.PRIVATE);
        dream.setIsPublic(false);

        return dreamRepository.save(dream);
    }

    /**
     * 删除梦境（软删除）
     */
    @Transactional
    public void delete(UUID id, UUID userId) {
        Dream dream = findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        dream.setStatus(DreamStatus.DELETED);
        dream.setDeletedAt(OffsetDateTime.now());

        dreamRepository.save(dream);
    }

    /**
     * 获取用户梦境分页
     */
    public Page<Dream> getUserDreams(UUID userId, Pageable pageable) {
        return dreamRepository.findByUserId(userId, pageable);
    }

    /**
     * 获取用户指定状态的梦境
     */
    public Page<Dream> getUserDreamsByStatus(UUID userId, DreamStatus status, Pageable pageable) {
        return dreamRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    /**
     * 获取用户最近的梦境
     */
    public List<Dream> getUserRecentDreams(UUID userId, DreamStatus status) {
        return dreamRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
    }

    /**
     * 获取公开梦境分页
     */
    public Page<Dream> getPublicDreams(Pageable pageable) {
        return dreamRepository.findByIsPublicTrueAndStatusOrderByCreatedAtDesc(DreamStatus.ACTIVE, pageable);
    }

    /**
     * 搜索公开梦境
     */
    public Page<Dream> searchPublicDreams(String keyword, Pageable pageable) {
        return dreamRepository.searchPublicDreams(keyword, DreamStatus.ACTIVE, pageable);
    }

    /**
     * 增加浏览量
     */
    @Transactional
    public void incrementViewCount(UUID id) {
        dreamRepository.incrementViewCount(id);
    }

    /**
     * 增加点赞数
     */
    @Transactional
    public void incrementLikeCount(UUID id) {
        dreamRepository.incrementLikeCount(id);
    }

    /**
     * 减少点赞数
     */
    @Transactional
    public void decrementLikeCount(UUID id) {
        dreamRepository.decrementLikeCount(id);
    }

    /**
     * 增加分享数
     */
    @Transactional
    public void incrementShareCount(UUID id) {
        dreamRepository.incrementShareCount(id);
    }

    /**
     * 增加评论数
     */
    @Transactional
    public void incrementCommentCount(UUID id) {
        dreamRepository.incrementCommentCount(id);
    }

    /**
     * 减少评论数
     */
    @Transactional
    public void decrementCommentCount(UUID id) {
        dreamRepository.decrementCommentCount(id);
    }

    public long countByUserId(UUID userId) {
        return dreamRepository.countByUserId(userId);
    }

    public long countByUserIdAndStatus(UUID userId, DreamStatus status) {
        return dreamRepository.countByUserIdAndStatus(userId, status);
    }
}
