package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.DreamAsset;
import com.rewindai.system.daydream.enums.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 梦境资产响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DreamAssetResponse {

    private UUID id;
    private UUID dreamId;
    private UUID nodeId;
    private AssetType assetType;
    private String assetName;
    private BigDecimal assetValue;
    private BigDecimal quantity;
    private String locationProvince;
    private String locationCity;
    private String locationDistrict;
    private String locationAddress;
    private LocalDate acquisitionDate;
    private Map<String, Object> metadata;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static DreamAssetResponse from(DreamAsset asset) {
        return DreamAssetResponse.builder()
                .id(asset.getId())
                .dreamId(asset.getDreamId())
                .nodeId(asset.getNodeId())
                .assetType(asset.getAssetType())
                .assetName(asset.getAssetName())
                .assetValue(asset.getAssetValue())
                .quantity(asset.getQuantity())
                .locationProvince(asset.getLocationProvince())
                .locationCity(asset.getLocationCity())
                .locationDistrict(asset.getLocationDistrict())
                .locationAddress(asset.getLocationAddress())
                .acquisitionDate(asset.getAcquisitionDate())
                .metadata(asset.getMetadata())
                .isActive(asset.getIsActive())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}
