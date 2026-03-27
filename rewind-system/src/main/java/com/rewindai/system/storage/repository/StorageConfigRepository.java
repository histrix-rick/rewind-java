package com.rewindai.system.storage.repository;

import com.rewindai.system.storage.entity.StorageConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 存储配置 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface StorageConfigRepository extends JpaRepository<StorageConfig, Long> {

    Optional<StorageConfig> findByConfigKey(String configKey);

    Optional<StorageConfig> findByIsDefaultTrue();

    List<StorageConfig> findByConfigKeyContainingIgnoreCase(String configKey);

    List<StorageConfig> findByBucketNameContainingIgnoreCase(String bucketName);

    @Modifying
    @Query("UPDATE StorageConfig c SET c.isDefault = false WHERE c.isDefault = true")
    void clearDefault();
}
