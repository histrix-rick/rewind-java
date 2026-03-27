package com.rewindai.system.storage.provider;

import com.rewindai.system.storage.entity.StorageConfig;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 腾讯云COS URL生成测试
 *
 * @author Rewind.ai Team
 */
public class TencentCosProviderTest {

    @Test
    public void testGenerateFileUrl() throws Exception {
        TencentCosProvider provider = new TencentCosProvider();
        Method method = TencentCosProvider.class.getDeclaredMethod("generateFileUrl", StorageConfig.class, String.class);
        method.setAccessible(true);

        // 测试用例1: endpoint无前导点
        StorageConfig config1 = new StorageConfig();
        config1.setBucketName("hk-gm2x-1343366967");
        config1.setAccessEndpoint("cos.myqcloud.com");
        config1.setIsHttps(true);

        String url1 = (String) method.invoke(provider, config1, "test.jpg");
        System.out.println("测试1 - endpoint无前导点: " + url1);
        assertTrue(url1.contains("hk-gm2x-1343366967.cos.myqcloud.com"));
        assertFalse(url1.contains(".."));

        // 测试用例2: endpoint有前导点
        StorageConfig config2 = new StorageConfig();
        config2.setBucketName("hk-gm2x-1343366967");
        config2.setAccessEndpoint(".cos.myqcloud.com");
        config2.setIsHttps(true);

        String url2 = (String) method.invoke(provider, config2, "test.jpg");
        System.out.println("测试2 - endpoint有前导点: " + url2);
        assertTrue(url2.contains("hk-gm2x-1343366967.cos.myqcloud.com"));
        assertFalse(url2.contains(".."));

        // 测试用例3: endpoint已经包含bucketName
        StorageConfig config3 = new StorageConfig();
        config3.setBucketName("hk-gm2x-1343366967");
        config3.setAccessEndpoint("hk-gm2x-1343366967.cos.myqcloud.com");
        config3.setIsHttps(true);

        String url3 = (String) method.invoke(provider, config3, "test.jpg");
        System.out.println("测试3 - endpoint已包含bucketName: " + url3);
        assertTrue(url3.contains("hk-gm2x-1343366967.cos.myqcloud.com"));
        assertFalse(url3.contains(".."));

        // 测试用例4: endpoint有双点
        StorageConfig config4 = new StorageConfig();
        config4.setBucketName("hk-gm2x-1343366967");
        config4.setAccessEndpoint("cos..myqcloud.com");
        config4.setIsHttps(true);

        String url4 = (String) method.invoke(provider, config4, "test.jpg");
        System.out.println("测试4 - endpoint有双点: " + url4);
        assertTrue(url4.contains("hk-gm2x-1343366967.cos.myqcloud.com"));
        assertFalse(url4.contains(".."));

        // 测试用例5: 使用自定义域名
        StorageConfig config5 = new StorageConfig();
        config5.setBucketName("hk-gm2x-1343366967");
        config5.setAccessEndpoint("cos.myqcloud.com");
        config5.setCustomDomain("cdn.example.com");
        config5.setIsHttps(true);

        String url5 = (String) method.invoke(provider, config5, "test.jpg");
        System.out.println("测试5 - 自定义域名: " + url5);
        assertTrue(url5.contains("cdn.example.com"));
        assertFalse(url5.contains(".."));

        System.out.println("\n所有测试通过！");
    }
}
