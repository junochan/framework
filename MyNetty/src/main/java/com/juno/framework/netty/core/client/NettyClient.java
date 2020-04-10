package com.juno.framework.netty.core.client;

import com.alibaba.fastjson.JSON;
import com.juno.framework.netty.beans.SyncResponse;
import com.juno.framework.netty.configuration.NettyProperties;
import com.juno.framework.netty.core.DispatcherHandle;
import com.juno.framework.netty.exception.NettyFwException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Juno
 * @Date: 2020/4/9 14:40
 */
@Slf4j
public class NettyClient {

    private Bootstrap bootstrap;
    private Channel channel;
    private NettyProperties properties;
    private ClientHandlersInitializer clientHandlersInitializer;

    public NettyClient(NettyProperties properties, DispatcherHandle dispatcherHandle, Pinger pinger,RetryPolicy retryPolicy) {
        this.properties = properties;
        this.clientHandlersInitializer = new ClientHandlersInitializer(properties,dispatcherHandle,pinger);
        ReconnectHandler reconnectHandler = new ReconnectHandler(retryPolicy);
        reconnectHandler.setNettyClient(this);
        this.clientHandlersInitializer.setReconnectHandler(reconnectHandler);
        init();
    }


    /**
     * 向远程TCP服务器请求连接
     */
    public void connect() {
        synchronized (bootstrap) {
            String serverHost = properties.getServerHost();
            int serverPort = properties.getServerPort();
            if (StringUtils.isEmpty(serverHost)) {
                serverHost = "localhost";
            }
            if (serverPort <= 0) {
                serverPort = properties.getPort();
            }
            ChannelFuture future = bootstrap.connect(serverHost, serverPort);
            future.addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    log.info("连接MyNetty服务端成功");
                } else {
                    f.channel().pipeline().fireChannelInactive();
//                    log.info("连接服务端失败，正在重连...");
//                    f.channel().eventLoop().schedule(() -> {
//                        System.out.println("连接正在重试...");
//                        connect();
//                    },properties.getClientRetryInterval(), TimeUnit.SECONDS);
                }
            });
            this.channel = future.channel();
        }
    }

    private void init() {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(this.clientHandlersInitializer);
    }

    public void sendMessage(Object msg) {
        channel.writeAndFlush(msg).addListener(future -> log.info("send result: " + (future.isSuccess() ? "success" : "failed")));
    }

}
