#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
插入缺失的知识题目（大专、硕士、博士）
"""

import paramiko
import os


def insert_questions():
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

        # 插入缺失的题目
        print("=" * 60)
        print("插入缺失的知识题目...")
        print("=" * 60)

        sql = """
        INSERT INTO knowledge_questions (id, education_level_id, subject, question_text, option_a, option_b, option_c, option_d, correct_answer, difficulty, is_active, created_at) VALUES
        -- 大专题目 (level 4, id=4)
        (13, 4, 'GENERAL', '以下哪个是编程语言？', 'Python', 'Photoshop', 'Word', 'Excel', 'A', 3, true, NOW()),
        (14, 4, 'MATH', '导数可以表示函数的什么？', '面积', '变化率', '体积', '周长', 'B', 3, true, NOW()),
        (15, 4, 'COMPUTER', 'HTTP协议的默认端口是？', '21', '22', '80', '443', 'C', 3, true, NOW()),
        -- 硕士题目 (level 6, id=6)
        (16, 6, 'GENERAL', '机器学习中，过拟合是指？', '模型在训练集上表现差，测试集上表现好', '模型在训练集和测试集上都表现差', '模型在训练集上表现好，测试集上表现差', '模型在训练集和测试集上都表现好', 'C', 4, true, NOW()),
        (17, 6, 'MATH', '线性代数中，矩阵的秩表示？', '矩阵的行数', '矩阵的列数', '矩阵中线性无关的行或列的最大数目', '矩阵元素的总和', 'C', 4, true, NOW()),
        (18, 6, 'GENERAL', '以下哪个是深度学习框架？', 'Spring', 'TensorFlow', 'Django', 'Flask', 'B', 4, true, NOW()),
        -- 博士题目 (level 7, id=7)
        (19, 7, 'GENERAL', '图灵测试是用来测试什么的？', '计算机的计算速度', '计算机的存储容量', '机器是否具有智能', '计算机的网络速度', 'C', 5, true, NOW()),
        (20, 7, 'MATH', '贝叶斯定理主要用于？', '计算几何面积', '概率推理', '数值积分', '线性方程组求解', 'B', 5, true, NOW()),
        (21, 7, 'GENERAL', 'NP完全问题是指？', '可以在多项式时间内求解的问题', '不能在多项式时间内求解的问题', '所有NP问题中最难的问题', '没有解决方案的问题', 'C', 5, true, NOW())
        ON CONFLICT (id) DO UPDATE SET
            question_text = EXCLUDED.question_text,
            option_a = EXCLUDED.option_a,
            option_b = EXCLUDED.option_b,
            option_c = EXCLUDED.option_c,
            option_d = EXCLUDED.option_d,
            correct_answer = EXCLUDED.correct_answer;
        """

        cmd = f"docker exec -i 1Panel-postgresql-9ISB psql -U user_N7TYjT -d rewind_db -c \"{sql}\" "
        stdin, stdout, stderr = ssh.exec_command(cmd)

        print(stdout.read().decode())
        err_output = stderr.read().decode()
        if err_output:
            print("错误输出:")
            print(err_output)

        # 验证数据
        print("\n" + "=" * 60)
        print("验证数据...")
        print("=" * 60)
        verify_cmd = """
        docker exec -i 1Panel-postgresql-9ISB psql -U user_N7TYjT -d rewind_db -c "
        SELECT el.level, el.name, COUNT(kq.id) as question_count
        FROM education_levels el
        LEFT JOIN knowledge_questions kq ON el.id = kq.education_level_id AND kq.is_active = true
        GROUP BY el.level, el.name
        ORDER BY el.level;
        "
        """
        stdin, stdout, stderr = ssh.exec_command(verify_cmd)
        print(stdout.read().decode())

        # 关闭连接
        ssh.close()
        print("\n连接已关闭")
        print("\n知识题目插入完成!")

    except Exception as e:
        print(f"\n错误: {e}")
        import traceback
        traceback.print_exc()


if __name__ == "__main__":
    insert_questions()

