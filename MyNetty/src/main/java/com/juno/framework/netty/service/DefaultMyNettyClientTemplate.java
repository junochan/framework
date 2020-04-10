package com.juno.framework.netty.service;

import com.alibaba.fastjson.JSON;
import com.juno.framework.netty.annotation.NettyService;
import com.juno.framework.netty.beans.NettyMessage;
import com.juno.framework.netty.beans.SyncResponse;
import com.juno.framework.netty.configuration.NettyProperties;
import com.juno.framework.netty.core.client.NettyClient;
import com.juno.framework.netty.exception.NettyFwException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Juno
 * @Date: 2020/4/10 14:14
 */
@Slf4j
@NettyService
public class DefaultMyNettyClientTemplate implements MyNettyClientTemplate {

    @Lazy
    private NettyClient nettyClient;
    private NettyProperties properties;
    private MyNettyResponseCache responseCache;

    public DefaultMyNettyClientTemplate(NettyProperties properties, MyNettyResponseCache responseCache) {
        this.properties = properties;
        this.responseCache = responseCache;
    }

    @Lazy
    public void setNettyClient(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void sendMessage(NettyMessage message) {
        this.nettyClient.sendMessage(JSON.toJSONString(message));
    }

    @Override
    public Object syncSendMessage(NettyMessage message) throws NettyFwException {
        SyncResponse<Object> result = new SyncResponse<>();
        if (StringUtils.isEmpty(message.getNo())) {
            String no = UUID.randomUUID().toString().replace("-", "");
            message.setNo(no);
        }
        responseCache.addResponse(message.getNo(),result);
        this.nettyClient.sendMessage(JSON.toJSONString(message));
        try {
            return result.get(properties.getCacheExpireAfterWrite(), TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            responseCache.invalidateKey(message.getNo());
            throw new NettyFwException(e);
        }
    }

}
