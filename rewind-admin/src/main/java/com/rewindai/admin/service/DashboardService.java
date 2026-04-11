package com.rewindai.admin.service;

import com.rewindai.admin.dto.DashboardStatsResponse;
import com.rewindai.admin.dto.DashboardTrendResponse;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.repository.DaydreamRepository;
import com.rewindai.system.daydream.repository.DreamCommentRepository;
import com.rewindai.system.daydream.repository.DreamLikeRepository;
import com.rewindai.system.daydream.repository.DreamRewardRepository;
import com.rewindai.system.user.enums.UserStatus;
import com.rewindai.system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 后台管理 - 仪表盘统计服务
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final DaydreamRepository daydreamRepository;
    private final DreamCommentRepository dreamCommentRepository;
    private final DreamRewardRepository dreamRewardRepository;
    private final DreamLikeRepository dreamLikeRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取仪表盘统计数据
     */
    public DashboardStatsResponse getDashboardStats() {
        OffsetDateTime todayStart = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);

        // 用户统计
        long totalUsers = userRepository.count();
        long todayNewUsers = userRepository.countByCreatedAtAfter(todayStart);
        long bannedUsers = userRepository.countByStatus(UserStatus.BANNED);
        long todayActiveUsers = calculateTodayActiveUsers(todayStart);

        // 白日梦统计
        long totalDreams = daydreamRepository.count();
        long todayNewDreams = daydreamRepository.countByCreatedAtAfter(todayStart);
        long publicDreams = daydreamRepository.countByIsPublicTrue();
        long pendingReviewCount = daydreamRepository.countByReviewStatusPending();

        // 互动统计
        long totalLikes = daydreamRepository.sumLikeCount();
        long totalComments = dreamCommentRepository.count();
        long totalRewards = dreamRewardRepository.countAll();
        BigDecimal totalRewardAmount = daydreamRepository.sumRewardAmount();

        // 计算今日互动统计
        long todayLikes = dreamLikeRepository.countByCreatedAtAfter(todayStart);
        long todayComments = dreamCommentRepository.countByCreatedAtAfter(todayStart);
        long todayRewardCount = dreamRewardRepository.countByCreatedAtAfter(todayStart);

        // 计算互动总数
        long totalInteractions = totalLikes + totalComments + totalRewards;

        return DashboardStatsResponse.builder()
                .userStats(DashboardStatsResponse.UserStats.builder()
                        .totalUsers(totalUsers)
                        .todayNewUsers(todayNewUsers)
                        .todayActiveUsers(todayActiveUsers)
                        .bannedUsers(bannedUsers)
                        .build())
                .dreamStats(DashboardStatsResponse.DreamStats.builder()
                        .totalDreams(totalDreams)
                        .todayNewDreams(todayNewDreams)
                        .publicDreams(publicDreams)
                        .pendingReviewCount(pendingReviewCount)
                        .build())
                .interactionStats(DashboardStatsResponse.InteractionStats.builder()
                        .totalLikes(totalLikes)
                        .totalComments(totalComments)
                        .totalRewards(totalRewards)
                        .totalRewardAmount(totalRewardAmount)
                        .totalInteractions(totalInteractions)
                        .build())
                .todayStats(DashboardStatsResponse.TodayStats.builder()
                        .newUsers(todayNewUsers)
                        .newDreams(todayNewDreams)
                        .likes(todayLikes)
                        .comments(todayComments)
                        .rewards(todayRewardCount)
                        .build())
                .build();
    }

    /**
     * 获取仪表盘趋势图表数据
     */
    public DashboardTrendResponse getDashboardTrends() {
        // 生成最近7天的日期
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            dates.add(today.minusDays(i).format(DATE_FORMATTER));
        }

        // 用户增长趋势（近7天）
        List<Long> userGrowthValues = calculateDailyUserGrowth(dates);

        // 梦境发布趋势（近7天）
        List<Long> dreamPublishValues = calculateDailyDreamGrowth(dates);

        // 互动趋势（近7天：点赞、评论、打赏）
        List<Long> likeValues = calculateDailyLikes(dates);
        List<Long> commentValues = calculateDailyComments(dates);
        List<Long> rewardValues = calculateDailyRewards(dates);

        // 热门内容排行（按点赞数取TOP10）
        List<Daydream> topDreams = daydreamRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "likeCount"))
        ).getContent();

        List<DashboardTrendResponse.HotContentItem> hotContentList = new ArrayList<>();
        for (Daydream dream : topDreams) {
            hotContentList.add(DashboardTrendResponse.HotContentItem.builder()
                    .dreamId(dream.getId().toString())
                    .title(dream.getTitle())
                    .coverUrl(dream.getCoverUrl())
                    .viewCount(dream.getViewCount() != null ? dream.getViewCount().longValue() : 0L)
                    .likeCount(dream.getLikeCount() != null ? dream.getLikeCount().longValue() : 0L)
                    .commentCount(dream.getCommentCount() != null ? dream.getCommentCount().longValue() : 0L)
                    .sortValue(dream.getLikeCount() != null ? dream.getLikeCount().longValue() : 0L)
                    .build());
        }

        return DashboardTrendResponse.builder()
                .userGrowthTrend(DashboardTrendResponse.TrendData.builder()
                        .dates(dates)
                        .values(userGrowthValues)
                        .build())
                .dreamPublishTrend(DashboardTrendResponse.TrendData.builder()
                        .dates(dates)
                        .values(dreamPublishValues)
                        .build())
                .interactionTrend(DashboardTrendResponse.InteractionTrend.builder()
                        .dates(dates)
                        .likes(likeValues)
                        .comments(commentValues)
                        .rewards(rewardValues)
                        .build())
                .hotContentList(hotContentList)
                .build();
    }

    /**
     * 计算今日活跃用户数（今日有发布梦境、点赞、评论或打赏的用户）
     */
    private long calculateTodayActiveUsers(OffsetDateTime todayStart) {
        // 由于没有用户活动记录表，暂时返回0
        // 实际项目中应该查询今日有活动的用户数
        return 0L;
    }

    /**
     * 计算每日用户增长
     */
    private List<Long> calculateDailyUserGrowth(List<String> dates) {
        List<Long> values = new ArrayList<>();
        for (String dateStr : dates) {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            OffsetDateTime startOfDay = date.atStartOfDay().atOffset(ZoneOffset.UTC);
            OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
            values.add(userRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        }
        return values;
    }

    /**
     * 计算每日梦境发布
     */
    private List<Long> calculateDailyDreamGrowth(List<String> dates) {
        List<Long> values = new ArrayList<>();
        for (String dateStr : dates) {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            OffsetDateTime startOfDay = date.atStartOfDay().atOffset(ZoneOffset.UTC);
            OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
            values.add(daydreamRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        }
        return values;
    }

    /**
     * 计算每日点赞数
     */
    private List<Long> calculateDailyLikes(List<String> dates) {
        List<Long> values = new ArrayList<>();
        for (String dateStr : dates) {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            OffsetDateTime startOfDay = date.atStartOfDay().atOffset(ZoneOffset.UTC);
            OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
            values.add(dreamLikeRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        }
        return values;
    }

    /**
     * 计算每日评论数
     */
    private List<Long> calculateDailyComments(List<String> dates) {
        List<Long> values = new ArrayList<>();
        for (String dateStr : dates) {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            OffsetDateTime startOfDay = date.atStartOfDay().atOffset(ZoneOffset.UTC);
            OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
            values.add(dreamCommentRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        }
        return values;
    }

    /**
     * 计算每日打赏数
     */
    private List<Long> calculateDailyRewards(List<String> dates) {
        List<Long> values = new ArrayList<>();
        for (String dateStr : dates) {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            OffsetDateTime startOfDay = date.atStartOfDay().atOffset(ZoneOffset.UTC);
            OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
            values.add(dreamRewardRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        }
        return values;
    }
}
