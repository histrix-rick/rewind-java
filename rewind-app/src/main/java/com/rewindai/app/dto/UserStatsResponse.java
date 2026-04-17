package com.rewindai.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户统计数据响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {

    /**
     * 梦境总数
     */
    private Long dreamCount;

    /**
     * 公开梦境数
     */
    private Long publicCount;

    /**
     * 累计浏览量
     */
    private Long totalViews;

    /**
     * 累计获赞数
     */
    private Long totalLikes;

    /**
     * 累计获得梦想币
     */
    private Long totalEarned;
}
