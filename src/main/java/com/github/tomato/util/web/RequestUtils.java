package com.github.tomato.util.web;

import com.github.tomato.util.IpUtils;
import com.github.tomato.util.RequiredUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author liuxin 2023/4/18 22:16
 */
@Slf4j
public class RequestUtils {

    public static String getRequestToken() {
        // 1. 先从请求参数中获取验证
        String token = RequestUtils.getRequestParameter("token");
        // 2. 没有就从请求头中获取
        if (StringUtils.isEmpty(token)) {
            String tokenName = "token";
            token = RequestUtils.getRequestHeader(tokenName);
            // 3. 最后从cookie中获取
            if (StringUtils.isEmpty(token)) {
                token = CookieUtils.getCookie(tokenName);
                if (token == null) {
                    token = "";
                }
            }
        }
        return token;
    }

    /**
     * 使用简单字符串判断，性能比正则好
     *
     * @return boolean
     */
    public static boolean isLocationRequest() {
        StringBuffer requestURL = RequestUtils.getRequest().getRequestURL();
        // 检查请求 URL 是否以指定的子字符串开头
        boolean b = requestURL.toString().startsWith("http://localhost")
                || requestURL.toString().startsWith("http://127.0.0.1")
                || requestURL.toString().startsWith("http://" + IpUtils.getNetIp());
        // 如果条件为真，记录一条消息并跳过鉴权
        if (b) {
            log.info("本地请求:{}, 跳过鉴权", requestURL);
        } else {
            log.error("本地请求:{}, 鉴权拦截", requestURL);
        }
        return b;
    }

    public static HandlerMethod getHandlerMethod(Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return null;
        } else {
            return ((HandlerMethod) handler);
        }
    }

    public static String getRequestHeader(String headerName) {
        return RequiredUtils.orElse(getRequest(), o -> {
            return o.getHeader(headerName);
        }, null);
    }

//    public static String setRequestHeader(String headerName,String value) {
//        return RequiredUtils.orElse(getRequest(), o -> {
//            return o.geth(headerName);
//        }, null);
//    }


    public static String getRequestParameter(String token) {
        return RequiredUtils.orElse(getRequest(), o -> {
            return o.getParameter(token);
        }, null);
    }

    public static boolean isWebHandler(Object handler) {
        return handler instanceof HandlerMethod;
    }

    public static boolean isNotWebHandler(Object handler) {
        return !isWebHandler(handler);
    }

    @Data
    @AllArgsConstructor
    public static class UserRequestInfo {

        /**
         * 用户id
         */
        private String ip;

        /**
         * 浏览器信息
         */
        private String userAgent;

        /**
         * 系统
         */
        private String os;
    }

    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return Objects.nonNull(requestAttributes) ? ((ServletRequestAttributes) requestAttributes).getRequest() : null;
    }

    public static HttpServletRequest getRequest() {
        return getHttpServletRequest();
    }

    public static UserRequestInfo getUserRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String os = null;
        String browser = null;

        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();

            if (userAgent.contains("windows")) {
                os = "Windows";
            } else if (userAgent.contains("mac")) {
                os = "Mac";
            } else if (userAgent.contains("x11")) {
                os = "Unix";
            } else if (userAgent.contains("android")) {
                os = "Android";
            } else if (userAgent.contains("iphone")) {
                os = "iPhone";
            } else {
                os = "UnKnown";
            }

            if (userAgent.contains("edge")) {
                browser = "Edge";
            } else if (userAgent.contains("msie")) {
                browser = "IE";
            } else if (userAgent.contains("firefox")) {
                browser = "Firefox";
            } else if (userAgent.contains("safari")) {
                browser = "Safari";
            } else if (userAgent.contains("Chrome")) {
                browser = "Chrome";
            } else if (userAgent.contains("chrome")) {
                browser = "Chrome";
            } else {
                if (userAgent.contains("opera")) {
                    browser = "Opera";
                } else {
                    browser = "UnKnown";
                }
            }
        }
        return new UserRequestInfo(request.getRemoteAddr(), browser, os);
    }
}
