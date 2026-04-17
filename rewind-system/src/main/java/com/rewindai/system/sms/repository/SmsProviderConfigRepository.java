package com.rewindai.system.sms.repository;

import com.rewindai.system.sms.entity.SmsProviderConfig;
import com.rewindai.system.sms.enums.SmsProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 短信运营商配置Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface SmsProviderConfigRepository extends JpaRepository<SmsProviderConfig, Long> {

    Optional<SmsProviderConfig> findByProviderCode(SmsProvider providerCode);

    Optional<SmsProviderConfig> findByIsDefaultTrue();

    List<SmsProviderConfig> findByIsActiveTrue();

    List<SmsProviderConfig> findAllByOrderByProviderCodeAsc();
}
