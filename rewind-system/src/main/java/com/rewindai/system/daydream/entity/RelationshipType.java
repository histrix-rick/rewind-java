package com.rewindai.system.daydream.entity;

import com.rewindai.system.dream.enums.RelationshipCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * 关系类型实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "relationship_types", indexes = {
        @Index(name = "idx_rel_type_active", columnList = "is_active"),
        @Index(name = "idx_rel_type_category", columnList = "category")
})
public class RelationshipType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false, length = 30)
    @Convert(converter = RelationshipCategoryConverter.class)
    @Builder.Default
    private RelationshipCategory category = RelationshipCategory.FRIEND;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Converter
    public static class RelationshipCategoryConverter implements AttributeConverter<RelationshipCategory, String> {
        @Override
        public String convertToDatabaseColumn(RelationshipCategory category) {
            return category != null ? category.name() : RelationshipCategory.OTHER.name();
        }

        @Override
        public RelationshipCategory convertToEntityAttribute(String name) {
            return name != null ? RelationshipCategory.valueOf(name) : RelationshipCategory.OTHER;
        }
    }
}
