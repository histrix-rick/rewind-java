-- =============================================
-- 添加梦境关注表
-- 创建时间: 2026-03-30
-- =============================================

-- =============================================
-- 梦境关注表
-- =============================================
CREATE TABLE IF NOT EXISTS dream_follows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    dream_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, dream_id)
);

CREATE INDEX IF NOT EXISTS idx_df_user_id ON dream_follows(user_id);
CREATE INDEX IF NOT EXISTS idx_df_dream_id ON dream_follows(dream_id);

-- =============================================
-- 添加完成
-- =============================================
