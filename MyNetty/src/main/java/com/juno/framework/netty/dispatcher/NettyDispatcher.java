package com.juno.framework.netty.dispatcher;

import com.juno.framework.netty.beans.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @Author: Juno
 * @Date: 2020/4/7 14:06
 */
public interface NettyDispatcher {

    void init(ApplicationContext applicationContext, String configLocations);

    void doDispatch(ChannelHandlerContext ctx, NettyMessage msg) throws Exception;

    Map<String,NettyHandleMapping> getHandlerMappings();

}
