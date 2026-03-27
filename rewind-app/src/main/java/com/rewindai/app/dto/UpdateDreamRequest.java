package com.rewindai.app.dto;

import com.rewindai.system.dream.enums.DreamPrivacy;
import com.rewindai.system.dream.enums.DreamStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 更新梦境请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
public class UpdateDreamRequest {

    @Size(max = 200, message = "标题最多200字符")
    private String title;

    private String content;

    private String coverUrl;

    private String dreamDate;

    private DreamStatus status;

    private DreamPrivacy privacy;

    private String tags;

    private String mood;

    private String weather;

    private Integer durationMinutes;

    private Boolean isLucid;

    private Boolean isRecurring;

    private Boolean isNightmare;

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
