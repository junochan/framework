package com.juno.framework.netty.configuration;

import com.juno.framework.netty.dispatcher.NettyDispatcher;
import com.juno.framework.netty.mapping.PropertySourcedRequestMappingHandlerMapping;
import com.juno.framework.netty.service.DefaultMyNettyTemplate;
import com.juno.framework.netty.service.MyNettyTemplate;
import com.juno.framework.netty.web.MyNettyController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerMapping;

/**
 * @Author: Juno
 * @Date: 2020/4/8 9:45
 */
@Configuration
@Import(MyNettyConfiguration.class)
public class MyNettyAutoConfiguration {

    @Autowired
    private NettyProperties nettyProperties;

    @Bean
    public MyNettyTemplate myNettyTemplate() {
        return new DefaultMyNettyTemplate(nettyProperties);
    }

    @Bean
    public HandlerMapping myNettyControllerMapping(
            Environment environment, NettyDispatcher nettyDispatcher) {
        return new PropertySourcedRequestMappingHandlerMapping(environment, new MyNettyController(nettyDispatcher));
    }

}
