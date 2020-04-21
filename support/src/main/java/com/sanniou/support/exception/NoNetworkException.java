package com.sanniou.support.exception;

/**
 * 没有网络时抛出的异常
 *
 * @author jichang
 * @date 2018/8/10
 */
public class NoNetworkException extends BaseCustomizeException {

    public NoNetworkException(String message, boolean mute) {
        super(message, mute);
    }

    public NoNetworkException(String message) {
        super(message);
    }

    public NoNetworkException() {
        super("没有网络连接");
    }
}
