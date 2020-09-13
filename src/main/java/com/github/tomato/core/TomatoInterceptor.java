package com.github.tomato.core;

import com.github.tomato.annotation.Repeat;
import com.github.tomato.exception.RepeatOptException;
import com.github.tomato.support.RepeatToInterceptSupport;
import com.github.tomato.support.TokenProviderSupport;
import com.github.tomato.util.StaticContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Aspect
public class TomatoInterceptor {

    private Idempotent idempotent;

    private TokenProviderSupport tokenProviderSupport;

    private RepeatToInterceptSupport interceptSupport;

    public TomatoInterceptor() {
    }

    public TomatoInterceptor(Idempotent idempotent, TokenProviderSupport tokenProviderSupport, RepeatToInterceptSupport interceptSupport) {
        this.idempotent = idempotent;
        this.tokenProviderSupport = tokenProviderSupport;
        this.interceptSupport = interceptSupport;
    }

    @Around("@annotation(com.github.tomato.annotation.Repeat)")
    public Object doAround(ProceedingJoinPoint pjp) {
        //1. 获取唯一键的获取方式
        Object[] args = pjp.getArgs();
        Method method = findMethod(pjp.getSignature());
        Repeat repeat = findRepeat(method);
        Object result = null;
        Exception e;
        try {
            //2. 获取唯一键
            String tomatoToken = tokenProviderSupport.findTomatoToken(method, args);
            //3. 唯一键键不存在,直接执行
            if (tomatoToken == null) {
                result = pjp.proceed();
            } else if (idempotent(tomatoToken, repeat.scope(), repeat)) {
                StaticContext.setToken(tomatoToken);
                result = pjp.proceed();
            } else {
                //防重之后交给用户来处理
                Object proceed = interceptSupport.proceed(method, args);
                if (proceed == null) {
                    Class<? extends Exception> throwable = repeat.throwable();
                    Constructor<? extends Exception> declaredConstructor = throwable.getDeclaredConstructor(String.class);
                    String message = repeat.message();
                    e = declaredConstructor.newInstance(message);
                    throw e;
                }
                result = proceed;
            }
        } catch (RepeatOptException rop) {
            throw rop;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        } finally {
            StaticContext.clear();
        }
        return result;
    }

    public boolean idempotent(String tomatoToken, Long millisecond, Repeat repeat) {
        RepeatTypeEnum typeEnum = repeat.type();
        if (RepeatTypeEnum.FIXED_WINDOW == typeEnum) {
            return idempotent.fixedIdempotent(tomatoToken, millisecond);
        } else if (RepeatTypeEnum.SLIDING_WINDOW == typeEnum) {
            return idempotent.idempotent(tomatoToken, millisecond);
        }
        return idempotent.idempotent(tomatoToken, millisecond);
    }

    private Method findMethod(Signature signature) {
        MethodSignature ms = (MethodSignature) signature;
        return ms.getMethod();
    }

    private Repeat findRepeat(Method method) {
        return AnnotationUtils.findAnnotation(method, Repeat.class);
    }
}
