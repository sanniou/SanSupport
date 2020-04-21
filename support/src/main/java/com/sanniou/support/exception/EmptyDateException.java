package com.sanniou.support.exception;

/**
 * 接口请求 code 为 0，返回 [] 或者 [null] 时 的异常
 */
public class EmptyDateException extends BaseCustomizeException {

    public EmptyDateException(String message, boolean mute) {
        super(message, mute);
    }

    public EmptyDateException(String message) {
        super(message);
    }
}
