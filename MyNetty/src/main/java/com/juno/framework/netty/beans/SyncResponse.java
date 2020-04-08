package com.juno.framework.netty.beans;

import com.juno.framework.netty.exception.NettyFwException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Juno
 * @Date: 2020/4/3 14:17
 */
public class SyncResponse<T> implements Future<T> {

    private CountDownLatch latch = new CountDownLatch(1);

    private T response;

    private long beginTime = System.currentTimeMillis();

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }


    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return null != response;
    }

    @Override
    public T get() throws InterruptedException {
        latch.await();
        return this.response;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException,NettyFwException {
        if (latch.await(timeout,unit)) {
            return this.response;
        }
        throw new NettyFwException("请求超时");
    }

    // 用于设置响应结果，并且做countDown操作，通知请求线程
    public void setResponse(T response) {
        this.response = response;
        latch.countDown();
    }

    public long getBeginTime() {
        return beginTime;
    }

}
