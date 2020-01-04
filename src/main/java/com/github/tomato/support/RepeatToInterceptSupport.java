package com.github.tomato.support;

import java.lang.reflect.Method;

/**
 * @author liuxin
 * 2020-01-04 19:55
 */
public interface RepeatToInterceptSupport {

    /**
     * 当被拦截后,运行业务方进行二次处理
     *
     * @param method 当前方法
     * @param args   方法入参
     * @return Object 务必保证与Method签名一样的返回值类型
     */
    Object proceed(Method method, Object[] args);
}
