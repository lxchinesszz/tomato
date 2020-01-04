package com.github.tomato.support;

import com.github.tomato.annotation.TomatoToken;
import com.github.tomato.util.BaseTypeTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author liuxin
 * 2020-01-03 22:30
 */
@Slf4j
public class DefaultTokenProviderSupport extends AbstractTokenProvider {


    @Override
    public String findTomatoToken(Method method, Object[] args) throws Exception {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            if (arg == null) continue;
            AbstractTokenProvider.ParameterType parameterType = typeArgParameter(arg);
            TomatoToken tomatoToken = findTomatoToken(parameter);
            if (tomatoToken == null) continue;
            String tokenName = tomatoToken.value();
            switch (parameterType) {
                case HTTP_REQUEST:
                    return ((HttpServletRequest) arg).getParameter(tokenName);
                case OBJECT:
                    Class<?> argClass = arg.getClass();
                    Field field = classFieldCache.get(argClass);
                    if (field == null) {
                        field = ReflectionUtils.findField(argClass, tokenName);
                        if (field == null) {
                            String errorMsg = String.format("Don't find %s in %s", tokenName, argClass);
                            throw new RuntimeException(errorMsg);
                        }
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        classFieldCache.put(argClass, field);
                    }
                    Object tokenFieldValue = field.get(arg);
                    if (tokenFieldValue == null) continue;
                    if (!BaseTypeTools.isBaseType(tokenFieldValue.getClass(), true)) {
                        //如果不是基本类型错误提示
                        throw new RuntimeException("Token may be base type,not is object type");
                    }
                    return String.valueOf(tokenFieldValue);
                case BASE_TYPE:
                    return String.valueOf(arg);
                default:
                    break;
            }
        }
        return null;
    }
}
