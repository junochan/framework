package com.juno.framework.netty.configuration;

import com.juno.framework.netty.NettyServerRunner;
import com.juno.framework.netty.core.NettyServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author: Juno
 * @Date: 2020/4/8 11:08
 */
@Configuration
@Import(NettyServer.class)
public class MyNettyAutoStarter {

    @Bean
    public NettyServerRunner nettyServerRunner(NettyServer nettyServer) {
        return new NettyServerRunner(nettyServer);
    }

}
