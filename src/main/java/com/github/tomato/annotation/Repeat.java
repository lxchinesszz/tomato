package com.github.tomato.annotation;

import com.github.tomato.core.RepeatTypeEnum;
import com.github.tomato.exception.RepeatOptException;
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

    /**
     * 控制范围
     * 默认时间300ms
     * @return long
     */
    @AliasFor("value")
    long scope() default 300;

    /**
     * 控制范围
     * 默认时间300ms
     * @return long
     */
    @AliasFor("scope")
    long value() default 300;

    /**
     * 返回指定异常
     *
     * @return Class
     */
    Class<? extends Exception> throwable() default RepeatOptException.class;

    /**
     * 错误提示
     * 拦截后返回的错误提示
     * @return 错误提示
     */
    String message() default "repeat submit";

    /**
     * 拦截策略(默认滑动窗口)
     * @see RepeatTypeEnum
     * @return RepeatTypeEnum
     */
    RepeatTypeEnum type() default RepeatTypeEnum.SLIDING_WINDOW;

    /**
     * 方法锁时间(ms)
     *
     * @return Long
     */
    long methodLock() default 60 * 1000;

    /**
     * 从请求头中取值. 【web环境内生效】
     * 当需要从请求头中获取幂等键的时候,可以在这里指定请求头。
     * 此时会从Request的请求中获取数据
     * @return String
     */
    String headValue() default "";
}
