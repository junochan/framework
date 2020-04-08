package com.juno.framework.netty.configuration;

import com.juno.framework.netty.core.NettyServer;
import com.juno.framework.netty.core.ServerBizHandler;
import com.juno.framework.netty.core.ServerHandlerInitializer;
import com.juno.framework.netty.dispatcher.DefaultNettyDispatcher;
import com.juno.framework.netty.dispatcher.NettyDispatcher;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @Author: Juno
 * @Date: 2020/4/8 9:23
 */
@EnableConfigurationProperties(NettyProperties.class)
@Configuration
public class MyNettyConfiguration {

    @Autowired
    private NettyProperties nettyProperties;

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup(){
        return new NioEventLoopGroup(nettyProperties.getBossThread());
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup(){
        return new NioEventLoopGroup(nettyProperties.getWorkerThread());
    }

    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPost(){
        return new InetSocketAddress(nettyProperties.getPort());
    }

    @Bean(name = "nettyDispatcher")
    public NettyDispatcher nettyDispatcher(ApplicationContext context) {
        return new DefaultNettyDispatcher(context,nettyProperties.getScanPackage());
    }

    @Bean(name = "serverBizHandler")
    public ServerBizHandler serverBizHandler(NettyDispatcher nettyDispatcher) {
        return new ServerBizHandler(nettyDispatcher);
    }

    @Bean(name = "serverHandlerInitializer")
    public ServerHandlerInitializer serverHandlerInitializer(ServerBizHandler serverBizHandler) {
        return new ServerHandlerInitializer(nettyProperties,serverBizHandler);
    }

    @Bean(name = "nettyServer")
    public NettyServer nettyServer(NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup, ServerHandlerInitializer serverHandlerInitializer, InetSocketAddress tcpPort) {
        return new NettyServer(nettyProperties,bossGroup,workerGroup,serverHandlerInitializer,tcpPort);
    }

}
