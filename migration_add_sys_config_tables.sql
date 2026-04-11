-- ==========================================
-- 模块八：系统配置管理 数据库迁移脚本
-- ==========================================

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_configs (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_name VARCHAR(100) NOT NULL,
    config_value TEXT,
    config_category VARCHAR(50) NOT NULL,
    value_type VARCHAR(20) DEFAULT 'STRING',
    description VARCHAR(500),
    is_encrypted BOOLEAN DEFAULT false,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_config_key ON sys_configs(config_key);
CREATE INDEX IF NOT EXISTS idx_config_category ON sys_configs(config_category);

-- 敏感词表
CREATE TABLE IF NOT EXISTS sensitive_words (
    id BIGSERIAL PRIMARY KEY,
    word VARCHAR(100) UNIQUE NOT NULL,
    word_type VARCHAR(20) DEFAULT 'NORMAL',
    severity VARCHAR(20) DEFAULT 'MEDIUM',
    remark VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_sensitive_word ON sensitive_words(word);
CREATE INDEX IF NOT EXISTS idx_sensitive_word_type ON sensitive_words(word_type);

-- 初始化默认配置
INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'site.name', '站点名称', 'Rewind.ai', 'BASIC', 'STRING', '站点显示名称', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'site.name');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'site.description', '站点描述', '记录你的每一个梦境', 'BASIC', 'STRING', '站点描述信息', 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'site.description');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'registration.enabled', '启用注册', 'true', 'BASIC', 'BOOLEAN', '是否允许新用户注册', 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'registration.enabled');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'invite.code.required', '需要邀请码', 'false', 'BASIC', 'BOOLEAN', '注册是否需要邀请码', 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'invite.code.required');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'user.agreement', '用户协议', '', 'BASIC', 'TEXT', '用户协议内容', 5, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'user.agreement');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'privacy.policy', '隐私政策', '', 'BASIC', 'TEXT', '隐私政策内容', 6, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'privacy.policy');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'customer.service.email', '客服邮箱', 'support@rewind.ai', 'BASIC', 'STRING', '客服联系邮箱', 7, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'customer.service.email');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'customer.service.phone', '客服电话', '', 'BASIC', 'STRING', '客服联系电话', 8, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'customer.service.phone');

-- 内容配置
INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'content.review.enabled', '启用内容审核', 'true', 'CONTENT', 'BOOLEAN', '是否启用内容审核', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'content.review.enabled');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'recommend.algorithm.version', '推荐算法版本', 'v1', 'CONTENT', 'STRING', '内容推荐算法版本', 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'recommend.algorithm.version');

-- 社交配置
INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'social.follow.max', '关注上限', '1000', 'SOCIAL', 'INTEGER', '单个用户最多关注人数', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'social.follow.max');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'social.like.daily.max', '每日点赞上限', '100', 'SOCIAL', 'INTEGER', '每日最多点赞次数', 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'social.like.daily.max');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'social.reward.min.amount', '打赏最小金额', '1', 'SOCIAL', 'INTEGER', '打赏最小梦想币金额', 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'social.reward.min.amount');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'social.reward.max.amount', '打赏最大金额', '10000', 'SOCIAL', 'INTEGER', '打赏最大梦想币金额', 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'social.reward.max.amount');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'social.comment.max.length', '评论最大长度', '1000', 'SOCIAL', 'INTEGER', '评论最大字符数', 5, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'social.comment.max.length');

-- 财务配置
INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'finance.exchange.rate', '梦想币汇率', '100', 'FINANCE', 'INTEGER', '1元人民币兑换多少梦想币', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'finance.exchange.rate');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'finance.withdrawal.min.amount', '提现最小金额', '1000', 'FINANCE', 'INTEGER', '提现最小梦想币金额', 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'finance.withdrawal.min.amount');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'finance.withdrawal.enabled', '启用提现', 'true', 'FINANCE', 'BOOLEAN', '是否允许用户提现', 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'finance.withdrawal.enabled');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'finance.reward.share.ratio', '打赏分成比例', '0.7', 'FINANCE', 'DECIMAL', '作者获得打赏金额的比例', 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'finance.reward.share.ratio');

-- 推送配置
INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'push.enabled', '启用推送', 'true', 'PUSH', 'BOOLEAN', '是否启用消息推送', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'push.enabled');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'push.time.start', '推送开始时间', '08:00', 'PUSH', 'STRING', '允许推送的开始时间', 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'push.time.start');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'push.time.end', '推送结束时间', '22:00', 'PUSH', 'STRING', '允许推送的结束时间', 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'push.time.end');

INSERT INTO sys_configs (config_key, config_name, config_value, config_category, value_type, description, sort_order, created_at, updated_at)
SELECT 'push.daily.limit', '每日推送次数限制', '5', 'PUSH', 'INTEGER', '单个用户每日最多接收推送次数', 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_configs WHERE config_key = 'push.daily.limit');
