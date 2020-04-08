package com.juno.framework.netty.exception;

/**
 * @Author: Juno
 * @Date: 2020/4/7 10:33
 */
public class NettyFwException extends RuntimeException {

    public NettyFwException() {
        super();
    }

    public NettyFwException(String message) {
        super(message);
    }

    public NettyFwException(String message, Throwable cause) {
        super(message, cause);
    }

    public NettyFwException(Throwable cause) {
        super(cause);
    }

    protected NettyFwException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
