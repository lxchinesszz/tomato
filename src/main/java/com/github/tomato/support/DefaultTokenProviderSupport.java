package com.github.tomato.support;

import com.github.tomato.annotation.TomatoToken;
import com.github.tomato.annotation.TomatoTokenFrom;
import com.github.tomato.util.BaseTypeTools;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
/**
 * @author liuxin
 * 2020-01-03 22:30
 */
public class DefaultTokenProviderSupport implements TokenProviderSupport {


    public String findTomatoToken(Method method, Object[] args) throws Exception {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            if (arg == null) {
                continue;
            }
            Class<?> argClass = arg.getClass();
            boolean baseType = BaseTypeTools.isBaseType(argClass, true);
            TomatoToken tomatoToken = AnnotationUtils.findAnnotation(parameter, TomatoToken.class);
            //1. 基本类型直接返回
            if (tomatoToken != null && baseType) {
                return String.valueOf(arg);
            }
            //2. 对象类型会从获取该属性
            if (tomatoToken != null) {
                String tokenFieldName = tomatoToken.value();
                if (StringUtils.isEmpty(tokenFieldName)) {
                    throw new RuntimeException("if parameter is object type,@TomatoToken must statement " +
                            "attribute: value or " +
                            "uniqueCode");
                }
                Field tokenField = ReflectionUtils.findField(argClass, tokenFieldName);
                if (tokenField == null) {
                    String errorMsg = String.format("Don't find ${%s} in %s", tokenFieldName, arg.getClass());
                    throw new RuntimeException(errorMsg);
                }
                if (!tokenField.isAccessible()) {
                    tokenField.setAccessible(true);
                }
                Object tokenFieldValue = tokenField.get(arg);
                if (!BaseTypeTools.isBaseType(tokenFieldValue.getClass(), true)) {
                    //如果不是基本类型错误提示
                    throw new RuntimeException("Token may be base type,not is object type");
                }
                return String.valueOf(tokenFieldValue);
            }
            //3. 如果类型是Token
            if (arg instanceof HttpServletRequest) {
                TomatoTokenFrom tomatoTokenFrom = AnnotationUtils.findAnnotation(parameter, TomatoTokenFrom.class);
                if (tomatoTokenFrom != null) {
                    String tokenFromName = tomatoTokenFrom.value();
                    return ((HttpServletRequest) arg).getParameter(tokenFromName);
                }
            }
        }
        return null;
    }
}
