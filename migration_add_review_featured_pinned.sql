-- ===========================================
-- 数据库迁移: 添加内容审核、精选、置顶字段
-- 日期: 2026-04-10
-- ===========================================

-- 1. 添加审核状态字段
ALTER TABLE dream_worlds
ADD COLUMN IF NOT EXISTS review_status INTEGER DEFAULT 1;

-- 添加审核时间字段
ALTER TABLE dream_worlds
ADD COLUMN IF NOT EXISTS reviewed_at TIMESTAMP WITH TIME ZONE;

-- 添加审核人ID字段
ALTER TABLE dream_worlds
ADD COLUMN IF NOT EXISTS reviewed_by UUID;

-- 添加审核原因字段
ALTER TABLE dream_worlds
ADD COLUMN IF NOT EXISTS review_reason VARCHAR(500);

-- 2. 添加精选字段
ALTER TABLE dream_worlds
ADD COLUMN IF NOT EXISTS is_featured BOOLEAN DEFAULT FALSE;

-- 添加精选时间字段
ALTER TABLE dream_worlds
ADD COLUMN IF NOT EXISTS featured_at TIMESTAMP WITH TIME ZONE;

-- 3. 添加置顶字段
ALTER TABLE dream_worlds
ADD COLUMN IF NOT EXISTS is_pinned BOOLEAN DEFAULT FALSE;

-- 添加置顶时间字段
ALTER TABLE dream_worlds
ADD COLUMN IF NOT EXISTS pinned_at TIMESTAMP WITH TIME ZONE;

-- 4. 创建相关索引
CREATE INDEX IF NOT EXISTS idx_dw_review_status ON dream_worlds(review_status);
CREATE INDEX IF NOT EXISTS idx_dw_featured ON dream_worlds(is_featured);
CREATE INDEX IF NOT EXISTS idx_dw_pinned ON dream_worlds(is_pinned);

-- 5. 更新现有数据的默认值
UPDATE dream_worlds SET review_status = 1 WHERE review_status IS NULL;
UPDATE dream_worlds SET is_featured = FALSE WHERE is_featured IS NULL;
UPDATE dream_worlds SET is_pinned = FALSE WHERE is_pinned IS NULL;

-- ===========================================
-- 迁移完成
-- ===========================================
