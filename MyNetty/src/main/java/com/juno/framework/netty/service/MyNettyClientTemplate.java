package com.juno.framework.netty.service;

import com.juno.framework.netty.beans.NettyMessage;
import com.juno.framework.netty.exception.NettyFwException;

/**
 * @Author: Juno
 * @Date: 2020/4/10 14:17
 */
public interface MyNettyClientTemplate {

    void sendMessage(NettyMessage message);

    Object syncSendMessage(NettyMessage message) throws NettyFwException;

}
