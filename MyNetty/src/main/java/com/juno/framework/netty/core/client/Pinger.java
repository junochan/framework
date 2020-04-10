package com.juno.framework.netty.core.client;

import com.juno.framework.netty.configuration.NettyProperties;
import com.juno.framework.netty.exception.NettyFwException;
import com.juno.framework.netty.utils.NettyMessageGenerator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 客户端连接到服务器端后，会循环执行一个任务：随机等待几秒，然后ping一下Server端，即发送一个心跳包。
 * @Author: Juno
 * @Date: 2020/4/9 14:14
 */
@Slf4j
@ChannelHandler.Sharable
public class Pinger extends ChannelInboundHandlerAdapter {

    private NettyProperties nettyProperties;

    public Pinger(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ping(ctx);
    }

    private void ping(ChannelHandlerContext ctx) {
        log.debug("next heart beat will send after " + nettyProperties.getClientPingInterval() + "s.");
        String heartBeat = NettyMessageGenerator.genHeartBeatMessage().toString();
        ScheduledFuture<?> future = ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                if (ctx.channel().isActive()) {
                    log.debug("sending heart beat to the server...");
                    ctx.writeAndFlush(heartBeat);
                } else {
                    ctx.channel().closeFuture();
                    throw new NettyFwException("The connection had broken, cancel the task that will send a heart beat.");
                }
            }
        }, nettyProperties.getClientPingInterval(), TimeUnit.SECONDS);

        future.addListener(new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()) {
                    ping(ctx);
                }
            }
        });
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
