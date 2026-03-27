-- =============================================
-- Rewind.ai 白日梦想家 - 数据库初始化脚本
-- 数据库: PostgreSQL
-- 创建时间: 2026-03-22
-- =============================================

-- =============================================
-- 用户属性表
-- =============================================
CREATE TABLE IF NOT EXISTS user_attributes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    financial_power INTEGER NOT NULL DEFAULT 50,
    intelligence INTEGER NOT NULL DEFAULT 50,
    physical_power INTEGER NOT NULL DEFAULT 50,
    charisma INTEGER NOT NULL DEFAULT 50,
    luck INTEGER NOT NULL DEFAULT 50,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE(user_id)
);

CREATE INDEX IF NOT EXISTS idx_attr_user_id ON user_attributes(user_id);

-- =============================================
-- 白日梦世界表
-- =============================================
CREATE TABLE IF NOT EXISTS dream_worlds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    cover_url TEXT,
    start_date DATE NOT NULL,
    current_day DATE NOT NULL,
    branch_root_id UUID,
    parent_branch_id UUID,
    current_branch_id UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_finished BOOLEAN NOT NULL DEFAULT false,
    is_public BOOLEAN NOT NULL DEFAULT false,
    status SMALLINT NOT NULL DEFAULT 1,
    privacy SMALLINT NOT NULL DEFAULT 0,
    view_count INTEGER NOT NULL DEFAULT 0,
    like_count INTEGER NOT NULL DEFAULT 0,
    share_count INTEGER NOT NULL DEFAULT 0,
    comment_count INTEGER NOT NULL DEFAULT 0,
    reward_amount NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_dw_user_id ON dream_worlds(user_id);
CREATE INDEX IF NOT EXISTS idx_dw_active ON dream_worlds(user_id, is_active);
CREATE INDEX IF NOT EXISTS idx_dw_created_at ON dream_worlds(created_at);

-- =============================================
-- 梦境分支表
-- =============================================
CREATE TABLE IF NOT EXISTS dream_branches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dream_id UUID NOT NULL,
    parent_node_id UUID,
    branch_name VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_db_dream_id ON dream_branches(dream_id);

-- =============================================
-- 时间轴节点表
-- =============================================
CREATE TABLE IF NOT EXISTS dream_timeline_nodes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dream_id UUID NOT NULL,
    branch_id UUID,
    sequence_num INTEGER NOT NULL,
    node_date DATE NOT NULL,
    user_decision TEXT,
    decision_summary VARCHAR(500),
    ai_feedback TEXT,
    reasoning_trace TEXT,
    is_approved BOOLEAN,
    attribute_snapshot TEXT,
    node_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    is_public BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tn_dream_id ON dream_timeline_nodes(dream_id);
CREATE INDEX IF NOT EXISTS idx_tn_branch ON dream_timeline_nodes(dream_id, branch_id);
CREATE INDEX IF NOT EXISTS idx_tn_sequence ON dream_timeline_nodes(dream_id, branch_id, sequence_num);

-- =============================================
-- AI 判定规则库表
-- =============================================
CREATE TABLE IF NOT EXISTS ai_judgment_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_type VARCHAR(30) NOT NULL,
    category VARCHAR(50),
    condition_pattern TEXT,
    judgment_result BOOLEAN NOT NULL,
    reasoning_template TEXT,
    example_question TEXT,
    example_answer TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    priority INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- =============================================
-- 初始化 AI 判定规则数据
-- =============================================
INSERT INTO ai_judgment_rules (rule_type, category, judgment_result, reasoning_template, example_question, example_answer, priority) VALUES
('HISTORICAL_FACT', '自然灾害', false, '地震是不可抗拒的自然力量，无法阻止。但你可以在地震发生前带走你的亲人。', '我要阻止汶川地震', 'AI判官判定：根据历史记录，汶川地震（2008-05-12）是不可改变的历史事实。地震是自然灾害，无法阻止。', 100),
('HISTORICAL_FACT', '自然灾害', false, '疫情是全球性的公共卫生事件，无法完全阻止。但你可以提醒身边的人做好防护。', '我要阻止新冠疫情', 'AI判官判定：根据历史记录，新冠疫情是全球性事件，无法阻止。但你可以提醒身边的人注意防护。', 99),
('PERSONAL_FATE', '个人命运', true, '这属于个人命运改变范畴，允许通过。你的现代记忆可能会对那个时空产生影响...', '我要去找十年前的自己', 'AI判官判定：逻辑合理，行动成功！历史的齿轮开始转动...', 50),
('PERSONAL_FATE', '个人命运', true, '这属于个人命运改变范畴，允许通过。也许这一次，结局会不同...', '我要劝家人买那只股票', 'AI判官判定：逻辑合理，行动成功！', 50),
('PERSONAL_FATE', '个人命运', true, '这属于个人命运改变范畴，允许通过。知识就是力量！', '我要努力学习考上更好的大学', 'AI判官判定：逻辑合理，行动成功！', 50);

