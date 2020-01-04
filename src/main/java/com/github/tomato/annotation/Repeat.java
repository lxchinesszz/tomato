package com.github.tomato.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 防重范围时间单位毫秒(ms)
 *
 * @author liuxin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Repeat {

    @AliasFor("value")
    long scope() default 100;

    @AliasFor("scope")
    long value() default 100;
}
