package com.rewindai.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 梦境人物关系请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
public class DreamRelationshipRequest {

    @NotBlank(message = "人物姓名不能为空")
    @Size(max = 100, message = "人物姓名最多100字符")
    private String personName;

    @NotNull(message = "关系类型不能为空")
    private Long relationshipTypeId;

    private Integer intimacyLevel;

    @Size(max = 100, message = "亲密度描述最多100字符")
    private String intimacyDescription;

    private String notes;
}
