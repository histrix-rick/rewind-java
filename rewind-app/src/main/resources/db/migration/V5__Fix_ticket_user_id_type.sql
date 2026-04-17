-- ==========================================
-- 修复ticket和user_feedback表的user_id字段类型
-- 将BIGINT改为UUID
-- ==========================================

-- 先删除旧表
DROP TABLE IF EXISTS ticket CASCADE;
DROP TABLE IF EXISTS ticket_reply CASCADE;
DROP TABLE IF EXISTS user_feedback CASCADE;

-- 创建正确的ticket表（user_id为UUID类型）
CREATE TABLE IF NOT EXISTS ticket (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    user_nickname VARCHAR(100),
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50),
    priority VARCHAR(50),
    status VARCHAR(50),
    assigned_admin_id BIGINT,
    assigned_admin_name VARCHAR(100),
    last_reply_time TIMESTAMP,
    reply_count INTEGER NOT NULL DEFAULT 0,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_ticket_user_id ON ticket(user_id);
CREATE INDEX IF NOT EXISTS idx_ticket_status ON ticket(status);
CREATE INDEX IF NOT EXISTS idx_ticket_category ON ticket(category);
CREATE INDEX IF NOT EXISTS idx_ticket_created_time ON ticket(created_time);

-- 创建ticket_reply表（用BIGINT ID）
CREATE TABLE IF NOT EXISTS ticket_reply (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    replyer_id BIGINT,
    replyer_name VARCHAR(100),
    is_admin BOOLEAN NOT NULL DEFAULT false,
    content TEXT NOT NULL,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ticket_reply_ticket_id ON ticket_reply(ticket_id);
CREATE INDEX IF NOT EXISTS idx_ticket_reply_created_time ON ticket_reply(created_time);

-- 创建正确的user_feedback表（user_id为UUID类型）
CREATE TABLE IF NOT EXISTS user_feedback (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    user_nickname VARCHAR(100),
    category VARCHAR(100),
    category_id BIGINT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    contact_info VARCHAR(200),
    contact VARCHAR(200),
    status VARCHAR(50),
    handler_id BIGINT,
    handler_name VARCHAR(100),
    handle_note TEXT,
    handle_time TIMESTAMP,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_user_feedback_user_id ON user_feedback(user_id);
CREATE INDEX IF NOT EXISTS idx_user_feedback_status ON user_feedback(status);
CREATE INDEX IF NOT EXISTS idx_user_feedback_created_time ON user_feedback(created_time);
