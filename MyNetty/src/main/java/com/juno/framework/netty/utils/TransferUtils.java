package com.juno.framework.netty.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

/**
 * @Author: Juno
 * @Date: 2020/4/10 15:59
 */
public class TransferUtils {

    public static String toLowerFirstCase(String simpleName) {
        if (StringUtils.isEmpty(simpleName)) {
            return simpleName;
        }
        char [] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public static String caseToStringValue(Object result) {
        if (null == result) {
            return "";
        }
        if (result instanceof String) {
            return (String) result;
        } else if (result instanceof Integer || result instanceof Double || result instanceof Float || result instanceof Boolean) {
            return String.valueOf(result);
        }
        return JSON.toJSONString(result);
    }

}
