package com.github.tomato.core;

import com.github.tomato.annotation.Repeat;
import com.github.tomato.exception.ElSyntaxException;
import com.github.tomato.exception.RepeatOptException;
import com.github.tomato.support.RepeatToInterceptSupport;
import com.github.tomato.support.TokenProviderSupport;
import com.github.tomato.util.StaticContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 拦截器拦截被幂等的接口
 * 优化逻辑,解决方法执行耗时,导致幂等拦截失效。
 *
 * @author liuxin
 */
@Aspect
public class TomatoV2Interceptor {

    private Idempotent idempotent;

    private TokenProviderSupport tokenProviderSupport;

    private RepeatToInterceptSupport interceptSupport;

    public TomatoV2Interceptor() {
    }

    public TomatoV2Interceptor(Idempotent idempotent, TokenProviderSupport tokenProviderSupport, RepeatToInterceptSupport interceptSupport) {
        this.idempotent = idempotent;
        this.tokenProviderSupport = tokenProviderSupport;
        this.interceptSupport = interceptSupport;
    }

    private String methodLockTomatoToken(Method method, String tomatoToken) {
        return tomatoToken + method.getName();
    }

    @Around("@annotation(com.github.tomato.annotation.Repeat)")
    public Object doAround(ProceedingJoinPoint pjp) {
        //1. 获取唯一键的获取方式
        Object[] args = pjp.getArgs();
        Method method = findMethod(pjp.getSignature());
        Repeat repeat = AnnotationUtils.getAnnotation(method, Repeat.class);
        Object result;
        Exception e;
        String tomatoToken = "";
        try {
            // 从
            assert repeat != null;
            if (org.codehaus.plexus.util.StringUtils.isNotBlank(repeat.headValue())) {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (Objects.isNull(requestAttributes)) {
                    throw new RuntimeException("HttpServletRequest 不存在");
                } else {
                    HttpServletRequest request = requestAttributes.getRequest();
                    return request.getHeader(repeat.headValue());
                }

            } else {
                //2. 获取唯一键
                tomatoToken = tokenProviderSupport.findTomatoToken(method, args);
            }
            // 如果为空直接报错
            if (StringUtils.isEmpty(tomatoToken)) {
                throw new ElSyntaxException("el语法错误:[" + Arrays.asList(args) + "]");
            }
            //3. 唯一键键不存在,直接执行
            if (idempotent(methodLockTomatoToken(method, tomatoToken), repeat.methodLock(), repeat)
                    && idempotent(tomatoToken, repeat.scope(), repeat)) {
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
                // 检查类型
                Class<?> returnType = method.getReturnType();
                if (!returnType.equals(proceed.getClass())) {
                    // 类型不匹配异常
                    throw new TypeMismatchException(proceed, returnType);
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
            if (!StringUtils.isEmpty(tomatoToken)) {
                // 移除本次方法锁
                idempotent.delIdempotent(methodLockTomatoToken(method, tomatoToken));
            }
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

}
