package com.juno.framework.netty.configuration;

import com.juno.framework.netty.NettyClientRunner;
import com.juno.framework.netty.core.DispatcherHandle;
import com.juno.framework.netty.core.client.ExponentialBackOffRetry;
import com.juno.framework.netty.core.client.NettyClient;
import com.juno.framework.netty.core.client.Pinger;
import com.juno.framework.netty.core.client.RetryPolicy;
import com.juno.framework.netty.service.DefaultMyNettyClientTemplate;
import com.juno.framework.netty.service.MyNettyClientTemplate;
import com.juno.framework.netty.service.MyNettyResponseCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

/**
 * @Author: Juno
 * @Date: 2020/4/9 15:46
 */
@Configuration
@Import(MyNettyCommonConfiguration.class)
public class MyNettyClientConfiguration {

    @Lazy
    @Autowired
    private DispatcherHandle dispatcherHandle;

    @Autowired
    private MyNettyResponseCache responseCache;

    @Bean("retryPolicy")
    public RetryPolicy retryPolicy(NettyProperties properties) {
        return new ExponentialBackOffRetry(properties.getClientBaseSleepTimeMs(),properties.getClientMaxRetries(),properties.getClientMaxSleepMs());
    }

    @Bean("pinger")
    public Pinger pinger(NettyProperties properties) {
        return new Pinger(properties);
    }

    @Lazy
    @Bean("nettyClient")
    public NettyClient nettyClient(NettyProperties properties,Pinger pinger,RetryPolicy retryPolicy) {
        return new NettyClient(properties,this.dispatcherHandle,pinger,retryPolicy);
    }

    @Lazy
    @Bean("myNettyClientTemplate")
    public MyNettyClientTemplate myNettyClientTemplate(NettyProperties properties,NettyClient nettyClient) {
        DefaultMyNettyClientTemplate defaultMyNettyClientServerTemplate = new DefaultMyNettyClientTemplate(properties,responseCache);
        defaultMyNettyClientServerTemplate.setNettyClient(nettyClient);
        return defaultMyNettyClientServerTemplate;
    }

    @Bean("nettyClientRunner")
    public NettyClientRunner nettyClientRunner(NettyClient nettyClient) {
        return new NettyClientRunner(nettyClient);
    }

}
