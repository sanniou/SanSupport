package com.sanniou.support.exception;

/**
 * 用户操作失误的异常
 */
public class AppErrorException extends BaseCustomizeException {

    public AppErrorException(String message, boolean mute) {
        super(message, mute);
    }

    public AppErrorException(String message) {
        super(message);
    }
}
