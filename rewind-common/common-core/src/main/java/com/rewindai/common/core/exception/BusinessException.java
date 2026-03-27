package com.rewindai.common.core.exception;

import com.rewindai.common.core.result.ErrorCode;
import lombok.Getter;

/**
 * 业务异常
 *
 * @author Rewind.ai Team
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
