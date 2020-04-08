package com.juno.framework.netty.core;

import com.juno.framework.netty.configuration.NettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * @Author: Juno
 * @Date: 2020/4/1 9:53
 */
@Slf4j
public class NettyServer {

    private NettyProperties nettyProperties;
    NioEventLoopGroup bossGroup;
    NioEventLoopGroup workerGroup;
    private ServerHandlerInitializer serverHandlerInitializer;
    private InetSocketAddress tcpPort;

    public NettyServer(NettyProperties nettyProperties, NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup, ServerHandlerInitializer serverHandlerInitializer, InetSocketAddress tcpPort) {
        this.nettyProperties = nettyProperties;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.serverHandlerInitializer = serverHandlerInitializer;
        this.tcpPort = tcpPort;
    }

    private Channel serverChannel;

    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(serverHandlerInitializer);
        b.option(ChannelOption.SO_BACKLOG, nettyProperties.getBacklog());
        // 服务器异步创建绑定
        ChannelFuture cf = b.bind(tcpPort).sync();
        log.info("netty server 启动正在监听： " + cf.channel().localAddress());
        serverChannel = cf.channel().closeFuture().sync().channel();
    }

    @PreDestroy
    public void stop() {
        serverChannel.close();
        serverChannel.parent().close();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}
