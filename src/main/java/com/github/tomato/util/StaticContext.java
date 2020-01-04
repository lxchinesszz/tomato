package com.github.tomato.util;

import com.github.tomato.constant.TomatoConstant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuxin
 * 2020-01-04 22:11
 */
public final class StaticContext {

    private static final ThreadLocal<Map<String, String>> context = new InheritableThreadLocal<>();

    private static Map<String, String> getAttributeAsMap() {
        Map<String, String> stringStringMap = context.get();
        if (stringStringMap == null) {
            stringStringMap = new ConcurrentHashMap<>();
            context.set(stringStringMap);
        }
        return stringStringMap;
    }

    public static String getToken() {
        return getAttributeAsMap().get(TomatoConstant.TOKEN);
    }

    public static void setToken(String tokenValue) {
        addAttribute(TomatoConstant.TOKEN, tokenValue);
    }

    public static void addAttribute(String key, String value) {
        getAttributeAsMap().put(key, value);
    }

    public static void clear() {
        context.remove();
    }
}
