package com.rewindai.app.dto;

import com.rewindai.system.daydream.enums.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 梦境资产请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DreamAssetRequest {

    @NotNull(message = "资产类型不能为空")
    private AssetType assetType;

    @NotBlank(message = "资产名称不能为空")
    private String assetName;

    @NotNull(message = "资产价值不能为空")
    private BigDecimal assetValue;

    private BigDecimal quantity;

    private String locationProvince;

    private String locationCity;

    private String locationDistrict;

    private String locationAddress;

    private LocalDate acquisitionDate;

    private Map<String, Object> metadata;
}
