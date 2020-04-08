package com.juno.framework.netty.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.juno.framework.netty.annotation.NettyController;
import com.juno.framework.netty.dispatcher.NettyDispatcher;
import com.juno.framework.netty.dispatcher.NettyHandleMapping;
import com.juno.framework.netty.mapping.PropertySourcedMapping;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Juno
 * @Date: 2020/4/8 13:54
 */
@Controller
public class MyNettyController {

    private NettyDispatcher nettyDispatcher;

    public MyNettyController(NettyDispatcher nettyDispatcher) {
        this.nettyDispatcher = nettyDispatcher;
    }

    @RequestMapping("/netty-api")
    @PropertySourcedMapping(
            propertyKey="my-netty.doc-path",
            value="${my-netty.doc-path}"
    )
    @ResponseBody
    public ResponseEntity<JSONObject> showAppInfo(HttpServletRequest request, HttpServletResponse response) {
        final Map<String, NettyHandleMapping> handlerMappings = nettyDispatcher.getHandlerMappings();
        JSONObject result = new JSONObject();
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
        result.put("apis",apis);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    public Map<String,Object> getRealParams(final NettyHandleMapping mapping) {
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
