package com.sanniou.support.exception;

/**
 * 自定义一个基础类型的异常，用来描述 APP 运行中的各种错误情况
 */
public class BaseCustomizeException extends RuntimeException {

    /**
     * ExceptionEngine 处理时返回的消息
     */
    public boolean mute;

    public BaseCustomizeException(String message, boolean mute) {
        super(message);
        this.mute = mute;
    }

    public BaseCustomizeException(String message) {
        super(message);
    }
}
