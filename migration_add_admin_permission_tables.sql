-- 模块十一：管理员与权限 - 数据库迁移脚本
-- 创建角色表、权限表、关联表及初始化数据

-- 角色表
CREATE TABLE IF NOT EXISTS sys_roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    role_code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(500),
    sort_order INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permissions (
    id BIGSERIAL PRIMARY KEY,
    permission_name VARCHAR(100) NOT NULL,
    permission_code VARCHAR(100) UNIQUE NOT NULL,
    permission_module VARCHAR(50) NOT NULL,
    permission_type VARCHAR(20) NOT NULL,
    parent_id BIGINT,
    description VARCHAR(500),
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 管理员-角色关联表
CREATE TABLE IF NOT EXISTS sys_admin_roles (
    id BIGSERIAL PRIMARY KEY,
    admin_id INTEGER NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(admin_id, role_id)
);

-- 角色-权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, permission_id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_sys_roles_status ON sys_roles(status);
CREATE INDEX IF NOT EXISTS idx_sys_permissions_module ON sys_permissions(permission_module);
CREATE INDEX IF NOT EXISTS idx_sys_permissions_parent ON sys_permissions(parent_id);
CREATE INDEX IF NOT EXISTS idx_sys_admin_roles_admin ON sys_admin_roles(admin_id);
CREATE INDEX IF NOT EXISTS idx_sys_admin_roles_role ON sys_admin_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_sys_role_permissions_role ON sys_role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_sys_role_permissions_permission ON sys_role_permissions(permission_id);

-- 初始化默认角色
INSERT INTO sys_roles (role_name, role_code, description, sort_order, status, created_at, updated_at) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有所有权限', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('系统管理员', 'SYSTEM_ADMIN', '系统管理权限', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('内容管理员', 'CONTENT_ADMIN', '内容管理权限', 3, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (role_code) DO NOTHING;

-- 初始化默认权限
INSERT INTO sys_permissions (permission_name, permission_code, permission_module, permission_type, parent_id, sort_order, created_at, updated_at) VALUES
('用户管理', 'user:view', 'USER', 'MENU', NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('用户列表', 'user:list', 'USER', 'BUTTON', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('用户禁用', 'user:ban', 'USER', 'BUTTON', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('梦境管理', 'dream:view', 'DREAM', 'MENU', NULL, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('梦境列表', 'dream:list', 'DREAM', 'BUTTON', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('梦境审核', 'dream:audit', 'DREAM', 'BUTTON', 4, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('社交管理', 'social:view', 'SOCIAL', 'MENU', NULL, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('钱包管理', 'wallet:view', 'WALLET', 'MENU', NULL, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('评论管理', 'comment:view', 'COMMENT', 'MENU', NULL, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('云存储管理', 'cloud_storage:view', 'CLOUD_STORAGE', 'MENU', NULL, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('财务管理', 'finance:view', 'FINANCE', 'MENU', NULL, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('通知管理', 'notification:view', 'NOTIFICATION', 'MENU', NULL, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('数据分析', 'analysis:view', 'ANALYSIS', 'MENU', NULL, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('系统配置', 'sysconfig:view', 'SYSCONFIG', 'MENU', NULL, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('安全与风控', 'security:view', 'SECURITY', 'MENU', NULL, 11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('管理员管理', 'admin:view', 'ADMIN', 'MENU', NULL, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('管理员列表', 'admin:list', 'ADMIN', 'BUTTON', 16, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('创建管理员', 'admin:create', 'ADMIN', 'BUTTON', 16, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('角色管理', 'role:view', 'ROLE', 'MENU', NULL, 13, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('角色列表', 'role:list', 'ROLE', 'BUTTON', 19, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('创建角色', 'role:create', 'ROLE', 'BUTTON', 19, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('权限管理', 'permission:view', 'PERMISSION', 'MENU', NULL, 14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (permission_code) DO NOTHING;
