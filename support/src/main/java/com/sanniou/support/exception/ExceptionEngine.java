package com.sanniou.support.exception;

import com.blankj.utilcode.util.LogUtils;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

import retrofit2.HttpException;

import static com.sanniou.support.utils.ContantUtilKt.isDebug;

/**
 * Rxjava 式的网络请求处理中，在 onError 中使用处理 Exception 得到设置好的信息提示
 */
public class ExceptionEngine {

    private ExceptionEngine() {
        throw new IllegalArgumentException("what are you doing?");
    }

    /**
     * 对应HTTP的状态码
     */
    private static final int UNAUTHORIZED = 401;

    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    /**
     * 适用于 retrofit + rxJava onError 中对 Exception 进行适配 提示
     */
    public static BaseCustomizeException handleException(Throwable e) {
        return handleException(e, false);
    }

    public static BaseCustomizeException handleException(Throwable e, boolean mute) {
        if (!mute) {
            LogUtils.w(e);
        }
        BaseCustomizeException ex;
        // HTTP错误 均视为网络错误
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex = new BaseCustomizeException(httpException.code() + "网络错误");
                    break;
            }
            return ex;
            // 服务器返回的错误
        } else if (e instanceof ApiErrorException
                // 没有网络连接
                || e instanceof NoNetworkException
                // 返回空数据，暂时没用上
                || e instanceof EmptyDateException
                // 网络请求出错
                || e instanceof NetRequstException
                // 用户操作的错误
                || e instanceof AppErrorException
                // 这些都是自定义的异常，正常返回
                || e instanceof BaseCustomizeException) {
            return (BaseCustomizeException) e;
            // 均视为解析错误
        } else if (e instanceof NoRouteToHostException) {
            ex = new BaseCustomizeException("路由错误");
            return ex;
        } else if (e instanceof UnknownHostException) {
            ex = new BaseCustomizeException("路由错误");
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new BaseCustomizeException("连接超时");
            return ex;
        } else if (e instanceof SSLHandshakeException) {
            ex = new BaseCustomizeException("证书校验错误");
            return ex;
        } else if (e instanceof SSLPeerUnverifiedException) {
            ex = new BaseCustomizeException("证书校验错误");
            return ex;
        } else if (e instanceof SSLException) {
            ex = new BaseCustomizeException("证书校验错误");
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new BaseCustomizeException("服务器连接失败");
            return ex;
            // 均视为网络超时
        } else if (e instanceof SocketTimeoutException) {
            ex = new BaseCustomizeException("连接超时");
            return ex;
        } else if (e instanceof InterruptedIOException) {
            ex = new BaseCustomizeException("服务器连接失败");
            return ex;
        } else if (e instanceof SocketException) {
            ex = new BaseCustomizeException("连接失败");
            return ex;
        } else {
            ex = new BaseCustomizeException(e.getMessage());
            return ex;
        }
    }

    public static String handleMessage(Throwable e) {
        BaseCustomizeException exception = handleException(e, true);
        if (isDebug()) {
            LogUtils.w(e, new Exception(exception.getMessage()));
        }
        return exception.mute ? null : exception.getMessage();
    }
}
