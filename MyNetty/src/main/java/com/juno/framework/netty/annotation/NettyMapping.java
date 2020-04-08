package com.juno.framework.netty.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Author: Juno
 * @Date: 2020/4/7 9:02
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NettyMapping {

    String value() default "";

}
