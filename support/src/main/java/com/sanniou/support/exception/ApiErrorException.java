package com.sanniou.support.exception;

/**
 * 接口请求 code 失败的异常
 */
public class ApiErrorException extends BaseCustomizeException {

    public ApiErrorException(String message, boolean mute, int errorCode) {
        super(message, mute);
        this.errorCode = errorCode;
    }

    public ApiErrorException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    private int errorCode;

    public ApiErrorException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
