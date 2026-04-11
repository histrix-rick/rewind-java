package com.rewindai.system.config.enums;

import lombok.Getter;

/**
 * 系统配置Key枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum ConfigKey {
    // ========== 基础配置 ==========
    SITE_NAME("site.name", "站点名称", ConfigCategory.BASIC, "STRING", "Rewind.ai"),
    SITE_DESCRIPTION("site.description", "站点描述", ConfigCategory.BASIC, "STRING", "记录你的每一个梦境"),
    REGISTRATION_ENABLED("registration.enabled", "启用注册", ConfigCategory.BASIC, "BOOLEAN", "true"),
    INVITE_CODE_REQUIRED("invite.code.required", "需要邀请码", ConfigCategory.BASIC, "BOOLEAN", "false"),
    USER_AGREEMENT("user.agreement", "用户协议", ConfigCategory.BASIC, "TEXT", ""),
    PRIVACY_POLICY("privacy.policy", "隐私政策", ConfigCategory.BASIC, "TEXT", ""),
    CUSTOMER_SERVICE_EMAIL("customer.service.email", "客服邮箱", ConfigCategory.BASIC, "STRING", "support@rewind.ai"),
    CUSTOMER_SERVICE_PHONE("customer.service.phone", "客服电话", ConfigCategory.BASIC, "STRING", ""),

    // ========== 内容配置 ==========
    CONTENT_REVIEW_ENABLED("content.review.enabled", "启用内容审核", ConfigCategory.CONTENT, "BOOLEAN", "true"),
    RECOMMEND_ALGORITHM_VERSION("recommend.algorithm.version", "推荐算法版本", ConfigCategory.CONTENT, "STRING", "v1"),

    // ========== 社交配置 ==========
    SOCIAL_FOLLOW_MAX("social.follow.max", "关注上限", ConfigCategory.SOCIAL, "INTEGER", "1000"),
    SOCIAL_LIKE_DAILY_MAX("social.like.daily.max", "每日点赞上限", ConfigCategory.SOCIAL, "INTEGER", "100"),
    SOCIAL_REWARD_MIN_AMOUNT("social.reward.min.amount", "打赏最小金额", ConfigCategory.SOCIAL, "INTEGER", "1"),
    SOCIAL_REWARD_MAX_AMOUNT("social.reward.max.amount", "打赏最大金额", ConfigCategory.SOCIAL, "INTEGER", "10000"),
    SOCIAL_COMMENT_MAX_LENGTH("social.comment.max.length", "评论最大长度", ConfigCategory.SOCIAL, "INTEGER", "1000"),

    // ========== 财务配置 ==========
    FINANCE_EXCHANGE_RATE("finance.exchange.rate", "梦想币汇率", ConfigCategory.FINANCE, "INTEGER", "100"),
    FINANCE_WITHDRAWAL_MIN_AMOUNT("finance.withdrawal.min.amount", "提现最小金额", ConfigCategory.FINANCE, "INTEGER", "1000"),
    FINANCE_WITHDRAWAL_ENABLED("finance.withdrawal.enabled", "启用提现", ConfigCategory.FINANCE, "BOOLEAN", "true"),
    FINANCE_REWARD_SHARE_RATIO("finance.reward.share.ratio", "打赏分成比例", ConfigCategory.FINANCE, "DECIMAL", "0.7"),

    // ========== 推送配置 ==========
    PUSH_ENABLED("push.enabled", "启用推送", ConfigCategory.PUSH, "BOOLEAN", "true"),
    PUSH_TIME_START("push.time.start", "推送开始时间", ConfigCategory.PUSH, "STRING", "08:00"),
    PUSH_TIME_END("push.time.end", "推送结束时间", ConfigCategory.PUSH, "STRING", "22:00"),
    PUSH_DAILY_LIMIT("push.daily.limit", "每日推送次数限制", ConfigCategory.PUSH, "INTEGER", "5");

    private final String key;
    private final String name;
    private final ConfigCategory category;
    private final String valueType;
    private final String defaultValue;

    ConfigKey(String key, String name, ConfigCategory category, String valueType, String defaultValue) {
        this.key = key;
        this.name = name;
        this.category = category;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
    }
}
