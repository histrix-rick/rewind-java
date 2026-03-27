package com.rewindai.system.storage.provider;

import com.rewindai.system.storage.entity.StorageConfig;
import com.rewindai.system.storage.enums.StorageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 腾讯云COS URL生成单元测试
 *
 * @author Rewind.ai Team
 */
class TencentCosProviderUnitTest {

    private TencentCosProvider provider;
    private Method generateFileUrlMethod;

    @BeforeEach
    void setUp() throws Exception {
        provider = new TencentCosProvider();
        generateFileUrlMethod = TencentCosProvider.class.getDeclaredMethod(
                "generateFileUrl", StorageConfig.class, String.class);
        generateFileUrlMethod.setAccessible(true);
    }

    @Test
    void testGenerateFileUrl_NormalCase() throws Exception {
        StorageConfig config = createConfig(
                "hk-gm2x-1343366967",
                "cos.myqcloud.com",
                null,
                true
        );

        String url = (String) generateFileUrlMethod.invoke(provider, config, "test.jpg");

        assertEquals("https://hk-gm2x-1343366967.cos.myqcloud.com/test.jpg", url);
        assertFalse(url.contains(".."));
    }

    @Test
    void testGenerateFileUrl_WithAccelerateEndpoint() throws Exception {
        StorageConfig config = createConfig(
                "hk-gm2x-1343366967",
                "cos.accelerate.myqcloud.com",
                null,
                true
        );

        String url = (String) generateFileUrlMethod.invoke(provider, config, "test.jpg");

        assertEquals("https://hk-gm2x-1343366967.cos.accelerate.myqcloud.com/test.jpg", url);
        assertFalse(url.contains(".."));
    }

    @ParameterizedTest
    @CsvSource({
            ".cos.myqcloud.com, https://hk-gm2x-1343366967.cos.myqcloud.com/test.jpg",
            "cos.myqcloud.com., https://hk-gm2x-1343366967.cos.myqcloud.com/test.jpg",
            ".cos.myqcloud.com., https://hk-gm2x-1343366967.cos.myqcloud.com/test.jpg",
            "cos..myqcloud.com, https://hk-gm2x-1343366967.cos.myqcloud.com/test.jpg",
            "...cos...myqcloud.com..., https://hk-gm2x-1343366967.cos.myqcloud.com/test.jpg"
    })
    void testGenerateFileUrl_WithDirtyEndpoint(String endpoint, String expectedUrl) throws Exception {
        StorageConfig config = createConfig(
                "hk-gm2x-1343366967",
                endpoint,
                null,
                true
        );

        String url = (String) generateFileUrlMethod.invoke(provider, config, "test.jpg");

        assertEquals(expectedUrl, url);
        assertFalse(url.contains(".."));
    }

    @Test
    void testGenerateFileUrl_EndpointAlreadyContainsBucketName() throws Exception {
        StorageConfig config = createConfig(
                "hk-gm2x-1343366967",
                "hk-gm2x-1343366967.cos.myqcloud.com",
                null,
                true
        );

        String url = (String) generateFileUrlMethod.invoke(provider, config, "test.jpg");

        assertEquals("https://hk-gm2x-1343366967.cos.myqcloud.com/test.jpg", url);
        assertFalse(url.contains(".."));
    }

    @Test
    void testGenerateFileUrl_WithCustomDomain() throws Exception {
        StorageConfig config = createConfig(
                "hk-gm2x-1343366967",
                "cos.myqcloud.com",
                "cdn.example.com",
                true
        );

        String url = (String) generateFileUrlMethod.invoke(provider, config, "test.jpg");

        assertEquals("https://cdn.example.com/test.jpg", url);
        assertFalse(url.contains(".."));
    }

    @Test
    void testGenerateFileUrl_WithCustomDomainThatNeedsCleaning() throws Exception {
        StorageConfig config = createConfig(
                "hk-gm2x-1343366967",
                "cos.myqcloud.com",
                "  cdn..example.com.  ",
                true
        );

        String url = (String) generateFileUrlMethod.invoke(provider, config, "test.jpg");

        // 现在custom_domain也会被清理
        assertEquals("https://cdn.example.com/test.jpg", url);
        assertFalse(url.contains(".."));
    }

    @Test
    void testGenerateFileUrl_WithHttp() throws Exception {
        StorageConfig config = createConfig(
                "hk-gm2x-1343366967",
                "cos.myqcloud.com",
                null,
                false
        );

        String url = (String) generateFileUrlMethod.invoke(provider, config, "test.jpg");

        assertEquals("http://hk-gm2x-1343366967.cos.myqcloud.com/test.jpg", url);
        assertFalse(url.contains(".."));
    }

    @Test
    void testGenerateFileUrl_WithRealDatabaseData() throws Exception {
        // 使用数据库中的实际配置
        StorageConfig config = createConfig(
                "hk-gm2x-1343366967",
                "cos.accelerate.myqcloud.com",
                "hk-gm2x-1343366967",  // 数据库中的custom_domain
                true
        );

        String url = (String) generateFileUrlMethod.invoke(provider, config, "test.jpg");

        assertEquals("https://hk-gm2x-1343366967/test.jpg", url);
        assertFalse(url.contains(".."));
    }

    private StorageConfig createConfig(String bucketName, String accessEndpoint,
                                         String customDomain, Boolean isHttps) {
        StorageConfig config = new StorageConfig();
        config.setBucketName(bucketName);
        config.setAccessEndpoint(accessEndpoint);
        config.setCustomDomain(customDomain);
        config.setIsHttps(isHttps);
        config.setProvider(StorageProvider.TENCENT_COS);
        return config;
    }
}
