package com.juno.framework.netty.core;

import com.alibaba.fastjson.JSON;
import com.juno.framework.netty.beans.NettyMessage;
import com.juno.framework.netty.context.ChannelPool;
import com.juno.framework.netty.dispatcher.NettyDispatcher;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @Author: Juno
 * @Date: 2020/4/1 9:36
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerBizHandler extends SimpleChannelInboundHandler<String> {

    private NettyDispatcher dispatcher;

    public ServerBizHandler(NettyDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        try {
            log.info("receive {} data: {}",ctx.channel().remoteAddress(),msg);
            NettyMessage nettyMessage = JSON.parseObject(msg, NettyMessage.class);
            if (!StringUtils.isEmpty(nettyMessage.getPath())) {
                dispatcher.doDispatch(ctx, nettyMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Established connection with the remote client:{}",ctx.channel().remoteAddress());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Disconnected with the remote client:{}",ctx.channel().remoteAddress());
        ChannelPool.getInstance(1).removeClient(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
