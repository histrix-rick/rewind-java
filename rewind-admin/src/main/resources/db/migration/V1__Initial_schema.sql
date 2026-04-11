-- ============================================
-- Rewind.ai 后台管理系统 - 数据库初始化脚本
-- 数据库: PostgreSQL
-- 版本: V1
-- 创建日期: 2026-04-11
-- ============================================

-- ============================================
-- 模块一: 用户管理系统表
-- ============================================

-- 用户主体表
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar_url TEXT,
    gender INTEGER NOT NULL DEFAULT 0,
    birth_date DATE NOT NULL,
    phone_number VARCHAR(20) UNIQUE,
    email VARCHAR(100) UNIQUE,
    real_name VARCHAR(50),
    id_card_no VARCHAR(18),
    status INTEGER NOT NULL DEFAULT 1,
    register_ip VARCHAR(45),
    register_device_id VARCHAR(100),
    last_login_time TIMESTAMP WITH TIME ZONE,
    last_login_ip VARCHAR(45),
    last_login_device VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone_number);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);

-- 用户登录日志表
CREATE TABLE IF NOT EXISTS user_login_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    login_ip VARCHAR(45),
    login_device VARCHAR(100),
    login_location VARCHAR(200),
    user_agent TEXT,
    login_status INTEGER NOT NULL DEFAULT 1,
    fail_reason VARCHAR(200),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_login_logs_user_id ON user_login_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_user_login_logs_created_at ON user_login_logs(created_at);

