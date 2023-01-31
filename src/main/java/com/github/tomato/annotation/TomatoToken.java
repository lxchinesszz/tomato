package com.github.tomato.annotation;

import java.lang.annotation.*;

/**
 * @author liuxin
 * 2020-01-03 22:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface TomatoToken {

    /**
     * http方式的不能使用el表达是,如果要添加前缀就在这里设置
     *
     * @return String
     */
    String prefix() default "";

    /**
     * 支持使用el表达式解析token
     *
     * @return String
     */
    String value() default "";

}
