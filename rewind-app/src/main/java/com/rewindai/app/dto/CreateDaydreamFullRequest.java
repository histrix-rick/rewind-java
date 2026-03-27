package com.rewindai.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 完整创建白日梦请求 DTO（包含上下文和关系）
 *
 * @author Rewind.ai Team
 */
@Data
public class CreateDaydreamFullRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最多200字符")
    private String title;

    private String description;

    private String coverUrl;

    @NotNull(message = "起始时间不能为空")
    private LocalDate startDate;

    private Integer privacy;

    @Valid
    private DreamContextRequest context;

    @Valid
    private List<DreamRelationshipRequest> relationships;
}
