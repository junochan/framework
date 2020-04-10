package com.juno.framework.netty.utils;

import com.alibaba.fastjson.JSON;
import com.juno.framework.netty.beans.NettyMessage;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Juno
 * @Date: 2020/4/9 14:08
 */
public class NettyMessageGenerator {

    public static final String ACK_PATH = "/mynety-callback";
    private static final NettyMessage heartBeat = new NettyMessage();

    private NettyMessageGenerator() {
    }

    public static NettyMessage genHeartBeatMessage() {
        return genHeartBeatMessage(null);
    }

    public static NettyMessage genHeartBeatMessage(String seq) {
        if (!StringUtils.isEmpty(seq)) {
            Map<String,String> params = new HashMap<>();
            params.put("seq",seq);
            heartBeat.setParams(params);
        }
        return heartBeat;
    }

    public static NettyMessage genAckMessage(String no, Object result) {
        NettyMessage ackMessage = new NettyMessage();
        ackMessage.setPath(ACK_PATH);
        ackMessage.getParams().put("no",no);
        ackMessage.getParams().put("result",TransferUtils.caseToStringValue(result));
        return ackMessage;
    }




}
