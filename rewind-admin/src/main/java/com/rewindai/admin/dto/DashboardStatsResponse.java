package com.rewindai.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 后台管理 - 仪表盘统计响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "仪表盘统计响应")
public class DashboardStatsResponse {

    @Schema(description = "用户统计")
    private UserStats userStats;

    @Schema(description = "梦境统计")
    private DreamStats dreamStats;

    @Schema(description = "互动统计")
    private InteractionStats interactionStats;

    @Schema(description = "今日数据")
    private TodayStats todayStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户统计")
    public static class UserStats {
        @Schema(description = "总用户数")
        private Long totalUsers;

        @Schema(description = "今日新增用户")
        private Long todayNewUsers;

        @Schema(description = "活跃用户数（今日）")
        private Long todayActiveUsers;

        @Schema(description = "封禁用户数")
        private Long bannedUsers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "梦境统计")
    public static class DreamStats {
        @Schema(description = "总梦境数")
        private Long totalDreams;

        @Schema(description = "今日新增梦境")
        private Long todayNewDreams;

        @Schema(description = "公开梦境数")
        private Long publicDreams;

        @Schema(description = "待审核内容数")
        private Long pendingReviewCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "互动统计")
    public static class InteractionStats {
        @Schema(description = "总点赞数")
        private Long totalLikes;

        @Schema(description = "总评论数")
        private Long totalComments;

        @Schema(description = "总打赏数")
        private Long totalRewards;

        @Schema(description = "总打赏金额")
        private BigDecimal totalRewardAmount;

        @Schema(description = "互动总数（点赞+评论+打赏）")
        private Long totalInteractions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "今日数据")
    public static class TodayStats {
        @Schema(description = "今日新增用户")
        private Long newUsers;

        @Schema(description = "今日新增梦境")
        private Long newDreams;

        @Schema(description = "今日点赞数")
        private Long likes;

        @Schema(description = "今日评论数")
        private Long comments;

        @Schema(description = "今日打赏数")
        private Long rewards;
    }
}