-- 用户属性表
CREATE TABLE IF NOT EXISTS user_attributes (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID UNIQUE NOT NULL,
    bio TEXT,
    website VARCHAR(200),
    location VARCHAR(200),
    is_verified BOOLEAN NOT NULL DEFAULT false,
    is_creator BOOLEAN NOT NULL DEFAULT false,
    is_official BOOLEAN NOT NULL DEFAULT false,
    dream_count INTEGER NOT NULL DEFAULT 0,
    follower_count INTEGER NOT NULL DEFAULT 0,
    following_count INTEGER NOT NULL DEFAULT 0,
    like_count INTEGER NOT NULL DEFAULT 0,
    comment_count INTEGER NOT NULL DEFAULT 0,
    total_liked_count INTEGER NOT NULL DEFAULT 0,
    total_commented_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_attributes_user_id ON user_attributes(user_id);

-- ============================================
-- 模块二: 管理员与权限表
-- ============================================

-- 管理员表
CREATE TABLE IF NOT EXISTS sys_admins (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar TEXT,
    phone_number VARCHAR(20) UNIQUE,
    email VARCHAR(100) UNIQUE,
    status INTEGER NOT NULL DEFAULT 1,
    is_default_password BOOLEAN NOT NULL DEFAULT true,
    last_login_at TIMESTAMP WITH TIME ZONE,
    last_pwd_change_at TIMESTAMP WITH TIME ZONE,
    created_by_admin_id INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_admins_username ON sys_admins(username);
CREATE INDEX IF NOT EXISTS idx_sys_admins_phone ON sys_admins(phone_number);
CREATE INDEX IF NOT EXISTS idx_sys_admins_email ON sys_admins(email);
CREATE INDEX IF NOT EXISTS idx_sys_admins_status ON sys_admins(status);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    role_code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(500),
    sort_order INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_roles_status ON sys_roles(status);

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permissions (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT,
    permission_name VARCHAR(100) NOT NULL,
    permission_code VARCHAR(100) UNIQUE NOT NULL,
    permission_module VARCHAR(50),
    permission_type VARCHAR(20) NOT NULL,
    description VARCHAR(500),
    sort_order INTEGER NOT NULL DEFAULT 0,
    icon VARCHAR(100),
    route_path VARCHAR(200),
    component_path VARCHAR(200),
    is_visible BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_permissions_parent_id ON sys_permissions(parent_id);
CREATE INDEX IF NOT EXISTS idx_sys_permissions_module ON sys_permissions(permission_module);
CREATE INDEX IF NOT EXISTS idx_sys_permissions_code ON sys_permissions(permission_code);

-- 管理员角色关联表
CREATE TABLE IF NOT EXISTS sys_admin_roles (
    id BIGSERIAL PRIMARY KEY,
    admin_id INTEGER NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(admin_id, role_id)
);

CREATE INDEX IF NOT EXISTS idx_sys_admin_roles_admin_id ON sys_admin_roles(admin_id);
CREATE INDEX IF NOT EXISTS idx_sys_admin_roles_role_id ON sys_admin_roles(role_id);

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, permission_id)
);

CREATE INDEX IF NOT EXISTS idx_sys_role_permissions_role_id ON sys_role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_sys_role_permissions_permission_id ON sys_role_permissions(permission_id);

-- 管理员操作日志表
CREATE TABLE IF NOT EXISTS admin_operation_logs (
    id BIGSERIAL PRIMARY KEY,
    admin_id INTEGER,
    admin_username VARCHAR(50),
    operation_type VARCHAR(50) NOT NULL,
    module VARCHAR(50),
    description TEXT,
    request_method VARCHAR(10),
    request_url VARCHAR(500),
    request_params TEXT,
    response_result TEXT,
    client_ip VARCHAR(45),
    user_agent TEXT,
    execution_time INTEGER,
    is_success BOOLEAN NOT NULL DEFAULT true,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_admin_operation_logs_admin_id ON admin_operation_logs(admin_id);
CREATE INDEX IF NOT EXISTS idx_admin_operation_logs_module ON admin_operation_logs(module);
CREATE INDEX IF NOT EXISTS idx_admin_operation_logs_created_at ON admin_operation_logs(created_at);

-- 验证码表
CREATE TABLE IF NOT EXISTS sys_verification_codes (
    id BIGSERIAL PRIMARY KEY,
    receiver VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL,
    type VARCHAR(20) NOT NULL,
    expire_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_used BOOLEAN NOT NULL DEFAULT false,
    used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_verification_codes_receiver ON sys_verification_codes(receiver);
CREATE INDEX IF NOT EXISTS idx_sys_verification_codes_type ON sys_verification_codes(type);
CREATE INDEX IF NOT EXISTS idx_sys_verification_codes_expire_at ON sys_verification_codes(expire_at);

-- ============================================
-- 模块三: 内容管理系统表
-- ============================================

-- 白日梦表
CREATE TABLE IF NOT EXISTS daydreams (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    cover_url TEXT,
    is_public BOOLEAN NOT NULL DEFAULT true,
    is_featured BOOLEAN NOT NULL DEFAULT false,
    is_pinned BOOLEAN NOT NULL DEFAULT false,
    featured_at TIMESTAMP WITH TIME ZONE,
    pinned_at TIMESTAMP WITH TIME ZONE,
    view_count INTEGER NOT NULL DEFAULT 0,
    like_count INTEGER NOT NULL DEFAULT 0,
    comment_count INTEGER NOT NULL DEFAULT 0,
    share_count INTEGER NOT NULL DEFAULT 0,
    audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    audit_reason VARCHAR(500),
    audited_by_admin_id INTEGER,
    audited_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_daydreams_user_id ON daydreams(user_id);
CREATE INDEX IF NOT EXISTS idx_daydreams_audit_status ON daydreams(audit_status);
CREATE INDEX IF NOT EXISTS idx_daydreams_is_public ON daydreams(is_public);
CREATE INDEX IF NOT EXISTS idx_daydreams_is_featured ON daydreams(is_featured);
CREATE INDEX IF NOT EXISTS idx_daydreams_created_at ON daydreams(created_at);

-- 时间轴节点表
CREATE TABLE IF NOT EXISTS timeline_nodes (
    id UUID PRIMARY KEY,
    daydream_id UUID NOT NULL,
    parent_node_id UUID,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    node_order INTEGER NOT NULL DEFAULT 0,
    is_branch BOOLEAN NOT NULL DEFAULT false,
    branch_condition VARCHAR(500),
    view_count INTEGER NOT NULL DEFAULT 0,
    like_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_timeline_nodes_daydream_id ON timeline_nodes(daydream_id);
CREATE INDEX IF NOT EXISTS idx_timeline_nodes_parent_node_id ON timeline_nodes(parent_node_id);

-- 梦境分支表
CREATE TABLE IF NOT EXISTS dream_branches (
    id UUID PRIMARY KEY,
    daydream_id UUID NOT NULL,
    from_node_id UUID NOT NULL,
    to_node_id UUID NOT NULL,
    branch_title VARCHAR(200),
    branch_condition TEXT,
    branch_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dream_branches_daydream_id ON dream_branches(daydream_id);
CREATE INDEX IF NOT EXISTS idx_dream_branches_from_node ON dream_branches(from_node_id);

-- 梦境资产表
CREATE TABLE IF NOT EXISTS dream_assets (
    id UUID PRIMARY KEY,
    daydream_id UUID NOT NULL,
    asset_type VARCHAR(20) NOT NULL,
    asset_url TEXT NOT NULL,
    asset_name VARCHAR(200),
    description TEXT,
    file_size BIGINT,
    mime_type VARCHAR(100),
    asset_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dream_assets_daydream_id ON dream_assets(daydream_id);

-- 人物关系表
CREATE TABLE IF NOT EXISTS dream_relationships (
    id UUID PRIMARY KEY,
    daydream_id UUID NOT NULL,
    relationship_type_id UUID NOT NULL,
    from_identity_id UUID NOT NULL,
    to_identity_id UUID NOT NULL,
    relationship_description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dream_relationships_daydream_id ON dream_relationships(daydream_id);

-- 关系类型表
CREATE TABLE IF NOT EXISTS relationship_types (
    id UUID PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL,
    type_code VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    is_system BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 用户身份表
CREATE TABLE IF NOT EXISTS user_identities (
    id UUID PRIMARY KEY,
    daydream_id UUID NOT NULL,
    user_id UUID,
    identity_name VARCHAR(100) NOT NULL,
    avatar_url TEXT,
    description TEXT,
    is_player BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_identities_daydream_id ON user_identities(daydream_id);

-- 教育程度表
CREATE TABLE IF NOT EXISTS education_levels (
    id UUID PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL,
    level_code VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 梦境上下文表
CREATE TABLE IF NOT EXISTS dream_contexts (
    id UUID PRIMARY KEY,
    daydream_id UUID NOT NULL,
    context_type VARCHAR(50) NOT NULL,
    context_title VARCHAR(200),
    context_content TEXT NOT NULL,
    context_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dream_contexts_daydream_id ON dream_contexts(daydream_id);

-- 时间轴节点扩展表
CREATE TABLE IF NOT EXISTS timeline_node_extensions (
    id UUID PRIMARY KEY,
    node_id UUID NOT NULL,
    extension_key VARCHAR(50) NOT NULL,
    extension_value TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_timeline_node_extensions_node_id ON timeline_node_extensions(node_id);

-- 梦境点赞表
CREATE TABLE IF NOT EXISTS dream_likes (
    id UUID PRIMARY KEY,
    daydream_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(daydream_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_dream_likes_daydream_id ON dream_likes(daydream_id);
CREATE INDEX IF NOT EXISTS idx_dream_likes_user_id ON dream_likes(user_id);

-- 节点点赞表
CREATE TABLE IF NOT EXISTS node_likes (
    id UUID PRIMARY KEY,
    node_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(node_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_node_likes_node_id ON node_likes(node_id);
CREATE INDEX IF NOT EXISTS idx_node_likes_user_id ON node_likes(user_id);

-- 梦境评论表
CREATE TABLE IF NOT EXISTS dream_comments (
    id UUID PRIMARY KEY,
    daydream_id UUID NOT NULL,
    user_id UUID NOT NULL,
    parent_comment_id UUID,
    reply_to_user_id UUID,
    content TEXT NOT NULL,
    like_count INTEGER NOT NULL DEFAULT 0,
    audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dream_comments_daydream_id ON dream_comments(daydream_id);
CREATE INDEX IF NOT EXISTS idx_dream_comments_user_id ON dream_comments(user_id);
CREATE INDEX IF NOT EXISTS idx_dream_comments_parent_id ON dream_comments(parent_comment_id);
CREATE INDEX IF NOT EXISTS idx_dream_comments_audit_status ON dream_comments(audit_status);
CREATE INDEX IF NOT EXISTS idx_dream_comments_created_at ON dream_comments(created_at);

-- 用户关注表
CREATE TABLE IF NOT EXISTS user_follows (
    id UUID PRIMARY KEY,
    follower_id UUID NOT NULL,
    following_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(follower_id, following_id)
);

CREATE INDEX IF NOT EXISTS idx_user_follows_follower_id ON user_follows(follower_id);
CREATE INDEX IF NOT EXISTS idx_user_follows_following_id ON user_follows(following_id);

-- 梦境打赏表
CREATE TABLE IF NOT EXISTS dream_rewards (
    id UUID PRIMARY KEY,
    daydream_id UUID NOT NULL,
    sender_id UUID NOT NULL,
    receiver_id UUID NOT NULL,
    amount INTEGER NOT NULL,
    message VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dream_rewards_daydream_id ON dream_rewards(daydream_id);
CREATE INDEX IF NOT EXISTS idx_dream_rewards_sender_id ON dream_rewards(sender_id);
CREATE INDEX IF NOT EXISTS idx_dream_rewards_receiver_id ON dream_rewards(receiver_id);
CREATE INDEX IF NOT EXISTS idx_dream_rewards_created_at ON dream_rewards(created_at);

-- 梦境关注表
CREATE TABLE IF NOT EXISTS dream_follows (
    id UUID PRIMARY KEY,
    daydream_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(daydream_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_dream_follows_daydream_id ON dream_follows(daydream_id);
CREATE INDEX IF NOT EXISTS idx_dream_follows_user_id ON dream_follows(user_id);

-- 知识问题表
CREATE TABLE IF NOT EXISTS knowledge_questions (
    id UUID PRIMARY KEY,
    question_text TEXT NOT NULL,
    answer_text TEXT,
    question_category VARCHAR(100),
    difficulty_level INTEGER NOT NULL DEFAULT 1,
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_knowledge_questions_category ON knowledge_questions(question_category);
CREATE INDEX IF NOT EXISTS idx_knowledge_questions_is_active ON knowledge_questions(is_active);

-- ============================================
-- 模块四: 钱包与财务管理表
-- ============================================

-- 用户钱包表
CREATE TABLE IF NOT EXISTS user_wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID UNIQUE NOT NULL,
    balance INTEGER NOT NULL DEFAULT 0,
    total_earned INTEGER NOT NULL DEFAULT 0,
    total_spent INTEGER NOT NULL DEFAULT 0,
    is_frozen BOOLEAN NOT NULL DEFAULT false,
    frozen_reason VARCHAR(500),
    frozen_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_wallets_user_id ON user_wallets(user_id);
CREATE INDEX IF NOT EXISTS idx_user_wallets_is_frozen ON user_wallets(is_frozen);

-- 钱包交易记录表
CREATE TABLE IF NOT EXISTS wallet_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    description VARCHAR(500),
    reference_id VARCHAR(100),
    reference_type VARCHAR(50),
    related_user_id UUID,
    admin_operation_id INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_wallet_transactions_user_id ON wallet_transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_wallet_transactions_type ON wallet_transactions(transaction_type);
CREATE INDEX IF NOT EXISTS idx_wallet_transactions_created_at ON wallet_transactions(created_at);
CREATE INDEX IF NOT EXISTS idx_wallet_transactions_reference ON wallet_transactions(reference_id, reference_type);

-- 奖励配置表
CREATE TABLE IF NOT EXISTS reward_configs (
    id BIGSERIAL PRIMARY KEY,
    reward_type VARCHAR(50) UNIQUE NOT NULL,
    reward_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    reward_amount INTEGER NOT NULL DEFAULT 0,
    daily_limit INTEGER,
    total_limit INTEGER,
    min_level INTEGER,
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_reward_configs_type ON reward_configs(reward_type);
CREATE INDEX IF NOT EXISTS idx_reward_configs_is_active ON reward_configs(is_active);

-- 现金流量记录表
CREATE TABLE IF NOT EXISTS cash_flow_records (
    id UUID PRIMARY KEY,
    daydream_id UUID NOT NULL,
    node_id UUID,
    user_id UUID NOT NULL,
    flow_type VARCHAR(20) NOT NULL,
    amount INTEGER NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_cash_flow_records_daydream_id ON cash_flow_records(daydream_id);
CREATE INDEX IF NOT EXISTS idx_cash_flow_records_user_id ON cash_flow_records(user_id);
CREATE INDEX IF NOT EXISTS idx_cash_flow_records_created_at ON cash_flow_records(created_at);

-- ============================================
-- 模块五: 通知系统表
-- ============================================

-- 通知表
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    related_type VARCHAR(50),
    related_id VARCHAR(100),
    is_read BOOLEAN NOT NULL DEFAULT false,
    read_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(notification_type);
CREATE INDEX IF NOT EXISTS idx_notifications_is_read ON notifications(is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);

-- ============================================
-- 模块六: 系统配置表
-- ============================================

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_configs (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    config_name VARCHAR(200) NOT NULL,
    config_group VARCHAR(50),
    description VARCHAR(500),
    value_type VARCHAR(20) NOT NULL DEFAULT 'STRING',
    is_system BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_configs_key ON sys_configs(config_key);
CREATE INDEX IF NOT EXISTS idx_sys_configs_group ON sys_configs(config_group);

-- 敏感词表
CREATE TABLE IF NOT EXISTS sensitive_words (
    id BIGSERIAL PRIMARY KEY,
    word VARCHAR(200) NOT NULL,
    word_type VARCHAR(50) NOT NULL,
    replacement VARCHAR(200),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_by_admin_id INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sensitive_words_word ON sensitive_words(word);
CREATE INDEX IF NOT EXISTS idx_sensitive_words_type ON sensitive_words(word_type);
CREATE INDEX IF NOT EXISTS idx_sensitive_words_is_active ON sensitive_words(is_active);

-- ============================================
-- 模块七: 安全与风控表
-- ============================================

-- 风控规则表
CREATE TABLE IF NOT EXISTS risk_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL,
    rule_code VARCHAR(50) UNIQUE NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    rule_config TEXT NOT NULL,
    description VARCHAR(500),
    severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    is_active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_risk_rules_code ON risk_rules(rule_code);
CREATE INDEX IF NOT EXISTS idx_risk_rules_type ON risk_rules(rule_type);
CREATE INDEX IF NOT EXISTS idx_risk_rules_is_active ON risk_rules(is_active);

-- 风险名单表
CREATE TABLE IF NOT EXISTS risk_lists (
    id BIGSERIAL PRIMARY KEY,
    list_type VARCHAR(20) NOT NULL,
    entry_type VARCHAR(20) NOT NULL,
    entry_value VARCHAR(200) NOT NULL,
    reason VARCHAR(500),
    severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    added_by_admin_id INTEGER,
    expire_at TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(list_type, entry_type, entry_value)
);

CREATE INDEX IF NOT EXISTS idx_risk_lists_type ON risk_lists(list_type, entry_type);
CREATE INDEX IF NOT EXISTS idx_risk_lists_entry ON risk_lists(entry_value);
CREATE INDEX IF NOT EXISTS idx_risk_lists_is_active ON risk_lists(is_active);

-- 用户敏感操作日志表
CREATE TABLE IF NOT EXISTS user_sensitive_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    operation_detail TEXT,
    client_ip VARCHAR(45),
    device_info TEXT,
    location VARCHAR(200),
    risk_level VARCHAR(20) NOT NULL DEFAULT 'LOW',
    risk_reason VARCHAR(500),
    is_blocked BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_sensitive_logs_user_id ON user_sensitive_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_user_sensitive_logs_type ON user_sensitive_logs(operation_type);
CREATE INDEX IF NOT EXISTS idx_user_sensitive_logs_created_at ON user_sensitive_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_user_sensitive_logs_risk ON user_sensitive_logs(risk_level);

-- 备份任务表
CREATE TABLE IF NOT EXISTS backup_tasks (
    id BIGSERIAL PRIMARY KEY,
    task_name VARCHAR(100) NOT NULL,
    backup_type VARCHAR(50) NOT NULL,
    backup_config TEXT NOT NULL,
    cron_expression VARCHAR(100),
    retention_days INTEGER NOT NULL DEFAULT 30,
    is_active BOOLEAN NOT NULL DEFAULT true,
    last_run_at TIMESTAMP WITH TIME ZONE,
    next_run_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_backup_tasks_type ON backup_tasks(backup_type);
CREATE INDEX IF NOT EXISTS idx_backup_tasks_is_active ON backup_tasks(is_active);

-- 备份记录表
CREATE TABLE IF NOT EXISTS backup_records (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT,
    backup_name VARCHAR(200) NOT NULL,
    backup_type VARCHAR(50) NOT NULL,
    file_path TEXT NOT NULL,
    file_size BIGINT,
    backup_status VARCHAR(20) NOT NULL,
    error_message TEXT,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_backup_records_task_id ON backup_records(task_id);
CREATE INDEX IF NOT EXISTS idx_backup_records_type ON backup_records(backup_type);
CREATE INDEX IF NOT EXISTS idx_backup_records_status ON backup_records(backup_status);
CREATE INDEX IF NOT EXISTS idx_backup_records_created_at ON backup_records(created_at);

-- ============================================
-- 模块八: 云存储表
-- ============================================

-- 云存储配置表
CREATE TABLE IF NOT EXISTS storage_configs (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    provider VARCHAR(50) NOT NULL,
    access_endpoint VARCHAR(200) NOT NULL,
    custom_domain VARCHAR(200),
    access_key VARCHAR(200) NOT NULL,
    secret_key VARCHAR(200) NOT NULL,
    bucket_name VARCHAR(100) NOT NULL,
    path_prefix VARCHAR(200),
    is_https BOOLEAN NOT NULL DEFAULT true,
    bucket_access_type VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    region VARCHAR(100),
    is_default BOOLEAN NOT NULL DEFAULT false,
    remark VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_storage_configs_key ON storage_configs(config_key);
CREATE INDEX IF NOT EXISTS idx_storage_configs_provider ON storage_configs(provider);
CREATE INDEX IF NOT EXISTS idx_storage_configs_is_default ON storage_configs(is_default);

-- 文件记录表
CREATE TABLE IF NOT EXISTS file_records (
    id UUID PRIMARY KEY,
    file_name VARCHAR(500) NOT NULL,
    original_name VARCHAR(500),
    file_ext VARCHAR(50),
    file_size BIGINT,
    content_type VARCHAR(200),
    file_url TEXT NOT NULL,
    storage_provider VARCHAR(50) NOT NULL,
    storage_config_id BIGINT,
    uploader_type VARCHAR(50) NOT NULL,
    uploader_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_file_records_uploader ON file_records(uploader_type, uploader_id);
CREATE INDEX IF NOT EXISTS idx_file_records_provider ON file_records(storage_provider);
CREATE INDEX IF NOT EXISTS idx_file_records_created_at ON file_records(created_at);

-- ============================================
-- 模块九: 工单与客服表
-- ============================================

-- 工单表
CREATE TABLE IF NOT EXISTS tickets (
    id UUID PRIMARY KEY,
    ticket_no VARCHAR(50) UNIQUE NOT NULL,
    user_id UUID NOT NULL,
    category VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    assigned_admin_id INTEGER,
    assigned_at TIMESTAMP WITH TIME ZONE,
    resolved_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tickets_user_id ON tickets(user_id);
CREATE INDEX IF NOT EXISTS idx_tickets_status ON tickets(status);
CREATE INDEX IF NOT EXISTS idx_tickets_category ON tickets(category);
CREATE INDEX IF NOT EXISTS idx_tickets_priority ON tickets(priority);
CREATE INDEX IF NOT EXISTS idx_tickets_assigned_admin ON tickets(assigned_admin_id);
CREATE INDEX IF NOT EXISTS idx_tickets_created_at ON tickets(created_at);
CREATE INDEX IF NOT EXISTS idx_tickets_no ON tickets(ticket_no);

-- 工单回复表
CREATE TABLE IF NOT EXISTS ticket_replies (
    id UUID PRIMARY KEY,
    ticket_id UUID NOT NULL,
    reply_user_id UUID,
    reply_admin_id INTEGER,
    is_admin_reply BOOLEAN NOT NULL DEFAULT false,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ticket_replies_ticket_id ON ticket_replies(ticket_id);
CREATE INDEX IF NOT EXISTS idx_ticket_replies_created_at ON ticket_replies(created_at);

-- 用户反馈表
CREATE TABLE IF NOT EXISTS user_feedbacks (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    category_id BIGINT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    images TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    handler_admin_id INTEGER,
    handled_at TIMESTAMP WITH TIME ZONE,
    handling_remark TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_feedbacks_user_id ON user_feedbacks(user_id);
CREATE INDEX IF NOT EXISTS idx_user_feedbacks_status ON user_feedbacks(status);
CREATE INDEX IF NOT EXISTS idx_user_feedbacks_category ON user_feedbacks(category_id);
CREATE INDEX IF NOT EXISTS idx_user_feedbacks_created_at ON user_feedbacks(created_at);

-- 反馈分类表
CREATE TABLE IF NOT EXISTS feedback_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(500),
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_feedback_categories_code ON feedback_categories(code);
CREATE INDEX IF NOT EXISTS idx_feedback_categories_is_enabled ON feedback_categories(is_enabled);

-- 知识库表
CREATE TABLE IF NOT EXISTS knowledge_bases (
    id UUID PRIMARY KEY,
    category_id BIGINT,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500),
    content TEXT NOT NULL,
    view_count INTEGER NOT NULL DEFAULT 0,
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_published BOOLEAN NOT NULL DEFAULT false,
    published_at TIMESTAMP WITH TIME ZONE,
    creator_id INTEGER,
    creator_name VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_knowledge_bases_category ON knowledge_bases(category_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_bases_is_published ON knowledge_bases(is_published);
CREATE INDEX IF NOT EXISTS idx_knowledge_bases_created_at ON knowledge_bases(created_at);

-- 知识库分类表
CREATE TABLE IF NOT EXISTS knowledge_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(500),
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_knowledge_categories_code ON knowledge_categories(code);
CREATE INDEX IF NOT EXISTS idx_knowledge_categories_is_enabled ON knowledge_categories(is_enabled);

-- ============================================
-- AI裁判表
-- ============================================

-- AI裁判规则表
CREATE TABLE IF NOT EXISTS ai_judgment_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL,
    rule_code VARCHAR(50) UNIQUE NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    rule_prompt TEXT NOT NULL,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ai_judgment_rules_code ON ai_judgment_rules(rule_code);
CREATE INDEX IF NOT EXISTS idx_ai_judgment_rules_type ON ai_judgment_rules(rule_type);
CREATE INDEX IF NOT EXISTS idx_ai_judgment_rules_is_active ON ai_judgment_rules(is_active);

-- ============================================
-- 初始化完成
-- ============================================
