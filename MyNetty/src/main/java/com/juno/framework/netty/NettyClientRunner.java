package com.juno.framework.netty;

import com.juno.framework.netty.core.client.NettyClient;
import com.juno.framework.netty.dispatcher.DefaultNettyDispatcher;
import com.juno.framework.netty.dispatcher.NettyDispatcher;
import com.juno.framework.netty.web.MyNettySelfController;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @Author: Juno
 * @Date: 2020/4/9 15:58
 */
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class NettyClientRunner implements ApplicationRunner {

    private NettyClient nettyClient;

    public NettyClientRunner(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        nettyClient.connect();
    }

}
