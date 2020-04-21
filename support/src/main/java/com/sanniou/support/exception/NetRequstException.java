package com.sanniou.support.exception;

/**
 * 网络请求失败异常
 */
public class NetRequstException extends BaseCustomizeException {

    public NetRequstException(String message, boolean mute) {
        super(message, mute);
    }

    public NetRequstException(String message) {
        super(message);
    }
}