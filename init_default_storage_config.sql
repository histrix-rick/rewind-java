-- =============================================
-- 初始化默认存储配置
-- =============================================

-- 插入一个默认的腾讯云COS配置（请根据实际情况修改）
INSERT INTO storage_configs (
    config_key,
    provider,
    access_endpoint,
    access_key,
    secret_key,
    bucket_name,
    region,
    is_https,
    bucket_access_type,
    is_default,
    remark,
    created_at,
    updated_at
) VALUES (
    'default-cos',
    'TENCENT_COS',
    'cos.ap-guangzhou.myqcloud.com',
    'your-access-key-here',
    'your-secret-key-here',
    'your-bucket-name',
    'ap-guangzhou',
    true,
    'PRIVATE',
    true,
    '默认腾讯云COS存储配置',
    NOW(),
    NOW()
)
ON CONFLICT (config_key) DO UPDATE SET
    access_endpoint = EXCLUDED.access_endpoint,
    access_key = EXCLUDED.access_key,
    secret_key = EXCLUDED.secret_key,
    bucket_name = EXCLUDED.bucket_name,
    region = EXCLUDED.region,
    is_https = EXCLUDED.is_https,
    bucket_access_type = EXCLUDED.bucket_access_type,
    is_default = EXCLUDED.is_default,
    remark = EXCLUDED.remark,
    updated_at = NOW();

