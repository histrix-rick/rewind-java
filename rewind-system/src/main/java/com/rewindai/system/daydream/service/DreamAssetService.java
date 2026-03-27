package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.DreamAsset;
import com.rewindai.system.daydream.enums.AssetType;
import com.rewindai.system.daydream.repository.DreamAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 梦境资产 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DreamAssetService {

    private final DreamAssetRepository dreamAssetRepository;
    private final DaydreamService daydreamService;

    public Optional<DreamAsset> findById(UUID id) {
        return dreamAssetRepository.findById(id);
    }

    public List<DreamAsset> getActiveAssetsByDream(UUID dreamId) {
        return dreamAssetRepository.findByDreamIdAndIsActiveOrderByCreatedAtAsc(dreamId, true);
    }

    public List<DreamAsset> getAssetsByDreamAndNode(UUID dreamId, UUID nodeId) {
        return dreamAssetRepository.findByDreamIdAndNodeIdOrderByCreatedAtAsc(dreamId, nodeId);
    }

    public List<DreamAsset> getAllAssetsByDream(UUID dreamId) {
        return dreamAssetRepository.findByDreamIdOrderByCreatedAtAsc(dreamId);
    }

    /**
     * 添加资产
     */
    @Transactional
    public DreamAsset addAsset(UUID userId, UUID dreamId, UUID nodeId, DreamAsset asset) {
        Daydream daydream = daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        asset.setDreamId(dreamId);
        asset.setNodeId(nodeId);
        if (asset.getQuantity() == null) {
            asset.setQuantity(BigDecimal.ONE);
        }
        if (asset.getIsActive() == null) {
            asset.setIsActive(true);
        }

        DreamAsset saved = dreamAssetRepository.save(asset);
        log.info("资产添加成功: dreamId={}, nodeId={}, assetType={}, assetName={}",
                dreamId, nodeId, asset.getAssetType(), asset.getAssetName());

        return saved;
    }

    /**
     * 更新资产
     */
    @Transactional
    public DreamAsset updateAsset(UUID userId, UUID id, DreamAsset update) {
        DreamAsset asset = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "资产不存在"));

        daydreamService.findByIdAndUserId(asset.getDreamId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        if (update.getAssetName() != null) {
            asset.setAssetName(update.getAssetName());
        }
        if (update.getAssetValue() != null) {
            asset.setAssetValue(update.getAssetValue());
        }
        if (update.getQuantity() != null) {
            asset.setQuantity(update.getQuantity());
        }
        if (update.getLocationProvince() != null) {
            asset.setLocationProvince(update.getLocationProvince());
        }
        if (update.getLocationCity() != null) {
            asset.setLocationCity(update.getLocationCity());
        }
        if (update.getLocationDistrict() != null) {
            asset.setLocationDistrict(update.getLocationDistrict());
        }
        if (update.getLocationAddress() != null) {
            asset.setLocationAddress(update.getLocationAddress());
        }
        if (update.getAcquisitionDate() != null) {
            asset.setAcquisitionDate(update.getAcquisitionDate());
        }
        if (update.getMetadataJson() != null) {
            asset.setMetadataJson(update.getMetadataJson());
        }

        return dreamAssetRepository.save(asset);
    }

    /**
     * 标记资产为非活跃
     */
    @Transactional
    public void deactivateAsset(UUID userId, UUID id) {
        DreamAsset asset = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "资产不存在"));

        daydreamService.findByIdAndUserId(asset.getDreamId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        asset.setIsActive(false);
        dreamAssetRepository.save(asset);
        log.info("资产已标记为非活跃: assetId={}", id);
    }

    /**
     * 删除资产
     */
    @Transactional
    public void deleteAsset(UUID userId, UUID id) {
        DreamAsset asset = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "资产不存在"));

        daydreamService.findByIdAndUserId(asset.getDreamId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        dreamAssetRepository.delete(asset);
        log.info("资产删除成功: assetId={}", id);
    }

    /**
     * 快速创建房产资产
     */
    @Transactional
    public DreamAsset addRealtyAsset(UUID userId, UUID dreamId, UUID nodeId,
                                       String assetName, BigDecimal assetValue,
                                       String province, String city, String district, String address,
                                       LocalDate acquisitionDate, Map<String, Object> metadata) {
        DreamAsset asset = DreamAsset.builder()
                .assetType(AssetType.REALTY)
                .assetName(assetName)
                .assetValue(assetValue)
                .quantity(BigDecimal.ONE)
                .locationProvince(province)
                .locationCity(city)
                .locationDistrict(district)
                .locationAddress(address)
                .acquisitionDate(acquisitionDate)
                .isActive(true)
                .build();
        asset.setMetadata(metadata);

        return addAsset(userId, dreamId, nodeId, asset);
    }

    @Transactional
    public void deleteByDreamIdAndNodeId(UUID dreamId, UUID nodeId) {
        dreamAssetRepository.deleteByDreamIdAndNodeId(dreamId, nodeId);
    }
}
