package com.juno.framework.netty.service;

import com.juno.framework.netty.beans.NettyMessage;
import com.juno.framework.netty.exception.NettyFwException;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: Juno
 * @Date: 2020/4/8 9:34
 */
public interface MyNettyServerTemplate {
    /**
     * 客户端注册，保存客户端通道
     * @param ctx @link{ChannelHandlerContext}
     * @param seq 客户端唯一标识
     */
    void registry(ChannelHandlerContext ctx, String seq);

    /**
     * 异步发送消息
     * @param message 消息内容
     * @param seq 客户端唯一标识符
     */
    void sendMessage(NettyMessage message, String seq);

    /**
     * 同步发送消息
     * @param message 消息内容
     * @param seq 客户端唯一标识符
     * @return 返回结果
     */
    Object syncSendMessage(NettyMessage message, String seq) throws NettyFwException;

    /**
     * 向所有客户端发送消息
     * @param message 消息内容
     */
    void broadcast(NettyMessage message);

    /**
     * 根据管道 ID 移除管道
     * @param ctx @link{ChannelHandlerContext}
     */
    void removeClient(ChannelHandlerContext ctx);
}
