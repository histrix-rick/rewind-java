-- ============================================
-- V3__Add_content_reports.sql
-- 添加内容举报管理表
-- 创建时间: 2026-04-11
-- ============================================

-- ============================================
-- 1. 内容举报表
-- ============================================
CREATE TABLE IF NOT EXISTS content_reports (
    id BIGSERIAL PRIMARY KEY,
    reporter_id UUID NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id UUID NOT NULL,
    report_category VARCHAR(50) NOT NULL,
    report_reason TEXT NOT NULL,
    description TEXT,
    evidence_images TEXT[],
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    handled_by BIGINT,
    handled_at TIMESTAMP WITH TIME ZONE,
    handle_result TEXT,
    handle_remark TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_report_handler FOREIGN KEY (handled_by) REFERENCES sys_admins(id) ON DELETE SET NULL,
    CONSTRAINT chk_report_target_type CHECK (target_type IN ('DAYDREAM', 'COMMENT', 'USER')),
    CONSTRAINT chk_report_category CHECK (report_category IN ('SPAM', 'PORN', 'VIOLENCE', 'HATE_SPEECH', 'IMPERSONATION', 'COPYRIGHT', 'PRIVACY', 'FRAUD', 'OTHER')),
    CONSTRAINT chk_report_status CHECK (status IN ('PENDING', 'PROCESSING', 'RESOLVED', 'DISMISSED'))
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_content_reports_reporter_id ON content_reports(reporter_id);
CREATE INDEX IF NOT EXISTS idx_content_reports_target ON content_reports(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_content_reports_status ON content_reports(status);
CREATE INDEX IF NOT EXISTS idx_content_reports_created_at ON content_reports(created_at);
CREATE INDEX IF NOT EXISTS idx_content_reports_handled_by ON content_reports(handled_by);

-- ============================================
-- 2. 内容举报操作记录表
-- ============================================
CREATE TABLE IF NOT EXISTS content_report_actions (
    id BIGSERIAL PRIMARY KEY,
    report_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    action_details TEXT,
    operator_id BIGINT,
    operator_name VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_action_report FOREIGN KEY (report_id) REFERENCES content_reports(id) ON DELETE CASCADE,
    CONSTRAINT fk_action_operator FOREIGN KEY (operator_id) REFERENCES sys_admins(id) ON DELETE SET NULL,
    CONSTRAINT chk_action_type CHECK (action_type IN ('CREATE', 'ASSIGN', 'PROCESS', 'RESOLVE', 'DISMISS', 'ESCALATE'))
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_report_actions_report_id ON content_report_actions(report_id);
CREATE INDEX IF NOT EXISTS idx_report_actions_operator_id ON content_report_actions(operator_id);
CREATE INDEX IF NOT EXISTS idx_report_actions_created_at ON content_report_actions(created_at);

-- ============================================
-- 数据初始化完成
-- ============================================
