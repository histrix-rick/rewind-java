package com.rewindai.system.sms.provider;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.sms.entity.SmsProviderConfig;
import com.rewindai.system.sms.enums.SmsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信服务提供者工厂
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsProviderFactory {

    private final Map<String, com.rewindai.system.sms.provider.SmsProvider> providers;

    public com.rewindai.system.sms.provider.SmsProvider getProvider(SmsProvider provider) {
        return switch (provider) {
            case ALIYUN -> providers.get("aliyunSmsProvider");
            case TENCENT -> providers.get("tencentSmsProvider");
            case YUNPIAN -> providers.get("yunpianSmsProvider");
        };
    }

    public com.rewindai.system.sms.provider.SmsProvider getProvider(SmsProviderConfig config) {
        return getProvider(config.getProviderCode());
    }
}
