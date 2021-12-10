package com.github.tomato.support;

import java.lang.reflect.Method;

/**
 * 如果需要处理拦截的请求,给出特殊的返回值,可以实现这个接口
 *
 * @author liuxin
 * 2020-01-04 22:03
 * @see RepeatToInterceptSupport
 */
public class DefaultRepeatToInterceptSupport implements RepeatToInterceptSupport {

    /**
     * 内置不实现直接抛异常阻断
     *
     * @param method 当前方法
     * @param args   方法入参
     * @return Object
     */
    @Override
    public Object proceed(Method method, Object[] args) {
        return null;
    }
}
