-- =============================================
-- 修复 storage_configs 表的 bucket_access_type CHECK 约束
-- =============================================

-- 删除旧的 CHECK 约束
ALTER TABLE storage_configs DROP CONSTRAINT IF EXISTS storage_configs_bucket_access_type_check;

-- 添加新的 CHECK 约束，包含 PUBLIC_READ 和 PUBLIC_READ_WRITE
ALTER TABLE storage_configs ADD CONSTRAINT storage_configs_bucket_access_type_check
    CHECK (bucket_access_type IN ('PRIVATE', 'PUBLIC', 'PUBLIC_READ', 'PUBLIC_READ_WRITE', 'CUSTOM'));

