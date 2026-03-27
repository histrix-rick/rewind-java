package com.rewindai.common.core.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author Rewind.ai Team
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 通用错误码 (1xxx)
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 用户模块错误码 (2xxx)
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_ALREADY_EXISTS(2002, "用户已存在"),
    USERNAME_ALREADY_EXISTS(2003, "用户名已存在"),
    PHONE_ALREADY_EXISTS(2004, "手机号已存在"),
    EMAIL_ALREADY_EXISTS(2005, "邮箱已存在"),
    PASSWORD_ERROR(2006, "密码错误"),
    USER_DISABLED(2007, "账号已被禁用"),
    USER_BANNED(2008, "账号已被封禁"),
    USER_MUTED(2009, "账号已被禁言"),

    // 认证模块错误码 (3xxx)
    TOKEN_INVALID(3001, "Token无效"),
    TOKEN_EXPIRED(3002, "Token已过期"),
    LOGIN_REQUIRED(3003, "请先登录"),

    // 后台管理错误码 (4xxx)
    ADMIN_NOT_FOUND(4001, "管理员不存在"),
    ADMIN_DISABLED(4002, "管理员已被禁用"),
    ROLE_NOT_FOUND(4003, "角色不存在"),
    MENU_NOT_FOUND(4004, "菜单不存在"),
    NO_PERMISSION(4005, "无权限操作"),

    // 梦境模块错误码 (5xxx)
    DREAM_NOT_FOUND(5001, "梦境不存在"),
    DREAM_ALREADY_DELETED(5002, "梦境已删除"),
    DREAM_NOT_PUBLIC(5003, "梦境未公开"),
    DREAM_ACCESS_DENIED(5004, "无权访问此梦境"),

    // 钱包模块错误码 (6xxx)
    WALLET_NOT_FOUND(6001, "钱包不存在"),
    INSUFFICIENT_BALANCE(6002, "余额不足"),
    TRANSACTION_NOT_FOUND(6003, "交易记录不存在"),
    AMOUNT_INVALID(6004, "金额无效"),

    // 通用参数错误
    PARAM_ERROR(1001, "参数错误");

    private final int code;
    private final String message;
}
