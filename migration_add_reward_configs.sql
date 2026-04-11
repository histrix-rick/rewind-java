-- =============================================
-- 添加奖励配置表
-- 创建时间: 2026-04-10
-- =============================================

CREATE TABLE IF NOT EXISTS reward_configs (
    id BIGSERIAL PRIMARY KEY,
    reward_type VARCHAR(50) NOT NULL,
    reward_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    reward_amount NUMERIC(19,2) NOT NULL,
    daily_limit INTEGER,
    total_limit INTEGER,
    min_level INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_reward_config_type ON reward_configs(reward_type);
CREATE INDEX IF NOT EXISTS idx_reward_config_active ON reward_configs(is_active);

-- 初始化一些默认奖励配置
INSERT INTO reward_configs (reward_type, reward_name, description, reward_amount, daily_limit, sort_order) VALUES
('DAILY_LOGIN', '每日登录', '每日首次登录奖励', 10.00, 1, 1),
('SHARE_DREAM', '分享梦境', '分享梦境给好友奖励', 5.00, 10, 2),
('CREATE_DREAM', '创建梦境', '成功创建新梦境奖励', 20.00, 3, 3),
('FIRST_COMMENT', '首次评论', '每日首次评论奖励', 2.00, 1, 4),
('LIKE_RECEIVED', '获得点赞', '梦境收到10个点赞奖励', 5.00, null, 5);
