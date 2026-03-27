#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查所有 knowledge_questions 数据
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

        # 检查所有 knowledge_questions
        print("=" * 60)
        print("所有 knowledge_questions 数据")
        print("=" * 60)
        cmd = """
        docker exec -i 1Panel-postgresql-9ISB psql -U user_N7TYjT -d rewind_db -c "
        SELECT id, education_level_id, subject, question_text, correct_answer, is_active
        FROM knowledge_questions
        ORDER BY id;
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

