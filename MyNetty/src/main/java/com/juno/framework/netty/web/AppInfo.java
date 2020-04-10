package com.juno.framework.netty.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.juno.framework.netty.dispatcher.NettyHandleMapping;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Juno
 * @Date: 2020/4/10 17:54
 */
public class AppInfo {

    private static final JSONObject appInfo = new JSONObject();

    public static void setAppInfo(final Map<String, NettyHandleMapping> handlerMappings) {
        JSONArray apis = new JSONArray();
        JSONObject item;
        for (Map.Entry<String, NettyHandleMapping> entry : handlerMappings.entrySet()) {
            final String key = entry.getKey();
            final NettyHandleMapping value = entry.getValue();
            item = new JSONObject();
            item.put("path",key);
            item.put("param",getRealParams(value));
            apis.add(item);
        }
        appInfo.put("apis",apis);
    }

    public static JSONObject getAppInfo() {
        return appInfo;
    }

    private static Map<String,Object> getRealParams(final NettyHandleMapping mapping) {
        if (null == mapping || null == mapping.getParams()) {
            return null;
        }
        Map<String,Object> realParams = new HashMap<>();
        Class<?>[] parameterTypes = mapping.getMethod().getParameterTypes();
        int filterIndex = -1;
        if (mapping.getParams().containsKey(ChannelHandlerContext.class.getName())) {
            filterIndex = mapping.getParams().get(ChannelHandlerContext.class.getName());
        }
        for (Map.Entry<String, Integer> entry : mapping.getParams().entrySet()) {
            String key = entry.getKey();
            int index = entry.getValue();
            if (index == filterIndex) {
                continue;
            }
            realParams.put(key,parameterTypes[index].getName());
        }
        return realParams;
    }

}
