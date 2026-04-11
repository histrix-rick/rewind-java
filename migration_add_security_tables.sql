-- 模块九：安全与风控数据库迁移脚本
-- 创建时间: 2026-04-11

-- 风控规则表
CREATE TABLE IF NOT EXISTS risk_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL,
    rule_code VARCHAR(50) UNIQUE NOT NULL,
    risk_type VARCHAR(20) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    rule_config TEXT,
    description VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 风险名单表
CREATE TABLE IF NOT EXISTS risk_lists (
    id BIGSERIAL PRIMARY KEY,
    list_type VARCHAR(20) NOT NULL,
    target_value VARCHAR(200) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    reason VARCHAR(500),
    added_by INTEGER,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE(list_type, target_value)
);

-- 管理员操作日志表
CREATE TABLE IF NOT EXISTS admin_operation_logs (
    id BIGSERIAL PRIMARY KEY,
    admin_id INTEGER NOT NULL,
    admin_username VARCHAR(50),
    operation_type VARCHAR(50) NOT NULL,
    module VARCHAR(50),
    description TEXT,
    request_method VARCHAR(10),
    request_url VARCHAR(200),
    request_params TEXT,
    response_status INTEGER,
    client_ip VARCHAR(45),
    user_agent VARCHAR(500),
    execution_time INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 用户敏感操作日志表
CREATE TABLE IF NOT EXISTS user_sensitive_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID,
    operation_type VARCHAR(50) NOT NULL,
    description TEXT,
    client_ip VARCHAR(45),
    device_info VARCHAR(500),
    location VARCHAR(200),
    risk_level VARCHAR(20) DEFAULT 'LOW',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 备份任务表
CREATE TABLE IF NOT EXISTS backup_tasks (
    id BIGSERIAL PRIMARY KEY,
    task_name VARCHAR(100) NOT NULL,
    task_type VARCHAR(20) NOT NULL,
    backup_type VARCHAR(20) NOT NULL,
    cron_expression VARCHAR(50),
    storage_path VARCHAR(500),
    retention_days INTEGER DEFAULT 30,
    compress BOOLEAN DEFAULT true,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_executed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 备份记录表
CREATE TABLE IF NOT EXISTS backup_records (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT,
    task_name VARCHAR(100),
    backup_type VARCHAR(20),
    file_path VARCHAR(500),
    file_size BIGINT,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_risk_rules_status ON risk_rules(status);
CREATE INDEX IF NOT EXISTS idx_risk_rules_type ON risk_rules(risk_type);
CREATE INDEX IF NOT EXISTS idx_risk_lists_type ON risk_lists(list_type);
CREATE INDEX IF NOT EXISTS idx_risk_lists_created ON risk_lists(created_at);
CREATE INDEX IF NOT EXISTS idx_admin_logs_admin ON admin_operation_logs(admin_id);
CREATE INDEX IF NOT EXISTS idx_admin_logs_created ON admin_operation_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_user_logs_user ON user_sensitive_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_user_logs_created ON user_sensitive_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_user_logs_risk ON user_sensitive_logs(risk_level);
CREATE INDEX IF NOT EXISTS idx_backup_tasks_status ON backup_tasks(status);
CREATE INDEX IF NOT EXISTS idx_backup_records_task ON backup_records(task_id);
CREATE INDEX IF NOT EXISTS idx_backup_records_status ON backup_records(status);
CREATE INDEX IF NOT EXISTS idx_backup_records_created ON backup_records(created_at);

-- 初始化默认风控规则
INSERT INTO risk_rules (rule_name, rule_code, risk_type, risk_level, description, status, sort_order, created_at, updated_at) VALUES
('异常登录检测', 'LOGIN_ANOMALY', 'LOGIN', 'HIGH', '检测异地、异常时间登录', 'ACTIVE', 1, NOW(), NOW()),
('频繁发布检测', 'POST_FREQUENCY', 'POST', 'MEDIUM', '检测短时间内频繁发布', 'ACTIVE', 2, NOW(), NOW()),
('异常互动检测', 'INTERACTION_ANOMALY', 'INTERACTION', 'MEDIUM', '检测异常点赞、关注行为', 'ACTIVE', 3, NOW(), NOW())
ON CONFLICT (rule_code) DO NOTHING;
