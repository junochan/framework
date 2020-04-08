package com.juno.framework.netty.dispatcher;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author: Juno
 * @Date: 2020/4/7 9:35
 */
public class NettyHandleMapping {

    //保存方法对应的实例
    private Object controller;
    //保存映射的方法
    private Method method;
    // 保存方法的参数
    private Map<String,Integer> params;

    public NettyHandleMapping(Object controller, Method method, Map<String, Integer> params) {
        this.controller = controller;
        this.method = method;
        this.params = params;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Map<String, Integer> getParams() {
        return params;
    }

    public void setParams(Map<String, Integer> params) {
        this.params = params;
    }
}
