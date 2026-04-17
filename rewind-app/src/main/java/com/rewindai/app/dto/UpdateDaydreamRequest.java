package com.rewindai.app.dto;

import com.rewindai.system.daydream.enums.DreamPrivacy;
import com.rewindai.system.daydream.enums.DreamStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 更新白日梦请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
public class UpdateDaydreamRequest {

    private String id;

    @Size(max = 200, message = "标题最多200字符")
    private String title;

    private String description;

    private String coverUrl;

    private LocalDate startDate;

    private DreamStatus status;

    private DreamPrivacy privacy;

    @Valid
    private DreamContextRequest context;

    @Valid
    private List<DreamRelationshipRequest> relationships;
}
