package com.github.tomato.core;

import com.github.tomato.annotation.Repeat;
import com.github.tomato.support.RepeatToInterceptSupport;
import com.github.tomato.support.TokenProviderSupport;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

@Aspect
public class TomatoInterceptor {

    private Idempotent idempotent;

    private TokenProviderSupport tokenProviderSupport;

//    private RepeatToInterceptSupport interceptSupport;

    public TomatoInterceptor() {
    }

    public TomatoInterceptor(Idempotent idempotent, TokenProviderSupport tokenProviderSupport) {
        this.idempotent = idempotent;
        this.tokenProviderSupport = tokenProviderSupport;
    }

    @Around("@annotation(com.github.tomato.annotation.Repeat)")
    public Object doAround(ProceedingJoinPoint pjp) {
        //1. 获取唯一键的获取方式
        Object[] args = pjp.getArgs();
        Signature signature = pjp.getSignature();
        Method method = findMethod(signature);
        Repeat repeat = findRepeat(method);
        Object result = null;
        try {
            //2. 获取唯一键
            String tomatoToken = tokenProviderSupport.findTomatoToken(method, args);
            //3. 唯一键键不存在,直接执行
            if (tomatoToken == null) {
                result = pjp.proceed();
            } else if (idempotent.idempotent(tomatoToken, repeat.scope())) {
                result = pjp.proceed();
            } else {
                //防重之后交给用户来处理
//                interceptSupport.provider();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return result;
    }

    private Method findMethod(Signature signature) {
        MethodSignature ms = (MethodSignature) signature;
        return ms.getMethod();
    }

    private Repeat findRepeat(Method method) {
        return AnnotationUtils.findAnnotation(method, Repeat.class);
    }

}
