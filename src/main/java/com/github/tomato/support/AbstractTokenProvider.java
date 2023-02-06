package com.github.tomato.support;

import com.github.tomato.annotation.TomatoToken;
import com.github.tomato.util.BaseTypeTools;
import com.github.tomato.util.ExpressionUtils;
import org.springframework.core.annotation.AnnotationUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于解析Controller中的请求信息将,不同的请求转换成指定的枚举
 * eg: 基本类型、对象类型、HttpRequest类型
 * @author liuxin
 * 2020-01-04 21:14
 */
public abstract class AbstractTokenProvider implements TokenProviderSupport {

    enum ParameterType {

        /**
         * 基本类型
         */
        BASE_TYPE,

        /**
         * 对象类型
         */
        OBJECT,

        /**
         * http请求
         */
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


    /**
     * 获取组件中指定的幂等键
     *
     * @param method 执行方法
     * @param args   方法参数
     * @return String
     */
    @Override
    public String findTomatoToken(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            if (arg == null) {
                continue;
            }
            ParameterType parameterType = typeArgParameter(arg);
            TomatoToken tomatoToken = findTomatoToken(parameter);
            if (tomatoToken == null) {
                continue;
            }
            String tokenElValue = tomatoToken.value();
            switch (parameterType) {
                case HTTP_REQUEST:
                    // 1. 如果是request对象,直接当做属性查询
                    String requestToken = ((HttpServletRequest) arg).getParameter(tokenElValue);
                    return prefixToken(requestToken, tomatoToken.prefix());
                case OBJECT:
                    // 2. 如果是对象类型,使用SpringEL表达式解析
                    Object tokenValue = ExpressionUtils.getThisElValue(tokenElValue, arg);
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
                    return prefixToken(String.valueOf(arg), tomatoToken.prefix());
                default:
                    break;
            }
        }
        return null;
    }

    private static String prefixToken(Object arg, String prefix) {
        if (Objects.isNull(arg)) {
            return null;
        } else {
            return prefix + String.valueOf(arg);
        }
    }
}
