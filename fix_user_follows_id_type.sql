-- =============================================
-- 修复 user_follows 表 id 字段类型
-- 从 BIGINT 改为 UUID
-- =============================================

-- 1. 备份现有数据（如果有）
CREATE TABLE IF NOT EXISTS user_follows_backup AS SELECT * FROM user_follows;

-- 2. 删除旧表
DROP TABLE IF EXISTS user_follows;

-- 3. 重新创建表（使用正确的UUID类型）
CREATE TABLE IF NOT EXISTS user_follows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    follower_id UUID NOT NULL,
    following_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE(follower_id, following_id)
);

-- 4. 创建索引
CREATE INDEX IF NOT EXISTS idx_follow_follower ON user_follows(follower_id);
CREATE INDEX IF NOT EXISTS idx_follow_following ON user_follows(following_id);

-- 5. 如果备份表中有数据且可以转换，则尝试恢复（需要备份表中的id是UUID格式字符串）
-- 注意：如果备份表中的id是BIGINT，数据无法直接恢复，需要重新生成
-- 如果需要恢复数据，请根据实际情况调整

-- =============================================
-- 修复完成
-- =============================================

  SELECT * FROM pg_stat_activity WHERE query LIKE '%user_follows%';