-- =============================================
-- 用户身份预设表（管理员维护）
-- =============================================
CREATE TABLE IF NOT EXISTS user_identities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    min_age INTEGER NOT NULL DEFAULT 0,
    max_age INTEGER NOT NULL DEFAULT 150,
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_identity_active ON user_identities(is_active);
CREATE INDEX IF NOT EXISTS idx_identity_sort ON user_identities(sort_order);

-- =============================================
-- 关系类型表（管理员维护）
-- =============================================
CREATE TABLE IF NOT EXISTS relationship_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    category VARCHAR(30) NOT NULL DEFAULT 'FRIEND',
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_rel_type_active ON relationship_types(is_active);
CREATE INDEX IF NOT EXISTS idx_rel_type_category ON relationship_types(category);

-- =============================================
-- 学历知识水平配置表
-- =============================================
CREATE TABLE IF NOT EXISTS education_levels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    level INTEGER NOT NULL DEFAULT 0,
    question_count INTEGER NOT NULL DEFAULT 3,
    passing_score INTEGER NOT NULL DEFAULT 100,
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_edu_level_active ON education_levels(is_active);

-- =============================================
-- 梦境上下文表（关联时间轴节点）
-- =============================================
CREATE TABLE IF NOT EXISTS dream_contexts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dream_id UUID NOT NULL,
    node_id UUID NOT NULL,
    identity_id BIGINT,
    financial_amount NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    education_level_id BIGINT,
    birth_province VARCHAR(50),
    birth_city VARCHAR(50),
    birth_district VARCHAR(50),
    birth_address TEXT,
    dream_province VARCHAR(50),
    dream_city VARCHAR(50),
    dream_district VARCHAR(50),
    dream_address TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_ctx_dream_id ON dream_contexts(dream_id);
CREATE INDEX IF NOT EXISTS idx_ctx_node_id ON dream_contexts(node_id);

-- =============================================
-- 梦境人物关系表
-- =============================================
CREATE TABLE IF NOT EXISTS dream_relationships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dream_id UUID NOT NULL,
    node_id UUID NOT NULL,
    person_name VARCHAR(100) NOT NULL,
    relationship_type_id BIGINT NOT NULL,
    intimacy_level INTEGER NOT NULL DEFAULT 1,
    intimacy_description VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_rel_dream_id ON dream_relationships(dream_id);
CREATE INDEX IF NOT EXISTS idx_rel_node_id ON dream_relationships(node_id);

-- =============================================
-- 知识题库表（AI生成）
-- =============================================
CREATE TABLE IF NOT EXISTS knowledge_questions (
    id BIGSERIAL PRIMARY KEY,
    education_level_id BIGINT NOT NULL,
    subject VARCHAR(20) NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(500),
    option_b VARCHAR(500),
    option_c VARCHAR(500),
    option_d VARCHAR(500),
    correct_answer CHAR(1) NOT NULL,
    explanation TEXT,
    difficulty INTEGER NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_q_edu_level ON knowledge_questions(education_level_id);
CREATE INDEX IF NOT EXISTS idx_q_subject ON knowledge_questions(subject);
CREATE INDEX IF NOT EXISTS idx_q_active ON knowledge_questions(is_active);

-- =============================================
-- 初始化数据
-- =============================================

-- 初始化身份预设
INSERT INTO user_identities (name, description, min_age, max_age, sort_order) VALUES
('儿童', '学龄前儿童', 0, 6, 1),
('小学生', '小学阶段学生', 6, 12, 2),
('中学生', '初中/高中阶段学生', 12, 18, 3),
('大学生', '大学阶段学生', 18, 25, 4),
('刚入职的新手小白', '刚进入职场的新人', 22, 30, 5),
('某公司员工', '职场工作人士', 22, 65, 6);

-- 初始化关系类型
INSERT INTO relationship_types (name, category, sort_order) VALUES
('父亲', 'FAMILY', 1),
('母亲', 'FAMILY', 2),
('哥哥', 'FAMILY', 3),
('姐姐', 'FAMILY', 4),
('弟弟', 'FAMILY', 5),
('妹妹', 'FAMILY', 6),
('爱人', 'LOVER', 7),
('好朋友', 'FRIEND', 8),
('同学', 'FRIEND', 9),
('同事', 'COLLEAGUE', 10),
('老师', 'TEACHER', 11),
('其他', 'OTHER', 99);

-- 初始化学历水平
INSERT INTO education_levels (name, level, question_count, passing_score, sort_order) VALUES
('小学', 1, 3, 100, 1),
('初中', 2, 3, 100, 2),
('高中', 3, 3, 100, 3),
('大专', 4, 3, 100, 4),
('本科', 5, 3, 100, 5),
('硕士', 6, 3, 100, 6),
('博士', 7, 3, 100, 7);

-- =============================================
-- 初始化完成
-- =============================================
