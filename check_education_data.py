#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查 education_levels 和 knowledge_questions 数据
"""

import paramiko
import os


def check_data():
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

        # 检查 education_levels
        print("=" * 60)
        print("1. education_levels 表数据")
        print("=" * 60)
        cmd = """
        docker exec -i 1Panel-postgresql-9ISB psql -U user_N7TYjT -d rewind_db -c "
        SELECT id, name, level FROM education_levels ORDER BY level;
        "
        """
        stdin, stdout, stderr = ssh.exec_command(cmd)
        print(stdout.read().decode())

        # 检查 knowledge_questions 按 education_level_id 分组统计
        print("\n" + "=" * 60)
        print("2. knowledge_questions 各 education_level_id 的题目数量")
        print("=" * 60)
        cmd = """
        docker exec -i 1Panel-postgresql-9ISB psql -U user_N7TYjT -d rewind_db -c "
        SELECT education_level_id, COUNT(*) as count
        FROM knowledge_questions
        WHERE is_active = true
        GROUP BY education_level_id
        ORDER BY education_level_id;
        "
        """
        stdin, stdout, stderr = ssh.exec_command(cmd)
        print(stdout.read().decode())

        # 检查硕士学历 (level=6) 的题目
        print("\n" + "=" * 60)
        print("3. 检查硕士学历 (level=6) 的题目")
        print("=" * 60)
        cmd = """
        docker exec -i 1Panel-postgresql-9ISB psql -U user_N7TYjT -d rewind_db -c "
        SELECT kq.id, kq.education_level_id, el.name as level_name, kq.question_text
        FROM knowledge_questions kq
        JOIN education_levels el ON kq.education_level_id = el.id
        WHERE el.level = 6 AND kq.is_active = true;
        "
        """
        stdin, stdout, stderr = ssh.exec_command(cmd)
        print(stdout.read().decode())

        # 关闭连接
        ssh.close()
        print("\n连接已关闭")

    except Exception as e:
        print(f"\n错误: {e}")
        import traceback
        traceback.print_exc()


if __name__ == "__main__":
    check_data()

