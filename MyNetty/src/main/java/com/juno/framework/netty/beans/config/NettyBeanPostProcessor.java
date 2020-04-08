package com.juno.framework.netty.beans.config;

/**
 * @Author: Juno
 * @Date: 2020/4/7 10:42
 */
public class NettyBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

}
