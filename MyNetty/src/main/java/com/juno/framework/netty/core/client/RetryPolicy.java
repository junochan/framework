package com.juno.framework.netty.core.client;

/**
 * @Author: Juno
 * @Date: 2020/4/9 15:09
 */
public interface RetryPolicy {

    boolean allowRetry(int retryCount);

    long getSleepTimeMs(int retryCount);

}
