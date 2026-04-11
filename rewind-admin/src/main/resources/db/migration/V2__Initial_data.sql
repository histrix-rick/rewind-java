-- ============================================
-- V2__Initial_data.sql
-- 初始数据脚本
-- 创建时间: 2026-04-11
-- ============================================

-- ============================================
-- 1. 管理员与权限数据
-- ============================================

-- 插入默认超级管理员 (密码: admin123, 使用BCrypt加密)
INSERT INTO sys_admins (id, username, password, real_name, email, phone, avatar, status, is_super, last_login_at, last_login_ip, created_by, created_at, updated_by, updated_at, deleted)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '超级管理员', 'admin@rewind.ai', '13800138000', NULL, 'ACTIVE', true, NULL, NULL, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 插入默认角色
INSERT INTO sys_roles (id, name, code, description, status, data_scope, created_by, created_at, updated_by, updated_at, deleted)
VALUES
(1, '超级管理员', 'SUPER_ADMIN', '拥有所有权限', 'ACTIVE', 'ALL', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(2, '系统管理员', 'SYSTEM_ADMIN', '系统管理权限', 'ACTIVE', 'CUSTOM', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(3, '内容审核员', 'CONTENT_MODERATOR', '内容审核权限', 'ACTIVE', 'DEPT', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(4, '客服人员', 'CUSTOMER_SERVICE', '工单处理权限', 'ACTIVE', 'SELF', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 插入权限数据
INSERT INTO sys_permissions (id, parent_id, name, code, type, path, icon, sort_order, status, created_by, created_at, updated_by, updated_at, deleted)
VALUES
-- 系统管理模块
(1, 0, '系统管理', 'system', 'MENU', '/system', 'Setting', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(2, 1, '管理员管理', 'system:admin', 'MENU', '/system/admin', 'User', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(3, 2, '查看管理员', 'system:admin:list', 'BUTTON', NULL, NULL, 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(4, 2, '创建管理员', 'system:admin:create', 'BUTTON', NULL, NULL, 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(5, 2, '编辑管理员', 'system:admin:update', 'BUTTON', NULL, NULL, 3, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(6, 2, '删除管理员', 'system:admin:delete', 'BUTTON', NULL, NULL, 4, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 角色管理
(7, 1, '角色管理', 'system:role', 'MENU', '/system/role', 'UserFilled', 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(8, 7, '查看角色', 'system:role:list', 'BUTTON', NULL, NULL, 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(9, 7, '创建角色', 'system:role:create', 'BUTTON', NULL, NULL, 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(10, 7, '编辑角色', 'system:role:update', 'BUTTON', NULL, NULL, 3, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(11, 7, '删除角色', 'system:role:delete', 'BUTTON', NULL, NULL, 4, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 用户管理
(12, 0, '用户管理', 'user', 'MENU', '/user', 'User', 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(13, 12, '用户列表', 'user:list', 'MENU', '/user/list', 'List', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(14, 13, '查看用户', 'user:list:view', 'BUTTON', NULL, NULL, 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(15, 13, '编辑用户', 'user:list:update', 'BUTTON', NULL, NULL, 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(16, 13, '禁用用户', 'user:list:disable', 'BUTTON', NULL, NULL, 3, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 内容管理
(17, 0, '内容管理', 'content', 'MENU', '/content', 'Document', 3, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(18, 17, '梦境内容', 'content:dream', 'MENU', '/content/dream', 'Document', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(19, 18, '查看梦境', 'content:dream:list', 'BUTTON', NULL, NULL, 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(20, 18, '审核梦境', 'content:dream:audit', 'BUTTON', NULL, NULL, 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(21, 18, '删除梦境', 'content:dream:delete', 'BUTTON', NULL, NULL, 3, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 评论管理
(22, 17, '评论管理', 'content:comment', 'MENU', '/content/comment', 'ChatDotRound', 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(23, 22, '查看评论', 'content:comment:list', 'BUTTON', NULL, NULL, 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(24, 22, '审核评论', 'content:comment:audit', 'BUTTON', NULL, NULL, 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 社交互动
(25, 0, '社交互动', 'social', 'MENU', '/social', 'Connection', 4, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(26, 25, '关注管理', 'social:follow', 'MENU', '/social/follow', 'UserFilled', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(27, 25, '打赏管理', 'social:reward', 'MENU', '/social/reward', 'Money', 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 钱包管理
(28, 0, '钱包管理', 'wallet', 'MENU', '/wallet', 'Wallet', 5, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(29, 28, '钱包列表', 'wallet:list', 'MENU', '/wallet/list', 'List', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(30, 28, '交易记录', 'wallet:transaction', 'MENU', '/wallet/transaction', 'Tickets', 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 财务管理
(31, 0, '财务管理', 'finance', 'MENU', '/finance', 'Money', 6, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(32, 31, '打赏配置', 'finance:reward', 'MENU', '/finance/reward', 'Setting', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(33, 31, '财务报表', 'finance:report', 'MENU', '/finance/report', 'DataLine', 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 通知管理
(34, 0, '通知管理', 'notification', 'MENU', '/notification', 'Bell', 7, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 数据统计
(35, 0, '数据统计', 'analysis', 'MENU', '/analysis', 'DataAnalysis', 8, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 系统配置
(36, 0, '系统配置', 'sysconfig', 'MENU', '/sysconfig', 'Setting', 9, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(37, 36, '基础配置', 'sysconfig:basic', 'MENU', '/sysconfig/basic', 'Document', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(38, 36, '内容配置', 'sysconfig:content', 'MENU', '/sysconfig/content', 'Document', 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(39, 36, '社交配置', 'sysconfig:social', 'MENU', '/sysconfig/social', 'Document', 3, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(40, 36, '财务配置', 'sysconfig:finance', 'MENU', '/sysconfig/finance', 'Document', 4, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(41, 36, '推送配置', 'sysconfig:push', 'MENU', '/sysconfig/push', 'Document', 5, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 安全与风控
(42, 0, '安全风控', 'security', 'MENU', '/security', 'Shield', 10, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(43, 42, '风控规则', 'security:riskrule', 'MENU', '/security/riskrule', 'Document', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(44, 42, '风控名单', 'security:risklist', 'MENU', '/security/risklist', 'Document', 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(45, 42, '审计日志', 'security:audit', 'MENU', '/security/audit', 'Document', 3, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(46, 42, '数据备份', 'security:backup', 'MENU', '/security/backup', 'Document', 4, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 工单与客服
(47, 0, '工单客服', 'ticket', 'MENU', '/ticket', 'Service', 11, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(48, 47, '工单管理', 'ticket:list', 'MENU', '/ticket/list', 'Tickets', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(49, 47, '反馈管理', 'ticket:feedback', 'MENU', '/ticket/feedback', 'ChatDotRound', 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(50, 47, '知识库', 'ticket:knowledge', 'MENU', '/ticket/knowledge', 'ReadingBook', 3, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 云存储
(51, 0, '云存储', 'storage', 'MENU', '/storage', 'Folder', 12, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(52, 51, '存储配置', 'storage:config', 'MENU', '/storage/config', 'Setting', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(53, 51, '文件管理', 'storage:file', 'MENU', '/storage/file', 'Folder', 2, 'ACTIVE', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 为超级管理员分配角色
INSERT INTO sys_admin_roles (admin_id, role_id, created_by, created_at, updated_by, updated_at, deleted)
SELECT 1, id, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false FROM sys_roles WHERE code = 'SUPER_ADMIN'
ON CONFLICT DO NOTHING;

-- 为超级管理员角色分配所有权限
INSERT INTO sys_role_permissions (role_id, permission_id, created_by, created_at, updated_by, updated_at, deleted)
SELECT r.id, p.id, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false
FROM sys_roles r, sys_permissions p
WHERE r.code = 'SUPER_ADMIN'
ON CONFLICT DO NOTHING;

-- ============================================
-- 2. 系统配置数据
-- ============================================

INSERT INTO sys_configs (id, config_key, config_value, config_type, description, sort_order, is_system, created_by, created_at, updated_by, updated_at, deleted)
VALUES
-- 基础配置
(1, 'site.name', 'Rewind.ai', 'STRING', '网站名称', 1, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(2, 'site.description', '记录梦境，分享故事', 'TEXT', '网站描述', 2, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(3, 'site.logo', '', 'STRING', '网站Logo', 3, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(4, 'site.icp', '', 'STRING', 'ICP备案号', 4, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(5, 'site.status', 'ONLINE', 'ENUM', '站点状态: ONLINE-正常, MAINTENANCE-维护中, CLOSED-关闭', 5, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 内容配置
(6, 'content.dream.max_length', '10000', 'NUMBER', '梦境内容最大长度', 1, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(7, 'content.dream.min_length', '10', 'NUMBER', '梦境内容最小长度', 2, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(8, 'content.image.max_count', '9', 'NUMBER', '单条梦境最多图片数', 3, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(9, 'content.auto_audit', 'false', 'BOOLEAN', '是否启用自动审核', 4, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(10, 'content.sensitive_check', 'true', 'BOOLEAN', '是否启用敏感词检查', 5, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 社交配置
(11, 'social.follow.max_count', '1000', 'NUMBER', '每人最多关注数', 1, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(12, 'social.reward.enabled', 'true', 'BOOLEAN', '是否启用打赏功能', 2, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(13, 'social.reward.min_amount', '1', 'NUMBER', '最小打赏金额(分)', 3, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(14, 'social.reward.max_amount', '10000', 'NUMBER', '最大打赏金额(分)', 4, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(15, 'social.reward.platform_fee', '10', 'NUMBER', '平台服务费比例(%)', 5, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 财务配置
(16, 'finance.withdraw.enabled', 'true', 'BOOLEAN', '是否启用提现功能', 1, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(17, 'finance.withdraw.min_amount', '100', 'NUMBER', '最小提现金额(分)', 2, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(18, 'finance.withdraw.max_amount', '100000', 'NUMBER', '单日最大提现金额(分)', 3, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(19, 'finance.withdraw.fee', '1', 'NUMBER', '提现手续费比例(%)', 4, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(20, 'finance.deposit.enabled', 'true', 'BOOLEAN', '是否启用充值功能', 5, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),

-- 推送配置
(21, 'push.sms.enabled', 'false', 'BOOLEAN', '是否启用短信推送', 1, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(22, 'push.wechat.enabled', 'false', 'BOOLEAN', '是否启用微信推送', 2, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(23, 'push.app.enabled', 'true', 'BOOLEAN', '是否启用APP推送', 3, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(24, 'push.email.enabled', 'false', 'BOOLEAN', '是否启用邮件推送', 4, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 3. 敏感词数据
-- ============================================

INSERT INTO sensitive_words (id, word, category, level, source, hit_count, is_active, created_by, created_at, updated_by, updated_at, deleted)
VALUES
(1, '赌博', 'ILLEGAL', 'HIGH', 'SYSTEM', 0, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(2, '诈骗', 'ILLEGAL', 'HIGH', 'SYSTEM', 0, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(3, '色情', 'PORN', 'HIGH', 'SYSTEM', 0, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(4, '暴力', 'VIOLENCE', 'MEDIUM', 'SYSTEM', 0, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(5, '反动', 'POLITICAL', 'HIGH', 'SYSTEM', 0, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 4. 打赏配置数据
-- ============================================

INSERT INTO reward_configs (id, gift_name, gift_icon, coin_cost, sort_order, is_active, created_by, created_at, updated_by, updated_at, deleted)
VALUES
(1, '鲜花', '🌸', 10, 1, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(2, '掌声', '👏', 50, 2, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(3, '蛋糕', '🎂', 100, 3, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(4, '火箭', '🚀', 500, 4, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(5, '皇冠', '👑', 1000, 5, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 5. 反馈分类数据
-- ============================================

INSERT INTO feedback_categories (id, name, code, description, sort_order, is_active, created_by, created_at, updated_by, updated_at, deleted)
VALUES
(1, '功能问题', 'FUNCTION', '功能使用相关问题', 1, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(2, 'Bug反馈', 'BUG', '程序错误反馈', 2, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(3, '建议意见', 'SUGGESTION', '产品建议和意见', 3, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(4, '投诉举报', 'COMPLAINT', '投诉和举报内容', 4, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(5, '其他问题', 'OTHER', '其他类型问题', 5, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 6. 知识分类数据
-- ============================================

INSERT INTO knowledge_categories (id, name, code, description, parent_id, sort_order, is_active, created_by, created_at, updated_by, updated_at, deleted)
VALUES
(1, '账号相关', 'ACCOUNT', '账号注册、登录、安全等问题', NULL, 1, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(2, '内容发布', 'CONTENT', '梦境发布、编辑、删除等', NULL, 2, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(3, '社交互动', 'SOCIAL', '关注、点赞、评论、打赏等', NULL, 3, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(4, '钱包相关', 'WALLET', '充值、提现、交易记录等', NULL, 4, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(5, '其他问题', 'OTHER', '其他常见问题', NULL, 5, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 7. 知识库数据
-- ============================================

INSERT INTO knowledge_bases (id, category_id, title, content, summary, sort_order, view_count, is_active, created_by, created_at, updated_by, updated_at, deleted)
VALUES
(1, 1, '如何注册账号？', '## 账号注册步骤\n\n1. 打开Rewind.ai App\n2. 点击"注册"按钮\n3. 输入手机号获取验证码\n4. 设置登录密码\n5. 完成注册', '详细介绍账号注册的步骤流程', 1, 0, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(2, 1, '忘记密码怎么办？', '## 密码找回流程\n\n1. 点击登录页的"忘记密码"\n2. 输入绑定的手机号\n3. 输入验证码\n4. 设置新密码\n5. 完成密码重置', '忘记密码时的处理方法', 2, 0, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(3, 2, '如何发布梦境？', '## 发布梦境步骤\n\n1. 点击首页"+"按钮\n2. 选择"发布梦境"\n3. 填写梦境标题和内容\n4. 可选择添加图片\n5. 设置可见性\n6. 点击发布', '介绍如何发布梦境内容', 1, 0, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(4, 3, '如何打赏作者？', '## 打赏操作步骤\n\n1. 进入梦境详情页\n2. 点击底部"打赏"按钮\n3. 选择打赏礼物\n4. 确认支付\n5. 完成打赏', '介绍如何给喜欢的内容打赏', 1, 0, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(5, 4, '如何提现？', '## 提现操作指南\n\n1. 进入"我的钱包"\n2. 点击"提现"\n3. 选择提现方式\n4. 输入提现金额\n5. 确认提现信息\n6. 提交申请', '介绍钱包余额提现流程', 1, 0, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 8. 风控规则数据
-- ============================================

INSERT INTO risk_rules (id, rule_name, rule_code, rule_type, conditions, action, level, is_enabled, sort_order, created_by, created_at, updated_by, updated_at, deleted)
VALUES
(1, '登录频率限制', 'LOGIN_FREQUENCY', 'LOGIN', '{"max_attempts": 5, "time_window": 3600}', 'BLOCK', 'HIGH', true, 1, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(2, '异常IP检测', 'ABNORMAL_IP', 'LOGIN', '{"country_check": true, "proxy_check": true}', 'ALERT', 'MEDIUM', true, 2, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(3, '高频发布检测', 'POST_FREQUENCY', 'CONTENT', '{"max_posts": 10, "time_window": 3600}', 'BLOCK', 'MEDIUM', true, 3, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(4, '敏感词检测', 'SENSITIVE_WORD', 'CONTENT', '{}', 'REVIEW', 'HIGH', true, 4, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(5, '大额交易监控', 'LARGE_TRANSACTION', 'FINANCE', '{"threshold": 100000}', 'REVIEW', 'MEDIUM', true, 5, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 9. 云存储配置数据
-- ============================================

INSERT INTO storage_configs (id, provider, config_name, config_json, is_default, is_active, created_by, created_at, updated_by, updated_at, deleted)
VALUES
(1, 'LOCAL', '本地存储', '{"basePath": "/data/uploads", "urlPrefix": "/uploads"}', true, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(2, 'OSS', '阿里云OSS', '{"endpoint": "", "accessKeyId": "", "accessKeySecret": "", "bucketName": ""}', false, false, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(3, 'COS', '腾讯云COS', '{"region": "", "secretId": "", "secretKey": "", "bucket": ""}', false, false, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false),
(4, 'QINIU', '七牛云', '{"accessKey": "", "secretKey": "", "bucket": "", "domain": ""}', false, false, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 数据初始化完成
-- ============================================
