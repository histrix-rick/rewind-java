-- ============================================
-- Rewind.ai 后台管理系统 - 短信服务与实名认证增强
-- 数据库: PostgreSQL
-- 版本: V4
-- 创建日期: 2026-04-11
-- ============================================

-- ============================================
-- 模块十: 短信服务配置表
-- ============================================

-- 短信运营商配置表
CREATE TABLE IF NOT EXISTS sms_provider_configs (
    id BIGSERIAL PRIMARY KEY,
    provider_code VARCHAR(50) UNIQUE NOT NULL,
    provider_name VARCHAR(100) NOT NULL,
    access_key_id VARCHAR(200),
    access_key_secret VARCHAR(200),
    sign_name VARCHAR(100),
    template_code_login VARCHAR(100),
    template_code_register VARCHAR(100),
    template_code_verify VARCHAR(100),
    endpoint VARCHAR(200),
    region VARCHAR(100),
    is_default BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_by_admin_id INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sms_provider_configs_code ON sms_provider_configs(provider_code);
CREATE INDEX IF NOT EXISTS idx_sms_provider_configs_is_active ON sms_provider_configs(is_active);
CREATE INDEX IF NOT EXISTS idx_sms_provider_configs_is_default ON sms_provider_configs(is_default);

COMMENT ON TABLE sms_provider_configs IS '短信运营商配置表';
COMMENT ON COLUMN sms_provider_configs.provider_code IS '运营商编码: ALIYUN, TENCENT, YUNPIAN';
COMMENT ON COLUMN sms_provider_configs.sign_name IS '短信签名';
COMMENT ON COLUMN sms_provider_configs.template_code_login IS '登录验证码模板编码';
COMMENT ON COLUMN sms_provider_configs.template_code_register IS '注册验证码模板编码';
COMMENT ON COLUMN sms_provider_configs.template_code_verify IS '实名认证验证码模板编码';

-- 扩展验证码表
ALTER TABLE sys_verification_codes
ADD COLUMN IF NOT EXISTS target_type VARCHAR(20) DEFAULT 'PHONE',
ADD COLUMN IF NOT EXISTS send_status VARCHAR(20) DEFAULT 'PENDING',
ADD COLUMN IF NOT EXISTS provider_code VARCHAR(50),
ADD COLUMN IF NOT EXISTS send_result TEXT,
ADD COLUMN IF NOT EXISTS retry_count INTEGER DEFAULT 0,
ADD COLUMN IF NOT EXISTS max_retry_at TIMESTAMP WITH TIME ZONE;

CREATE INDEX IF NOT EXISTS idx_sys_verification_codes_target_type ON sys_verification_codes(target_type);
CREATE INDEX IF NOT EXISTS idx_sys_verification_codes_send_status ON sys_verification_codes(send_status);
CREATE INDEX IF NOT EXISTS idx_sys_verification_codes_provider ON sys_verification_codes(provider_code);

COMMENT ON COLUMN sys_verification_codes.target_type IS '目标类型: PHONE, EMAIL';
COMMENT ON COLUMN sys_verification_codes.send_status IS '发送状态: PENDING, SENT, FAILED';
COMMENT ON COLUMN sys_verification_codes.provider_code IS '使用的短信运营商编码';
COMMENT ON COLUMN sys_verification_codes.send_result IS '发送结果详情';
COMMENT ON COLUMN sys_verification_codes.retry_count IS '重试次数';

-- ============================================
-- 插入系统配置项 - 短信服务
-- ============================================

INSERT INTO sys_configs (config_key, config_value, config_name, config_group, description, value_type, is_system, created_at, updated_at)
VALUES
    ('sms.enabled', 'true', '启用短信服务', 'SMS', '是否启用真实短信发送，关闭时使用测试验证码123456', 'BOOLEAN', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('sms.default.provider', 'ALIYUN', '默认短信运营商', 'SMS', '默认使用的短信运营商: ALIYUN, TENCENT, YUNPIAN', 'STRING', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('sms.test.code', '123456', '测试验证码', 'SMS', '短信服务关闭时使用的统一测试验证码', 'STRING', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('sms.code.length', '6', '验证码长度', 'SMS', '验证码字符长度', 'INTEGER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('sms.code.expire.minutes', '5', '验证码有效期(分钟)', 'SMS', '验证码过期时间，单位分钟', 'INTEGER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('sms.send.interval.seconds', '60', '发送间隔(秒)', 'SMS', '同一目标发送验证码的最小间隔时间，单位秒', 'INTEGER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('sms.daily.limit', '20', '每日发送上限', 'SMS', '同一目标每日最多接收验证码次数', 'INTEGER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('realname.verify.enabled', 'true', '启用实名认证验证', 'REALNAME', '是否调用第三方实名认证接口', 'BOOLEAN', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (config_key) DO NOTHING;

-- ============================================
-- 插入默认短信运营商配置（模板）
-- ============================================

INSERT INTO sms_provider_configs (provider_code, provider_name, is_default, is_active, created_at, updated_at)
VALUES
    ('ALIYUN', '阿里云短信服务', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TENCENT', '腾讯云短信服务', false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('YUNPIAN', '云片网短信服务', false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (provider_code) DO NOTHING;

-- ============================================
-- 更新完成
-- ============================================
