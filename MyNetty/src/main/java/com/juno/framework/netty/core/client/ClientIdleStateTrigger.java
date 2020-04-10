package com.juno.framework.netty.core.client;

import com.juno.framework.netty.beans.NettyMessage;
import com.juno.framework.netty.utils.NettyMessageGenerator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 用于捕获 @link{IdleState#WRITER_IDLE} 事件（未在指定时间内向服务器发送数据），
 * 然后向<code>Server</code>端发送一个心跳包。
 * @Author: Juno
 * @Date: 2020/4/9 13:59
 */
public class ClientIdleStateTrigger extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (IdleState.WRITER_IDLE == state) {
                NettyMessage nettyMessage = NettyMessageGenerator.genHeartBeatMessage();
                ctx.writeAndFlush(nettyMessage.toString());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
