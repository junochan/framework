package com.juno.framework.netty;

import com.juno.framework.netty.core.server.NettyServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @Author: Juno
 * @Date: 2020/4/7 16:50
 */
@Order(Ordered.LOWEST_PRECEDENCE - 2)
public class NettyServerRunner implements ApplicationRunner {

    private NettyServer nettyServer;

    public NettyServerRunner(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            nettyServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
