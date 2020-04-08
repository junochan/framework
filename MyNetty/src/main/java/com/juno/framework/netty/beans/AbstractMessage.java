package com.juno.framework.netty.beans;

/**
 * @Author: Juno
 * @Date: 2020/4/7 15:04
 */
public abstract class AbstractMessage<T> {



    abstract void parseMessage();

    abstract String getPath();

    abstract T getMessage();

}
