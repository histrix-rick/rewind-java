package com.rewindai.app.dto;

import com.rewindai.system.dream.enums.DreamPrivacy;
import com.rewindai.system.dream.enums.DreamStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新白日梦请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
public class UpdateDaydreamRequest {

    @Size(max = 200, message = "标题最多200字符")
    private String title;

    private String description;

    private String coverUrl;

    private DreamStatus status;

    private DreamPrivacy privacy;
}
