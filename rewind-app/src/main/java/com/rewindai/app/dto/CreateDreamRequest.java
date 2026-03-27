package com.rewindai.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rewindai.system.dream.enums.DreamPrivacy;
import com.rewindai.system.dream.enums.DreamStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 创建梦境请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
public class CreateDreamRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最多200字符")
    private String title;

    private String content;

    private String coverUrl;

    private String dreamDate;

    private DreamStatus status = DreamStatus.ACTIVE;

    private DreamPrivacy privacy = DreamPrivacy.PRIVATE;

    private String tags;

    private String mood;

    private String weather;

    private Integer durationMinutes;

    private Boolean isLucid = false;

    private Boolean isRecurring = false;

    private Boolean isNightmare = false;

    public OffsetDateTime getDreamDateAsOffsetDateTime() {
        if (dreamDate == null || dreamDate.isEmpty()) {
            return null;
        }
        try {
            // 尝试直接解析 OffsetDateTime
            return OffsetDateTime.parse(dreamDate);
        } catch (Exception e) {
            // 尝试解析为 LocalDate 然后转换
            try {
                LocalDate localDate = LocalDate.parse(dreamDate);
                return localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
            } catch (Exception e2) {
                return null;
            }
        }
    }
}
