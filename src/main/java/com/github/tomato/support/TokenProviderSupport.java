package com.github.tomato.support;


import java.lang.reflect.Method;

/**
 * Token生成器
 *
 * @author liuxin
 * 2020-01-03 22:29
 */
public interface TokenProviderSupport {

    /**
     * 获取方法中指定的token信息
     *
     * @param method 执行方法
     * @param args   方法参数
     * @return String
     * @throws Exception 未知异常
     */
    String findTomatoToken(Method method, Object[] args) throws Exception;
}