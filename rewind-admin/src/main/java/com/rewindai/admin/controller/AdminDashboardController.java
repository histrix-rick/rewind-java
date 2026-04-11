package com.rewindai.admin.controller;

import com.rewindai.admin.dto.DashboardStatsResponse;
import com.rewindai.admin.dto.DashboardTrendResponse;
import com.rewindai.admin.service.DashboardService;
import com.rewindai.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台管理 - 仪表盘控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "后台管理-仪表盘", description = "后台管理系统仪表盘接口")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "获取仪表盘统计", description = "获取首页仪表盘的各类统计数据")
    public Result<DashboardStatsResponse> getDashboardStats() {
        log.info("获取仪表盘统计数据");
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        return Result.success(stats);
    }

    @GetMapping("/trends")
    @Operation(summary = "获取趋势图表数据", description = "获取首页仪表盘的趋势图表数据")
    public Result<DashboardTrendResponse> getDashboardTrends() {
        log.info("获取仪表盘趋势图表数据");
        DashboardTrendResponse trends = dashboardService.getDashboardTrends();
        return Result.success(trends);
    }
}
