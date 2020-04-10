package com.juno.framework.netty.service;

import com.alibaba.fastjson.JSON;
import com.juno.framework.netty.annotation.NettyService;
import com.juno.framework.netty.beans.NettyMessage;
import com.juno.framework.netty.beans.SyncResponse;
import com.juno.framework.netty.configuration.NettyProperties;
import com.juno.framework.netty.context.ChannelPool;
import com.juno.framework.netty.exception.NettyFwException;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Juno
 * @Date: 2020/4/8 9:39
 */
@Slf4j
@NettyService
public class DefaultMyNettyServerTemplate implements MyNettyServerTemplate {

    private NettyProperties nettyProperties;
    private ChannelPool channelPool;
    private MyNettyResponseCache responseCache;

    public DefaultMyNettyServerTemplate(NettyProperties nettyProperties, MyNettyResponseCache myNettyResponseCache) {
        this.nettyProperties = nettyProperties;
        this.channelPool = ChannelPool.getInstance(nettyProperties.getChannelPoolSize());
        this.responseCache = myNettyResponseCache;
    }

    @Override
    public void registry(ChannelHandlerContext ctx, String seq) {
        channelPool.put(seq,ctx);
        log.info("与客户端 [{}],地址: [ {} ] 建立连接", seq,ctx.channel().remoteAddress());
    }


    private void checkForSeq(String seq) {
        if (!channelPool.containsKey(seq)) {
            log.error("无效的 seq : {}",seq);
            throw new NettyFwException("无效的 seq : " + seq);
        }
    }


    @Override
    public void sendMessage(NettyMessage message, String seq) {
        checkForSeq(seq);
        sendMessage(channelPool.get(seq), JSON.toJSONString(message));
    }


    @Override
    public Object syncSendMessage(NettyMessage message, String seq) throws NettyFwException {
        checkForSeq(seq);
        if (StringUtils.isEmpty(message.getNo())) {
            String no = UUID.randomUUID().toString().replace("-", "");
            message.setNo(no);
        }
        SyncResponse<Object> result = new SyncResponse<>();
        responseCache.addResponse(message.getNo(),result);
        sendMessage(channelPool.get(seq), JSON.toJSONString(message));
        try {
            return result.get(this.nettyProperties.getCacheExpireAfterWrite(),TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            responseCache.invalidateKey(message.getNo());
            throw new NettyFwException(e.getMessage());
        }
    }


    /**
     * 向所有客户端发送消息
     *
     * @param message 消息内容
     */
    @Override
    public void broadcast(NettyMessage message) {
        String msg = JSON.toJSONString(message);
        channelPool.values().forEach(ch -> sendMessage(ch,msg));
    }

    private void checkForContext(ChannelHandlerContext ctx, String msg) {
        if (null == ctx || StringUtils.isEmpty(msg)) {
            throw new NettyFwException("channel or msg is empty!!!");
        }
        if (!ctx.channel().isActive()) {
            throw new NettyFwException("channel is not active!!!");
        }
    }

    private void sendMessage(ChannelHandlerContext ctx, String msg) {
        checkForContext(ctx,msg);
        ctx.writeAndFlush(msg).addListener(future -> log.info("send message to {} {}",ctx.channel().remoteAddress(), future.isSuccess() ? "success" : "failed"));
    }

    @Override
    public void removeClient(ChannelHandlerContext ctx) {
        channelPool.removeClient(ctx);
    }
}
