-- =============================================
-- 迁移脚本：为 dream_timeline_nodes 添加 like_count 列
-- 执行时间: 2026-03-27
-- =============================================

-- 方式1：先更新现有数据为0，再添加NOT NULL约束
ALTER TABLE dream_timeline_nodes ADD COLUMN IF NOT EXISTS like_count INTEGER DEFAULT 0;

-- 如果列已存在但没有默认值，设置默认值
ALTER TABLE dream_timeline_nodes ALTER COLUMN like_count SET DEFAULT 0;

-- 确保现有数据都有值
UPDATE dream_timeline_nodes SET like_count = 0 WHERE like_count IS NULL;

-- =============================================
-- 同样为 dream_comments 确保 like_count 有默认值
-- =============================================
ALTER TABLE dream_comments ALTER COLUMN like_count SET DEFAULT 0;
UPDATE dream_comments SET like_count = 0 WHERE like_count IS NULL;

-- =============================================
-- 为 dream_worlds 确保点赞相关字段有默认值
-- =============================================
ALTER TABLE dream_worlds ALTER COLUMN like_count SET DEFAULT 0;
ALTER TABLE dream_worlds ALTER COLUMN comment_count SET DEFAULT 0;
ALTER TABLE dream_worlds ALTER COLUMN share_count SET DEFAULT 0;
ALTER TABLE dream_worlds ALTER COLUMN view_count SET DEFAULT 0;

UPDATE dream_worlds SET like_count = 0 WHERE like_count IS NULL;
UPDATE dream_worlds SET comment_count = 0 WHERE comment_count IS NULL;
UPDATE dream_worlds SET share_count = 0 WHERE share_count IS NULL;
UPDATE dream_worlds SET view_count = 0 WHERE view_count IS NULL;
