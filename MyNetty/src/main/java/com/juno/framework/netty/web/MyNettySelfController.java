package com.juno.framework.netty.web;

import com.alibaba.fastjson.JSONObject;
import com.juno.framework.netty.annotation.NettyController;
import com.juno.framework.netty.annotation.NettyMapping;
import com.juno.framework.netty.mapping.PropertySourcedMapping;
import com.juno.framework.netty.service.MyNettyResponseCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Juno
 * @Date: 2020/4/8 13:54
 */
@Controller
@NettyController
public class MyNettySelfController {

    private MyNettyResponseCache responseCache;

    public MyNettySelfController(MyNettyResponseCache responseCache) {
        this.responseCache = responseCache;
    }

    @NettyMapping(value = "mynety-callback")
    public void ackMessage(String no,Object result) {
        responseCache.ackMessageSync(no, result);
    }

    @RequestMapping("/netty-api")
    @PropertySourcedMapping(
            propertyKey="my-netty.doc-path",
            value="${my-netty.doc-path}"
    )
    @ResponseBody
    public ResponseEntity<JSONObject> showAppInfo(HttpServletRequest request, HttpServletResponse response) {
        return new ResponseEntity<>(AppInfo.getAppInfo(),HttpStatus.OK);
    }


}
