package com.github.tomato.support;


import java.lang.reflect.Method;

/**
 * @author liuxin
 * 2020-01-03 22:29
 */
public interface TokenProviderSupport {
    String findTomatoToken(Method method, Object[] args) throws Exception;
}