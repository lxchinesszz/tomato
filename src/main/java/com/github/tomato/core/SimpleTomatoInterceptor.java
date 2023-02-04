package com.github.tomato.core;

import com.github.tomato.annotation.Repeat;
import com.github.tomato.support.RepeatToInterceptSupport;
import com.github.tomato.support.TokenProviderSupport;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;

/**
 * @author liuxin
 * 2023/2/4 19:05
 */
@Aspect
public class SimpleTomatoInterceptor extends TomatoV2Interceptor {

    public SimpleTomatoInterceptor(Idempotent idempotent, TokenProviderSupport tokenProviderSupport, RepeatToInterceptSupport repeatToInterceptSupport) {
        super(idempotent,tokenProviderSupport,repeatToInterceptSupport);
    }

    @Override
    protected String tomatoToken(Repeat repeat, Method method, Object[] args) throws Exception {
        //2. 获取唯一键
        return tokenProviderSupport.findTomatoToken(method, args);
    }
}
