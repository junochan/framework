package com.juno.framework.netty.beans;

import java.util.Map;

/**
 * @Author: Juno
 * @Date: 2020/4/7 15:01
 */
public class NettyMessage {

    private String no;
    private String path;
    private Map<String,String> params;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
