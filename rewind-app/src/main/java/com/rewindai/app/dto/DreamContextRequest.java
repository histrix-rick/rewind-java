package com.rewindai.app.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 梦境上下文请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
public class DreamContextRequest {

    private Long identityId;

    private BigDecimal financialAmount;

    private Long educationLevelId;

    @Size(max = 50)
    private String birthProvince;

    @Size(max = 50)
    private String birthCity;

    @Size(max = 50)
    private String birthDistrict;

    private String birthAddress;

    @Size(max = 50)
    private String dreamProvince;

    @Size(max = 50)
    private String dreamCity;

    @Size(max = 50)
    private String dreamDistrict;

    private String dreamAddress;
}
