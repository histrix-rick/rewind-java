package com.rewindai.system.daydream.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewindai.system.daydream.enums.AssetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 梦境资产实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dream_assets", indexes = {
        @Index(name = "idx_asset_dream_id", columnList = "dream_id"),
        @Index(name = "idx_asset_node_id", columnList = "node_id"),
        @Index(name = "idx_asset_type", columnList = "asset_type")
})
public class DreamAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dream_id", nullable = false)
    private UUID dreamId;

    @Column(name = "node_id", nullable = false)
    private UUID nodeId;

    @Column(name = "asset_type", nullable = false, length = 32)
    @Convert(converter = AssetTypeConverter.class)
    private AssetType assetType;

    @Column(name = "asset_name", nullable = false, length = 255)
    private String assetName;

    @Column(name = "asset_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal assetValue;

    @Column(name = "quantity", precision = 20, scale = 4)
    @Builder.Default
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(name = "location_province", length = 50)
    private String locationProvince;

    @Column(name = "location_city", length = 50)
    private String locationCity;

    @Column(name = "location_district", length = 50)
    private String locationDistrict;

    @Column(name = "location_address", columnDefinition = "TEXT")
    private String locationAddress;

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getMetadata() {
        try {
            if (metadataJson == null || metadataJson.isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(metadataJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public void setMetadata(Map<String, Object> metadata) {
        try {
            this.metadataJson = objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            this.metadataJson = "{}";
        }
    }

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Converter
    public static class AssetTypeConverter implements AttributeConverter<AssetType, String> {
        @Override
        public String convertToDatabaseColumn(AssetType type) {
            return type != null ? type.getCode() : AssetType.OTHER.getCode();
        }

        @Override
        public AssetType convertToEntityAttribute(String code) {
            return code != null ? AssetType.fromCode(code) : AssetType.OTHER;
        }
    }
}
