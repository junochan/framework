package com.juno.framework.netty.context;

import com.juno.framework.netty.exception.NettyFwException;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Juno
 * @Date: 2020/4/8 11:48
 */
public class ChannelPool extends ConcurrentHashMap<String, ChannelHandlerContext> {

    private static final ChannelPool POOL = new ChannelPool();

    private long maxSize = 1000;

    private ChannelPool() {
    }

    public static ChannelPool getInstance() {
        return POOL;
    }

    public static ChannelPool getInstance(long maxSize) {
        POOL.maxSize = maxSize;
        return POOL;
    }

    @Override
    public ChannelHandlerContext put(String key, ChannelHandlerContext value) {
        checkForChannelPollSize();
        return super.put(key, value);
    }

    public void removeClient(ChannelHandlerContext ctx) {
        for (String seq : POOL.keySet()) {
            ChannelHandlerContext ch = POOL.get(seq);
            if (null != ch && ch.equals(ctx)) {
                POOL.remove(seq);
            }
        }
    }

    private void checkForChannelPollSize() {
        if (POOL.size() > maxSize) {
            throw new NettyFwException("is over channel pool size: " + maxSize);
        }
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }
}
