package com.rewindai.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 房价估算响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyPriceEstimateResponse {

    private BigDecimal averagePricePerSqm;
    private BigDecimal estimatedArea;
    private Integer estimatedUnitCount;
    private String locationDescription;
    private String year;
    private String dataSource;
}
