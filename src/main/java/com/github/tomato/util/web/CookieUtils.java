package com.github.tomato.util.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author liuxin 2023/4/22 15:20
 */
public class CookieUtils {

    public static String getCookie(String name) {
        HttpServletRequest request = RequestUtils.getRequest();
        if (Objects.nonNull(request)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (name.equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        }
        return null;
    }
}
