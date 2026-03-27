#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
通过SSH修复 storage_configs 表的 bucket_access_type CHECK 约束
"""

import paramiko
import os


def fix_db_via_ssh():
    # SSH连接配置
    host = "101.32.10.240"
    user = "root"
    key_path = os.path.expanduser("~/.ssh/id_ed25519")

    print(f"正在连接到 {user}@{host}...")

    try:
        # 创建SSH客户端
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

        # 连接
        ssh.connect(
            hostname=host,
            username=user,
            key_filename=key_path
        )
        print("连接成功！\n")

        # SQL 命令
        sql_commands = """
        -- 删除旧的 CHECK 约束
        ALTER TABLE storage_configs DROP CONSTRAINT IF EXISTS storage_configs_bucket_access_type_check;

        -- 添加新的 CHECK 约束
        ALTER TABLE storage_configs ADD CONSTRAINT storage_configs_bucket_access_type_check
            CHECK (bucket_access_type IN ('PRIVATE', 'PUBLIC', 'PUBLIC_READ', 'PUBLIC_READ_WRITE', 'CUSTOM'));
        """

        print("=" * 60)
        print("执行SQL修复约束...")
        print("=" * 60)

        # 直接用数据库用户连接
        cmd = f"""PGPASSWORD=password_dSKCGm psql -h 127.0.0.1 -p 5432 -U user_N7TYjT -d rewind_db -c "{sql_commands.replace('"', '\\"')}" """
        stdin, stdout, stderr = ssh.exec_command(cmd)

        print(stdout.read().decode())
        err_output = stderr.read().decode()
        if err_output:
            print("错误输出:")
            print(err_output)

        # 验证约束
        print("\n" + "=" * 60)
        print("验证约束...")
        print("=" * 60)
        verify_cmd = """
        PGPASSWORD=password_dSKCGm psql -h 127.0.0.1 -p 5432 -U user_N7TYjT -d rewind_db -c "
        SELECT conname
        FROM pg_constraint
        WHERE conrelid = 'storage_configs'::regclass
        AND conname = 'storage_configs_bucket_access_type_check';
        "
        """
        stdin, stdout, stderr = ssh.exec_command(verify_cmd)
        print(stdout.read().decode())

        # 关闭连接
        ssh.close()
        print("\n连接已关闭")
        print("\n数据库约束修复完成!")

    except Exception as e:
        print(f"\n错误: {e}")
        import traceback
        traceback.print_exc()


if __name__ == "__main__":
    fix_db_via_ssh()

