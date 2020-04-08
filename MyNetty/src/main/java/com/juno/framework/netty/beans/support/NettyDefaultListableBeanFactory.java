package com.juno.framework.netty.beans.support;

import com.juno.framework.netty.beans.config.NettyBeanDefinition;
import com.juno.framework.netty.context.support.AbstractNettyApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Juno
 * @Date: 2020/4/7 11:00
 */
public class NettyDefaultListableBeanFactory extends AbstractNettyApplicationContext implements ApplicationContextAware {

    //存储注册信息的BeanDefinition,伪IOC容器
    protected final Map<String, NettyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (null == this.applicationContext) {
            this.applicationContext = applicationContext;
        }
    }
}
