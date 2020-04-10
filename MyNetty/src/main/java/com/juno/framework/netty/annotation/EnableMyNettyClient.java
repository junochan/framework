package com.juno.framework.netty.annotation;

import com.juno.framework.netty.configuration.MyNettyClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: Juno
 * @Date: 2020/4/9 15:51
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MyNettyClientConfiguration.class)
public @interface EnableMyNettyClient {
}
