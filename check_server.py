#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查服务器环境
"""

import paramiko
import os


def check_server():
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

        # 检查Docker
        print("=" * 60)
        print("检查Docker状态")
        print("=" * 60)
        stdin, stdout, stderr = ssh.exec_command("docker ps -a")
        print(stdout.read().decode())
        print(stderr.read().decode())

        # 检查进程
        print("\n" + "=" * 60)
        print("检查PostgreSQL相关进程")
        print("=" * 60)
        stdin, stdout, stderr = ssh.exec_command("ps aux | grep -E 'postgres|docker' | grep -v grep")
        print(stdout.read().decode())

        # 关闭连接
        ssh.close()
        print("\n连接已关闭")

    except Exception as e:
        print(f"\n错误: {e}")
        import traceback
        traceback.print_exc()


if __name__ == "__main__":
    check_server()

