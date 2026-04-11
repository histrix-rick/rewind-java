package com.rewindai.common.core.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 统一API响应结果封装
 *
 * @author Rewind.ai Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 额外数据
     */
    private Map<String, Object> extra;

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, null);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, null);
    }

    public static <T> Result<T> successWithExtra(T data, Map<String, Object> extra) {
        return new Result<>(200, "操作成功", data, extra);
    }

    public static <T> Result<T> successWithExtra(String message, T data, Map<String, Object> extra) {
        return new Result<>(200, message, data, extra);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null, null);
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null, null);
    }

    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null, null);
    }

    public boolean isSuccess() {
        return this.code == 200;
    }
}
