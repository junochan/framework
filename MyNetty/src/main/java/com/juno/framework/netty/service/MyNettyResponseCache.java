package com.juno.framework.netty.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.juno.framework.netty.beans.SyncResponse;
import com.juno.framework.netty.configuration.NettyProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Juno
 * @Date: 2020/4/10 14:24
 */
@Slf4j
public class MyNettyResponseCache {

    protected LoadingCache<String, SyncResponse<Object>> responseCache;

    public MyNettyResponseCache(NettyProperties nettyProperties) {
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
                .build(new CacheLoader<String, SyncResponse<Object>>() {
                    @Override
                    public SyncResponse<Object> load(String key) throws Exception {
                        // 当获取key的缓存不存在时，不需要自动添加
                        return null;
                    }
                });
    }

    public void addResponse(String key,SyncResponse<Object> result) {
        responseCache.put(key,result);
    }

    public void invalidateKey(String key) {
        responseCache.invalidate(key);
    }

    public void ackMessageSync(String no, Object result) {
        SyncResponse<Object> response = responseCache.getIfPresent(no);
        if (null != response) {
            response.setResponse(result);
            responseCache.invalidate(no);
        }
    }



}
