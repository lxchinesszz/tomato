package com.github.tomato.support;

import com.github.tomato.annotation.TomatoToken;
import com.github.tomato.util.BaseTypeTools;
import com.github.tomato.util.ExpressionUtils;
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
    public String findTomatoToken(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            if (arg == null) {
                continue;
            }
            AbstractTokenProvider.ParameterType parameterType = typeArgParameter(arg);
            TomatoToken tomatoToken = findTomatoToken(parameter);
            if (tomatoToken == null) {
                continue;
            }
            String tokenElValue = tomatoToken.value();
            switch (parameterType) {
                case HTTP_REQUEST:
                    // 1. 如果是request对象,直接当做属性查询
                    return ((HttpServletRequest) arg).getParameter(tokenElValue);
                case OBJECT:
                    // 2. 如果是对象类型,使用SpringEL表达式解析
                    Object tokenValue = ExpressionUtils.getElValue(tokenElValue, arg);
                    if (tokenValue == null) {
                        continue;
                    }
                    // 3. 如果不是基本类型直接报错
                    if (!BaseTypeTools.isBaseType(tokenValue.getClass(), true)) {
                        //如果不是基本类型错误提示
                        throw new RuntimeException("Token may be base type,not is object type");
                    }
                    return String.valueOf(tokenValue);
                case BASE_TYPE:
                    return String.valueOf(arg);
                default:
                    break;
            }
        }
        return null;
    }
}
