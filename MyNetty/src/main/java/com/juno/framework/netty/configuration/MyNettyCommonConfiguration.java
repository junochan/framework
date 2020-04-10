package com.juno.framework.netty.configuration;

import com.juno.framework.netty.core.DispatcherHandle;
import com.juno.framework.netty.dispatcher.DefaultNettyDispatcher;
import com.juno.framework.netty.dispatcher.NettyDispatcher;
import com.juno.framework.netty.mapping.PropertySourcedRequestMappingHandlerMapping;
import com.juno.framework.netty.service.MyNettyResponseCache;
import com.juno.framework.netty.web.MyNettySelfController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerMapping;

/**
 * @Author: Juno
 * @Date: 2020/4/9 15:54
 */
@EnableConfigurationProperties(NettyProperties.class)
@Configuration
@ComponentScan({"com.juno.framework.netty.web"})
public class MyNettyCommonConfiguration {

    @Autowired
    private NettyProperties nettyProperties;

    @Bean("myNettyResponseCache")
    public MyNettyResponseCache myNettyResponseCache() {
        return new MyNettyResponseCache(nettyProperties);
    }

    @Bean("nettyDispatcher")
    public NettyDispatcher nettyDispatcher(ApplicationContext context,NettyProperties nettyProperties,MyNettyResponseCache myNettyResponseCache) {
        return new DefaultNettyDispatcher(context,nettyProperties, new MyNettySelfController(myNettyResponseCache));
    }

    @Bean("dispatcherHandle")
    public DispatcherHandle dispatcherHandle(NettyDispatcher nettyDispatcher) {
        return new DispatcherHandle(nettyDispatcher);
    }


    @Bean("myNettyControllerMapping")
    public HandlerMapping myNettyControllerMapping(
            Environment environment,MyNettyResponseCache myNettyResponseCache) {
        return new PropertySourcedRequestMappingHandlerMapping(environment, new MyNettySelfController(myNettyResponseCache));
    }

}
