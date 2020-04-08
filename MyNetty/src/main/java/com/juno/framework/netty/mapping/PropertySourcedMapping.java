package com.juno.framework.netty.mapping;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * @Author: Juno
 * @Date: 2020/4/8 13:54
 */
@Target({ ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface PropertySourcedMapping {
    String propertyKey();
    String value();
}