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
     *
     * @return long
     */
    @AliasFor("value")
    long scope() default 300;

    /**
     * 控制范围
     *
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
     *
     * @return 错误提示
     */
    String message() default "repeat submit";

    /**
     * 拦截策略(默认滑动窗口)
     *
     * @return RepeatTypeEnum
     */
    RepeatTypeEnum type() default RepeatTypeEnum.SLIDING_WINDOW;
}
