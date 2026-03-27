package com.rewindai.system.storage.entity;

import com.rewindai.system.storage.enums.BucketAccessType;
import com.rewindai.system.storage.enums.StorageProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * 存储配置实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "storage_configs", indexes = {
    @Index(name = "idx_config_key", columnList = "config_key", unique = true),
    @Index(name = "idx_config_is_default", columnList = "is_default")
})
public class StorageConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    @Column(name = "access_endpoint", nullable = false, length = 255)
    private String accessEndpoint;

    @Column(name = "custom_domain", length = 255)
    private String customDomain;

    @Column(name = "access_key", nullable = false, length = 255)
    private String accessKey;

    @Column(name = "secret_key", nullable = false, length = 255)
    private String secretKey;

    @Column(name = "bucket_name", nullable = false, length = 100)
    private String bucketName;

    @Column(name = "path_prefix", length = 100)
    private String pathPrefix;

    @Column(name = "is_https", nullable = false)
    @Builder.Default
    private Boolean isHttps = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "bucket_access_type", nullable = false, length = 20)
    @Builder.Default
    private BucketAccessType bucketAccessType = BucketAccessType.PRIVATE;

    @Column(name = "region", length = 50)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private StorageProvider provider;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
