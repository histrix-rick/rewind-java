package com.rewindai.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 点赞状态响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeStatusResponse {

    /**
     * 是否已点赞
     */
    private Boolean liked;

    /**
     * 点赞数
     */
    private Long likeCount;
}
