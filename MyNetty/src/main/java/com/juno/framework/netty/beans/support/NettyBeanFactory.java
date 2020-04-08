package com.juno.framework.netty.beans.support;

/**
 * @Author: Juno
 * @Date: 2020/4/7 10:00
 */
public interface NettyBeanFactory {

    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;

}
