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
 * 后台管理 - 仪表盘趋势图表响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "仪表盘趋势图表响应")
public class DashboardTrendResponse {

    @Schema(description = "用户增长趋势")
    private TrendData userGrowthTrend;

    @Schema(description = "梦境发布趋势")
    private TrendData dreamPublishTrend;

    @Schema(description = "互动活跃趋势")
    private InteractionTrend interactionTrend;

    @Schema(description = "热门内容排行")
    private List<HotContentItem> hotContentList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "趋势数据")
    public static class TrendData {
        @Schema(description = "日期列表")
        private List<String> dates;

        @Schema(description = "数据列表")
        private List<Long> values;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "互动趋势数据")
    public static class InteractionTrend {
        @Schema(description = "日期列表")
        private List<String> dates;

        @Schema(description = "点赞数据")
        private List<Long> likes;

        @Schema(description = "评论数据")
        private List<Long> comments;

        @Schema(description = "打赏数据")
        private List<Long> rewards;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "热门内容项")
    public static class HotContentItem {
        @Schema(description = "梦境ID")
        private String dreamId;

        @Schema(description = "梦境标题")
        private String title;

        @Schema(description = "封面URL")
        private String coverUrl;

        @Schema(description = "作者昵称")
        private String authorNickname;

        @Schema(description = "浏览数")
        private Long viewCount;

        @Schema(description = "点赞数")
        private Long likeCount;

        @Schema(description = "评论数")
        private Long commentCount;

        @Schema(description = "排序值（用于排行）")
        private Long sortValue;
    }
}
