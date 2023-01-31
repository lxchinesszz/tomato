package com.github.tomato.support;

import com.github.tomato.annotation.TomatoToken;
import com.github.tomato.util.BaseTypeTools;
import com.github.tomato.util.ExpressionUtils;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
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
