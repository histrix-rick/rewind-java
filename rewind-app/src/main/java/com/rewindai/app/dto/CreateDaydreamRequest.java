package com.rewindai.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建白日梦请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
public class CreateDaydreamRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最多200字符")
    private String title;

    private String description;

    private String coverUrl;

    @NotNull(message = "起始时间不能为空")
    private LocalDate startDate;
}
