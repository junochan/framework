package com.juno.framework.netty.configuration;

import com.juno.framework.netty.NettyServerRunner;
import com.juno.framework.netty.core.DispatcherHandle;
import com.juno.framework.netty.core.server.NettyServer;
import com.juno.framework.netty.core.server.ServerHandlerInitializer;
import com.juno.framework.netty.service.DefaultMyNettyServerTemplate;
import com.juno.framework.netty.service.MyNettyResponseCache;
import com.juno.framework.netty.service.MyNettyServerTemplate;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.net.InetSocketAddress;

/**
 * @Author: Juno
 * @Date: 2020/4/8 9:23
 */

@Configuration
@Import(MyNettyCommonConfiguration.class)
public class MyNettyServerConfiguration {

    @Autowired
    private MyNettyResponseCache responseCache;

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup(NettyProperties nettyProperties){
        return new NioEventLoopGroup(nettyProperties.getBossThread());
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup(NettyProperties nettyProperties){
        return new NioEventLoopGroup(nettyProperties.getWorkerThread());
    }

    @Bean("tcpSocketAddress")
    public InetSocketAddress tcpPost(NettyProperties nettyProperties){
        return new InetSocketAddress(nettyProperties.getPort());
    }

    @Bean(name = "serverHandlerInitializer")
    public ServerHandlerInitializer serverHandlerInitializer(NettyProperties nettyProperties, DispatcherHandle dispatcherHandle) {
        return new ServerHandlerInitializer(nettyProperties,dispatcherHandle);
    }

    @Bean(name = "nettyServer")
    public NettyServer nettyServer(NettyProperties nettyProperties,NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup, ServerHandlerInitializer serverHandlerInitializer, InetSocketAddress tcpPort) {
        return new NettyServer(nettyProperties,bossGroup,workerGroup,serverHandlerInitializer,tcpPort);
    }

    @Bean("myNettyServerTemplate")
    public MyNettyServerTemplate myNettyServerTemplate(NettyProperties nettyProperties) {
        return new DefaultMyNettyServerTemplate(nettyProperties,responseCache);
    }

    @Bean
    public NettyServerRunner nettyServerRunner(NettyServer nettyServer) {
        return new NettyServerRunner(nettyServer);
    }


}
