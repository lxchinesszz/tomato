package com.github.tomato.core;

/**
 * @author liuxin
 * 2020-01-06 22:55
 */
public enum RepeatTypeEnum {

    /**
     * 滑动拦截窗口策略
     * 这种策略能有效的拦截暴力请求,因为每个相同的请求都会导致,这个请求窗口在不断的滑动,而把这些请求给拦截到
     */
    SLIDING_WINDOW,

    /**
     * 固定拦截窗口策略
     * 一般大多数,会使用固定窗口。
     */
    FIXED_WINDOW
}
