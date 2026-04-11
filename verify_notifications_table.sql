-- =============================================
-- 验证 notifications 表是否存在
-- =============================================

-- 检查表是否存在
SELECT
    table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name = 'notifications';

-- 如果表存在，显示表结构
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'notifications') THEN
        RAISE NOTICE 'Table notifications exists!';
    ELSE
        RAISE NOTICE 'Table notifications does NOT exist!';
    END IF;
END $$;

-- 显示所有表
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;
