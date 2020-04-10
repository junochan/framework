package com.juno.framework.netty.core.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Juno
 * @Date: 2020/4/9 15:10
 */
@Slf4j
@ChannelHandler.Sharable
public class ReconnectHandler extends ChannelInboundHandlerAdapter {

    private int retries = 0;
    private NettyClient nettyClient;
    private RetryPolicy retryPolicy;

    public ReconnectHandler(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public void setNettyClient(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Successfully established a connection to the server.");
        this.retries = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (retries == 0) {
            log.error("Lost the TCP connection with the server.");
            ctx.close();
        }

        boolean allowRetry = retryPolicy.allowRetry(retries);
        if (allowRetry) {

            long sleepTimeMs = retryPolicy.getSleepTimeMs(retries);

            log.info("Try to reconnect to the server after {}ms. Retry count: {}.", sleepTimeMs, ++retries);

            final EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(() -> {
                log.info("Reconnecting ...");
                nettyClient.connect();
            }, sleepTimeMs, TimeUnit.MILLISECONDS);
        }
        ctx.fireChannelInactive();
    }
}
