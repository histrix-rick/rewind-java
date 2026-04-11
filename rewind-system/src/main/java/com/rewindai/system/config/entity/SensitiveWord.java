package com.rewindai.system.config.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * 敏感词实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sensitive_words", indexes = {
        @Index(name = "idx_sensitive_word", columnList = "word", unique = true),
        @Index(name = "idx_sensitive_word_type", columnList = "word_type")
})
public class SensitiveWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", nullable = false, unique = true, length = 100)
    private String word;

    @Column(name = "word_type", length = 20)
    @Builder.Default
    private String wordType = "NORMAL";

    @Column(name = "severity", length = 20)
    @Builder.Default
    private String severity = "MEDIUM";

    @Column(name = "remark", length = 500)
    private String remark;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
