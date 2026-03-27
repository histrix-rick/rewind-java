#!/usr/bin/env python3
"""
修复 storage_configs 表的 bucket_access_type CHECK 约束
"""

import psycopg2
from psycopg2 import OperationalError


def create_connection():
    """创建数据库连接"""
    try:
        conn = psycopg2.connect(
            host="101.32.10.240",
            port=5432,
            database="rewind_db",
            user="user_N7TYjT",
            password="password_dSKCGm"
        )
        print("数据库连接成功")
        return conn
    except OperationalError as e:
        print(f"数据库连接失败: {e}")
        return None


def fix_bucket_access_type_constraint(conn):
    """修复 bucket_access_type CHECK 约束"""
    try:
        cursor = conn.cursor()

        # 1. 删除旧的 CHECK 约束
        print("删除旧的 CHECK 约束...")
        cursor.execute("""
            ALTER TABLE storage_configs
            DROP CONSTRAINT IF EXISTS storage_configs_bucket_access_type_check
        """)
        print("旧约束已删除")

        # 2. 添加新的 CHECK 约束
        print("添加新的 CHECK 约束...")
        cursor.execute("""
            ALTER TABLE storage_configs
            ADD CONSTRAINT storage_configs_bucket_access_type_check
            CHECK (bucket_access_type IN ('PRIVATE', 'PUBLIC', 'PUBLIC_READ', 'PUBLIC_READ_WRITE', 'CUSTOM'))
        """)
        print("新约束已添加")

        # 3. 提交事务
        conn.commit()
        print("约束修复成功!")

        # 4. 验证约束
        cursor.execute("""
            SELECT conname
            FROM pg_constraint
            WHERE conrelid = 'storage_configs'::regclass
            AND conname = 'storage_configs_bucket_access_type_check'
        """)
        result = cursor.fetchone()
        if result:
            print(f"验证成功: 约束 {result[0]} 已存在")

        cursor.close()
        return True

    except Exception as e:
        conn.rollback()
        print(f"修复失败: {e}")
        return False


def main():
    conn = create_connection()
    if not conn:
        return

    success = fix_bucket_access_type_constraint(conn)
    conn.close()

    if success:
        print("\n数据库约束修复完成!")
    else:
        print("\n数据库约束修复失败!")


if __name__ == "__main__":
    main()

