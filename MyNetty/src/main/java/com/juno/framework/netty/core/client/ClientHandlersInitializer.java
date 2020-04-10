package com.juno.framework.netty.core.client;

import com.juno.framework.netty.configuration.NettyProperties;
import com.juno.framework.netty.core.DispatcherHandle;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * @Author: Juno
 * @Date: 2020/4/9 14:42
 */
public class ClientHandlersInitializer extends ChannelInitializer<SocketChannel> {

    private NettyProperties properties;
    private ReconnectHandler reconnectHandler;
    private DispatcherHandle dispatcherHandle;
    private Pinger pinger;

    public ClientHandlersInitializer(NettyProperties properties,DispatcherHandle dispatcherHandle,Pinger pinger) {
        this.properties = properties;
        this.dispatcherHandle = dispatcherHandle;
        this.pinger = pinger;
    }

    public void setReconnectHandler(ReconnectHandler reconnectHandler) {
        this.reconnectHandler = reconnectHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(this.reconnectHandler);
        pipeline.addLast(new LengthFieldBasedFrameDecoder(properties.getMaxFrameLength(), properties.getLengthFieldOffset(), properties.getLengthFieldLength(), properties.getLengthAdjustment(), properties.getInitialBytesToStrip()));
        pipeline.addLast(new LengthFieldPrepender(properties.getLengthFieldLength()));
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(this.pinger);
        pipeline.addLast(this.dispatcherHandle);
    }
}
