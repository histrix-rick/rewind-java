-- ============================================
-- 修复 dream_contexts 表 node_id 字段可为空
-- 用于支持草稿状态的梦境上下文保存
-- ============================================

-- 修改 dream_contexts 表，使 node_id 字段可以为 NULL
ALTER TABLE dream_contexts ALTER COLUMN node_id DROP NOT NULL;

-- 修改 dream_relationships 表，使 node_id 字段也可以为 NULL
ALTER TABLE dream_relationships ALTER COLUMN node_id DROP NOT NULL;
