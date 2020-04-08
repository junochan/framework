package com.juno.framework.netty.dispatcher;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.juno.framework.netty.annotation.NettyController;
import com.juno.framework.netty.annotation.NettyMapping;
import com.juno.framework.netty.beans.NettyMessage;
import com.juno.framework.netty.context.NettyApplicationContext;
import com.juno.framework.netty.exception.NettyFwException;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Juno
 * @Date: 2020/4/7 13:55
 */
@Slf4j
public class DefaultNettyDispatcher implements NettyDispatcher {

    private NettyApplicationContext context;
    private Map<String,NettyHandleMapping> handlerMappings = Maps.newConcurrentMap();

    public DefaultNettyDispatcher(ApplicationContext applicationContext,String configLocations) {
        init(applicationContext,configLocations);
    }

    @Override
    public void init(ApplicationContext applicationContext,String configLocations) {
        context = new NettyApplicationContext(applicationContext,configLocations);
        initStrategies(context);
    }

    private void initStrategies(NettyApplicationContext context) {
        //初始化handlerMapping
        initHandlerMappings(context);

    }

    private void initHandlerMappings(NettyApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                if (null == controller) {
                    continue;
                }
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(NettyController.class)) {
                    continue;
                }
                String baseUrl = "";
                if (clazz.isAnnotationPresent(NettyMapping.class)) {
                    NettyMapping nettyMapping = clazz.getAnnotation(NettyMapping.class);
                    baseUrl = nettyMapping.value();
                }
                for (Method method : clazz.getMethods()) {
                    if (!method.isAnnotationPresent(NettyMapping.class)) {
                        continue;
                    }
                    NettyMapping nettyMapping = method.getAnnotation(NettyMapping.class);
                    String path = ("/" + baseUrl + "/" + nettyMapping.value().replaceAll("\\*",".*")).replaceAll("/+", "/");
                    Map<String, Integer> parameterNames = getParameterNames(method);
                    this.handlerMappings.put(path,new NettyHandleMapping(controller,method,parameterNames));
                    log.info("Mapped " + path + "," + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String,Integer> getParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Map<String,Integer> parameterNames = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (!param.isNamePresent()) {
                return null;
            }
            parameterNames.put(param.getName(),i);
            if (parameterTypes[i].isAssignableFrom(ChannelHandlerContext.class)) {
                parameterNames.put(ChannelHandlerContext.class.getName(),i);
            }

        }
        return parameterNames;
    }


    @Override
    public void doDispatch(ChannelHandlerContext ctx, NettyMessage msg) {
        NettyHandleMapping handler = getHandler(msg.getPath());
        if (null == handler) {
            throw new NettyFwException("找不到对应的mapping");
        }
        Class<?>[] paramsTypes = handler.getMethod().getParameterTypes();
        Map<String, Integer> paramsIndex = handler.getParams();
        Object [] paramValues = new Object[paramsTypes.length];

        Map<String, String> requestParams = msg.getParams();
        for (Map.Entry<String, String> param : requestParams.entrySet()) {
            String key = param.getKey();
            if (!paramsIndex.containsKey(key)) {
                continue;
            }
            int index = paramsIndex.get(key);
            String value = param.getValue();
            paramValues[index] = caseStringValue(value,paramsTypes[index]);
        }

        if (paramsIndex.containsKey(ChannelHandlerContext.class.getName())) {
            paramValues[paramsIndex.get(ChannelHandlerContext.class.getName())] = ctx;
        }

        Object result = null;
        try {
            result = handler.getMethod().invoke(handler.getController(), paramValues);
        } catch (IllegalAccessException e) {
            throw new NettyFwException("无法访问方法:" + handler.getMethod().getName());
        } catch (InvocationTargetException e) {
            throw new NettyFwException("参数格式不正确");
        }
        if (null != result) {
            // 将结果返回给客户端

        }
    }

    private Object caseStringValue(String value, Class<?> paramsType) {
        if (String.class == paramsType) {
            return value;
        } else if (Integer.class == paramsType) {
            return Integer.valueOf(value);
        } else if (Boolean.class == paramsType) {
            return Boolean.valueOf(value);
        } else if (Double.class == paramsType) {
            return Double.valueOf(value);
        } else if (Float.class == paramsType) {
            return Float.valueOf(value);
        }
        return JSON.parseObject(value,paramsType);
    }

    private NettyHandleMapping getHandler(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        return handlerMappings.get(path);
    }

    @Override
    public Map<String,NettyHandleMapping> getHandlerMappings() {
        return handlerMappings;
    }
}
