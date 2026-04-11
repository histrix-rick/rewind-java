package com.rewindai.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 后台管理 - 财务报表响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "财务报表响应")
public class FinancialReportResponse {

    @Schema(description = "总体统计")
    private OverallStats overallStats;

    @Schema(description = "收入趋势")
    private TrendData incomeTrend;

    @Schema(description = "支出趋势")
    private TrendData expenseTrend;

    @Schema(description = "交易类型统计")
    private List<TypeStats> typeStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "总体统计")
    public static class OverallStats {
        @Schema(description = "总流水")
        private BigDecimal totalTransactionAmount;

        @Schema(description = "总收入")
        private BigDecimal totalIncome;

        @Schema(description = "总支出")
        private BigDecimal totalExpense;

        @Schema(description = "总交易笔数")
        private Long totalTransactionCount;

        @Schema(description = "今日收入")
        private BigDecimal todayIncome;

        @Schema(description = "今日支出")
        private BigDecimal todayExpense;

        @Schema(description = "今日交易笔数")
        private Long todayTransactionCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "趋势数据")
    public static class TrendData {
        @Schema(description = "日期列表")
        private List<String> dates;

        @Schema(description = "金额列表")
        private List<BigDecimal> amounts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "类型统计")
    public static class TypeStats {
        @Schema(description = "交易类型")
        private String transactionType;

        @Schema(description = "类型名称")
        private String typeName;

        @Schema(description = "金额")
        private BigDecimal amount;

        @Schema(description = "笔数")
        private Long count;

        @Schema(description = "占比")
        private BigDecimal percentage;
    }
}
