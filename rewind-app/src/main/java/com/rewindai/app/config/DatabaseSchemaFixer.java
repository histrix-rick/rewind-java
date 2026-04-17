package com.rewindai.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据库表结构修复器
 * 检测并修复ticket和user_feedback表的user_id字段类型
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Component
public class DatabaseSchemaFixer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("开始检测并修复数据库表结构...");
        log.info("========================================");

        try {
            fixTicketTable();
            fixUserFeedbackTable();
            log.info("========================================");
            log.info("数据库表结构修复完成！");
            log.info("========================================");
        } catch (Exception e) {
            log.error("数据库表结构修复失败", e);
        }
    }

    private void fixTicketTable() {
        log.info("检测ticket表结构...");

        // 检查ticket表是否存在
        boolean tableExists = checkTableExists("ticket");
        if (!tableExists) {
            log.info("ticket表不存在，将创建新表");
            createTicketTables();
            return;
        }

        // 检查user_id字段类型
        String userIdType = getColumnType("ticket", "user_id");
        log.info("ticket表user_id字段类型: {}", userIdType);

        if ("bigint".equalsIgnoreCase(userIdType) || "bigserial".equalsIgnoreCase(userIdType)) {
            log.warn("ticket表user_id字段类型为bigint，需要重建表");
            rebuildTicketTables();
        } else if ("uuid".equalsIgnoreCase(userIdType)) {
            log.info("ticket表user_id字段类型已经是UUID，无需修改");
        } else {
            log.warn("ticket表user_id字段类型为{}，不确定是否需要修改", userIdType);
        }
    }

    private void fixUserFeedbackTable() {
        log.info("检测user_feedback表结构...");

        // 检查user_feedback表是否存在
        boolean tableExists = checkTableExists("user_feedback");
        if (!tableExists) {
            log.info("user_feedback表不存在，将创建新表");
            // ticket表重建时会一起创建user_feedback表
            return;
        }

        // 检查user_id字段类型
        String userIdType = getColumnType("user_feedback", "user_id");
        log.info("user_feedback表user_id字段类型: {}", userIdType);

        if ("bigint".equalsIgnoreCase(userIdType) || "bigserial".equalsIgnoreCase(userIdType)) {
            log.warn("user_feedback表user_id字段类型为bigint，需要重建表");
            // ticket表重建时会一起重建user_feedback表
            rebuildTicketTables();
        } else if ("uuid".equalsIgnoreCase(userIdType)) {
            log.info("user_feedback表user_id字段类型已经是UUID，无需修改");
        } else {
            log.warn("user_feedback表user_id字段类型为{}，不确定是否需要修改", userIdType);
        }
    }

    private boolean checkTableExists(String tableName) {
        String sql = "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, tableName);
        return Boolean.TRUE.equals(exists);
    }

    private String getColumnType(String tableName, String columnName) {
        String sql = "SELECT data_type FROM information_schema.columns WHERE table_name = ? AND column_name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, tableName, columnName);
        } catch (Exception e) {
            log.error("获取字段类型失败: table={}, column={}", tableName, columnName, e);
            return null;
        }
    }

    private void createTicketTables() {
        log.info("创建ticket相关表...");

        // 创建ticket表
        jdbcTemplate.execute("""
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
            )
        """);

        // 创建索引
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_ticket_user_id ON ticket(user_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_ticket_status ON ticket(status)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_ticket_category ON ticket(category)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_ticket_created_time ON ticket(created_time)");

        // 创建ticket_reply表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ticket_reply (
                id BIGSERIAL PRIMARY KEY,
                ticket_id BIGINT NOT NULL,
                replyer_id BIGINT,
                replyer_name VARCHAR(100),
                is_admin BOOLEAN NOT NULL DEFAULT false,
                content TEXT NOT NULL,
                created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_ticket_reply_ticket_id ON ticket_reply(ticket_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_ticket_reply_created_time ON ticket_reply(created_time)");

        // 创建user_feedback表
        jdbcTemplate.execute("""
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
            )
        """);

        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_user_feedback_user_id ON user_feedback(user_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_user_feedback_status ON user_feedback(status)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_user_feedback_created_time ON user_feedback(created_time)");

        log.info("ticket相关表创建完成");
    }

    private void rebuildTicketTables() {
        log.info("重建ticket相关表（使用CASCADE删除）...");

        // 删除表
        jdbcTemplate.execute("DROP TABLE IF EXISTS ticket CASCADE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS ticket_reply CASCADE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_feedback CASCADE");

        // 创建新表
        createTicketTables();

        log.info("ticket相关表重建完成，user_id字段已改为UUID类型");
    }
}
