package com.github.tomato.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author liuxin
 * 2020-01-03 22:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface TomatoTokenFrom {
    @AliasFor
    String value() default "";
}
