package com.juno.framework.netty.beans;

/**
 * @Author: Juno
 * @Date: 2020/4/7 10:03
 */
public class NettyBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public NettyBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
        if (null != this.wrappedInstance) {
            this.wrappedClass = this.wrappedInstance.getClass();
        }
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrappedClass;
    }

}
