package com.github.tomato.support;

import com.github.tomato.annotation.TomatoToken;
import com.github.tomato.util.BaseTypeTools;
import org.springframework.core.annotation.AnnotationUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuxin
 * 2020-01-04 21:14
 */
public abstract class AbstractTokenProvider implements TokenProviderSupport {

    protected static Map<Class, Field> classFieldCache = new ConcurrentHashMap<>();

    enum ParameterType {
        BASE_TYPE,
        OBJECT,
        HTTP_REQUEST
    }


    protected ParameterType typeArgParameter(Object arg) {
        if (arg instanceof HttpServletRequest) {
            return AbstractTokenProvider.ParameterType.HTTP_REQUEST;
        } else {
            boolean baseType = BaseTypeTools.isBaseType(arg.getClass(), true);
            return baseType ? AbstractTokenProvider.ParameterType.BASE_TYPE : AbstractTokenProvider.ParameterType.OBJECT;
        }
    }

    protected TomatoToken findTomatoToken(Parameter parameter) {
        return AnnotationUtils.findAnnotation(parameter, TomatoToken.class);
    }
}
