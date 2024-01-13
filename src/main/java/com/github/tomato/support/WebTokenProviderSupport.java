package com.github.tomato.support;

import com.github.tomato.annotation.Repeat;
import com.github.tomato.exception.NotWebEnvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 1.update 2024-01-13
 * @author liuxin
 * 2020-01-03 22:30
 */
@Slf4j
public class WebTokenProviderSupport extends AbstractTokenProvider {

    /**
     * 获取组件中指定的幂等键
     *
     * @param method 执行方法
     * @param args   方法参数
     * @return String
     */
    @Override
    public String findTomatoToken(Method method, Object[] args) {
        return super.findTomatoToken(method, args);
    }

    @Override
    public String findTomatoHeadToken(Method method, Object[] args) throws Exception {
        Repeat repeat = AnnotationUtils.findAnnotation(method, Repeat.class);
        assert repeat != null;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            throw new NotWebEnvException("HttpServletRequest 不存在");
        } else {
            HttpServletRequest request = requestAttributes.getRequest();
            return request.getHeader(repeat.headValue());
        }
    }
}
