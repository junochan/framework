package com.juno.framework.netty.core.server;

import com.juno.framework.netty.configuration.NettyProperties;
import com.juno.framework.netty.core.DispatcherHandle;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @Author: Juno
 * @Date: 2020/4/1 9:42
 */

public class ServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private NettyProperties nettyProperties;
    private DispatcherHandle dispatcherHandle;

    public ServerHandlerInitializer(NettyProperties nettyProperties, DispatcherHandle dispatcherHandle) {
        this.nettyProperties = nettyProperties;
        this.dispatcherHandle = dispatcherHandle;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new IdleStateHandler(nettyProperties.getReaderIdleTimeSeconds(), nettyProperties.getWriterIdleTimeSeconds(), nettyProperties.getAllIdleTimeSeconds()));
        ch.pipeline().addLast(new ServerIdleStateTrigger());
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(nettyProperties.getMaxFrameLength(), nettyProperties.getLengthFieldOffset(), nettyProperties.getLengthFieldLength(), nettyProperties.getLengthAdjustment(), nettyProperties.getInitialBytesToStrip()));
        ch.pipeline().addLast(new LengthFieldPrepender(nettyProperties.getLengthFieldLength()));
        ch.pipeline().addLast(new StringDecoder());
        ch.pipeline().addLast(new StringEncoder());
        ch.pipeline().addLast(dispatcherHandle);
    }

}
