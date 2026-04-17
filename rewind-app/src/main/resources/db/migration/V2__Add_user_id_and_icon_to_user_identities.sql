-- ============================================
-- 为 user_identities 表添加 user_id 和 icon 字段
-- 用于支持用户自定义身份
-- ============================================

-- 添加 user_id 字段（用于区分系统身份和用户自定义身份）
ALTER TABLE user_identities ADD COLUMN user_id UUID;

-- 添加 icon 字段（用于存储自定义身份的图标）
ALTER TABLE user_identities ADD COLUMN icon VARCHAR(50);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_identity_user ON user_identities(user_id);
