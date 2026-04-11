package com.rewindai.system.config.repository;

import com.rewindai.system.config.entity.SysConfig;
import com.rewindai.system.config.enums.ConfigCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统配置 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface SysConfigRepository extends JpaRepository<SysConfig, Long> {

    Optional<SysConfig> findByConfigKey(String configKey);

    List<SysConfig> findByConfigCategory(ConfigCategory category);

    @Modifying
    @Query("UPDATE SysConfig c SET c.configValue = :value WHERE c.configKey = :key")
    int updateValueByKey(@Param("key") String key, @Param("value") String value);

    @Query("SELECT c FROM SysConfig c WHERE c.configKey IN (:keys)")
    List<SysConfig> findByConfigKeys(@Param("keys") List<String> keys);
}
