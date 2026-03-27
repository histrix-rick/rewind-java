-- =============================================
-- Rewind.ai 白日梦想家 - 初始化数据脚本
-- 可以重复执行（使用 ON CONFLICT 避免重复）
-- =============================================

-- =============================================
-- 初始化身份预设
-- =============================================
INSERT INTO user_identities (id, name, description, min_age, max_age, sort_order, is_active)
VALUES
    (1, '儿童', '学龄前儿童', 0, 6, 1, true),
    (2, '小学生', '小学阶段学生', 6, 12, 2, true),
    (3, '中学生', '初中/高中阶段学生', 12, 18, 3, true),
    (4, '大学生', '大学阶段学生', 18, 25, 4, true),
    (5, '刚入职的新手小白', '刚进入职场的新人', 22, 30, 5, true),
    (6, '某公司员工', '职场工作人士', 22, 65, 6, true)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    min_age = EXCLUDED.min_age,
    max_age = EXCLUDED.max_age,
    sort_order = EXCLUDED.sort_order;

-- =============================================
-- 初始化关系类型
-- =============================================
INSERT INTO relationship_types (id, name, category, sort_order, is_active)
VALUES
    (1, '父亲', 'FAMILY', 1, true),
    (2, '母亲', 'FAMILY', 2, true),
    (3, '哥哥', 'FAMILY', 3, true),
    (4, '姐姐', 'FAMILY', 4, true),
    (5, '弟弟', 'FAMILY', 5, true),
    (6, '妹妹', 'FAMILY', 6, true),
    (7, '爱人', 'LOVER', 7, true),
    (8, '好朋友', 'FRIEND', 8, true),
    (9, '同学', 'FRIEND', 9, true),
    (10, '同事', 'COLLEAGUE', 10, true),
    (11, '老师', 'TEACHER', 11, true),
    (12, '其他', 'OTHER', 99, true)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    category = EXCLUDED.category,
    sort_order = EXCLUDED.sort_order;

-- =============================================
-- 初始化学历水平
-- =============================================
INSERT INTO education_levels (id, name, level, question_count, passing_score, sort_order, is_active)
VALUES
    (1, '小学', 1, 3, 100, 1, true),
    (2, '初中', 2, 3, 100, 2, true),
    (3, '高中', 3, 3, 100, 3, true),
    (4, '大专', 4, 3, 100, 4, true),
    (5, '本科', 5, 3, 100, 5, true),
    (6, '硕士', 6, 3, 100, 6, true),
    (7, '博士', 7, 3, 100, 7, true)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    level = EXCLUDED.level,
    question_count = EXCLUDED.question_count,
    passing_score = EXCLUDED.passing_score,
    sort_order = EXCLUDED.sort_order;

-- =============================================
-- 初始化一些示例知识题目
-- =============================================
INSERT INTO knowledge_questions (id, education_level_id, subject, question_text, option_a, option_b, option_c, option_d, correct_answer, difficulty, is_active)
VALUES
    -- 小学题目 (level 1)
    (1, 1, 'CHINESE', '中国的首都是哪里？', '上海', '北京', '广州', '深圳', 'B', 1, true),
    (2, 1, 'MATH', '1 + 1 = ?', '1', '2', '3', '4', 'B', 1, true),
    (3, 1, 'GENERAL', '太阳从哪个方向升起？', '东边', '西边', '南边', '北边', 'A', 1, true),
    -- 初中题目 (level 2)
    (4, 2, 'CHINESE', '《红楼梦》的作者是谁？', '罗贯中', '曹雪芹', '施耐庵', '吴承恩', 'B', 2, true),
    (5, 2, 'MATH', '勾股定理中，a² + b² = ?', 'a² - b²', 'c²', 'a² + c²', 'b² + c²', 'B', 2, true),
    (6, 2, 'ENGLISH', 'What is the past tense of "go"?', 'goed', 'went', 'gone', 'going', 'B', 2, true),
    -- 高中题目 (level 3)
    (7, 3, 'PHYSICS', '牛顿第一定律又被称为？', '加速度定律', '惯性定律', '作用力定律', '万有引力定律', 'B', 3, true),
    (8, 3, 'CHEMISTRY', '水的化学式是？', 'H2O', 'CO2', 'O2', 'H2', 'A', 3, true),
    (9, 3, 'HISTORY', '中国第一个统一的封建王朝是？', '商朝', '周朝', '秦朝', '汉朝', 'C', 3, true),
    -- 本科题目 (level 5)
    (10, 5, 'GENERAL', '人工智能的英文缩写是？', 'IT', 'AI', 'IQ', 'EQ', 'B', 3, true),
    (11, 5, 'MATH', '圆周率π大约等于多少？', '3.14', '3.16', '3.12', '3.18', 'A', 3, true),
    (12, 5, 'GEOGRAPHY', '世界上最高的山峰是？', '喜马拉雅山', '珠穆朗玛峰', '乞力马扎罗山', '阿尔卑斯山', 'B', 3, true),
    -- 大专题目 (level 4)
    (13, 4, 'GENERAL', '以下哪个是编程语言？', 'Python', 'Photoshop', 'Word', 'Excel', 'A', 3, true),
    (14, 4, 'MATH', '导数可以表示函数的什么？', '面积', '变化率', '体积', '周长', 'B', 3, true),
    (15, 4, 'COMPUTER', 'HTTP协议的默认端口是？', '21', '22', '80', '443', 'C', 3, true),
    -- 硕士题目 (level 6)
    (16, 6, 'GENERAL', '机器学习中，过拟合是指？', '模型在训练集上表现差，测试集上表现好', '模型在训练集和测试集上都表现差', '模型在训练集上表现好，测试集上表现差', '模型在训练集和测试集上都表现好', 'C', 4, true),
    (17, 6, 'MATH', '线性代数中，矩阵的秩表示？', '矩阵的行数', '矩阵的列数', '矩阵中线性无关的行或列的最大数目', '矩阵元素的总和', 'C', 4, true),
    (18, 6, 'GENERAL', '以下哪个是深度学习框架？', 'Spring', 'TensorFlow', 'Django', 'Flask', 'B', 4, true),
    -- 博士题目 (level 7)
    (19, 7, 'GENERAL', '图灵测试是用来测试什么的？', '计算机的计算速度', '计算机的存储容量', '机器是否具有智能', '计算机的网络速度', 'C', 5, true),
    (20, 7, 'MATH', '贝叶斯定理主要用于？', '计算几何面积', '概率推理', '数值积分', '线性方程组求解', 'B', 5, true),
    (21, 7, 'GENERAL', 'NP完全问题是指？', '可以在多项式时间内求解的问题', '不能在多项式时间内求解的问题', '所有NP问题中最难的问题', '没有解决方案的问题', 'C', 5, true)
ON CONFLICT (id) DO UPDATE SET
    question_text = EXCLUDED.question_text,
    option_a = EXCLUDED.option_a,
    option_b = EXCLUDED.option_b,
    option_c = EXCLUDED.option_c,
    option_d = EXCLUDED.option_d,
    correct_answer = EXCLUDED.correct_answer;

-- =============================================
-- 验证数据
-- =============================================
SELECT 'user_identities' as table_name, COUNT(*) as count FROM user_identities WHERE is_active = true
UNION ALL
SELECT 'relationship_types' as table_name, COUNT(*) as count FROM relationship_types WHERE is_active = true
UNION ALL
SELECT 'education_levels' as table_name, COUNT(*) as count FROM education_levels WHERE is_active = true
UNION ALL
SELECT 'knowledge_questions' as table_name, COUNT(*) as count FROM knowledge_questions WHERE is_active = true;
