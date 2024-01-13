package com.github.tomato.util.table;

import java.lang.annotation.*;

/**
 * 动态表头
 *
 * @author liuxin 2021/4/23 2:26 下午
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableHeader {

    Integer JSON = 1;

    Integer PLAN_TEXT = 0;

    /**
     * 自动描述
     *
     * @return String
     */
    String value() default "";

    /**
     * 允许用户自定义一个标记,用来和前端做约定
     *
     * @return String
     */
    String tag() default "";

    /**
     * 1 json数据类型 0 非json数据
     *
     * @return int
     */
    int tagType() default 0;

    /**
     * 是否时间戳
     *
     * @return boolean
     */
    boolean timestamp() default false;

    /**
     * 是否有子表格
     * 
     * @return boolean
     */
    boolean sub() default false;

}
