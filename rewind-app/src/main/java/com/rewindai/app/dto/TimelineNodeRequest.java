package com.rewindai.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 添加时间轴节点请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
public class TimelineNodeRequest {

    @NotBlank(message = "决策内容不能为空")
    private String userDecision;

    private String decisionSummary;

    private UUID branchId;

    private LocalDate nodeDate;
}
