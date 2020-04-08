package com.juno.framework.netty.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.juno.framework.netty.beans.NettyMessage;
import com.juno.framework.netty.beans.SyncResponse;
import com.juno.framework.netty.configuration.NettyProperties;
import com.juno.framework.netty.context.ChannelPool;
import com.juno.framework.netty.exception.NettyFwException;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Juno
 * @Date: 2020/4/8 9:39
 */
@Slf4j
public class DefaultMyNettyTemplate implements MyNettyTemplate {

    private NettyProperties nettyProperties;
    private LoadingCache<String, SyncResponse<String>> responseCache;

    private ChannelPool channelPool;

    public DefaultMyNettyTemplate(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
        responseCache = CacheBuilder.newBuilder()
                //设置缓存容器的初始容量
                .initialCapacity(nettyProperties.getCacheInitialCapacity())
                // maximumSize 设置缓存大小
                .maximumSize(nettyProperties.getCacheMaximumSize())
                //设置并发级别，并发级别是指可以同时写缓存的线程数
                .concurrencyLevel(nettyProperties.getCacheConcurrencyLevel())
                // expireAfterWrite设置写缓存后xx秒钟过期
                .expireAfterWrite(nettyProperties.getCacheExpireAfterWrite(), TimeUnit.SECONDS)
                //设置缓存的移除通知
                .removalListener(notification -> log.debug("LoadingCache: {} was removed, cause is {}",notification.getKey(), notification.getCause()))
                //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
                .build(new CacheLoader<String, SyncResponse<String>>() {
                    @Override
                    public SyncResponse<String> load(String key) throws Exception {
                        // 当获取key的缓存不存在时，不需要自动添加
                        return null;
                    }
                });
        this.channelPool = ChannelPool.getInstance(nettyProperties.getChannelPoolSize());
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
    public String syncSendMessage(NettyMessage message, String seq) throws NettyFwException {
        checkForSeq(seq);
        if (StringUtils.isEmpty(message.getNo())) {
            String no = UUID.randomUUID().toString().replace("-", "");
            message.setNo(no);
        }
        SyncResponse<String> result = new SyncResponse<>();
        responseCache.put(message.getNo(),result);
        sendMessage(channelPool.get(seq), JSON.toJSONString(message));
        try {
            return result.get(this.nettyProperties.getCacheExpireAfterWrite(),TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            responseCache.invalidate(message.getNo());
            throw new NettyFwException(e.getMessage());
        }
    }


    @Override
    public void ackMessageSync(String no, String result) {
        SyncResponse<String> response = responseCache.getIfPresent(no);
        if (null != response) {
            response.setResponse(result);
            responseCache.invalidate(no);
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
