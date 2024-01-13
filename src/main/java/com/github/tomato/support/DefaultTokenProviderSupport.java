package com.github.tomato.support;

import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;

/**
 * 1.update 2024-01-13
 * @author liuxin
 * 2020-01-03 22:30
 */
@Slf4j
public class DefaultTokenProviderSupport extends AbstractTokenProvider {

    /**
     * 获取组件中指定的幂等键
     *
     * @param method 执行方法
     * @param args   方法参数
     * @return String
     */
    @Override
    public String findTomatoToken(Method method, Object[] args) {
        return super.findTomatoToken(method, args);
    }

}
