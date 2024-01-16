package com.github.tomato.util;

import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author liuxin
 * 2023/3/12 12:02
 */
public class Enums {

    public static <E extends Enum<?>> E ofByCode(Integer code, Class<E> e) {
        return toMap(e).get(code);
    }

    @SneakyThrows
    public static <K, E extends Enum<?>> Map<Integer, E> toMap(Class<E> enumType) {
        final Method values = enumType.getMethod("values");
        Map<Integer, E> map = new WeakHashMap<>();
        @SuppressWarnings("unchecked")
        E[] temporaryConstants = (E[]) values.invoke(null);
        for (E temporaryConstant : temporaryConstants) {
            if (!(temporaryConstant instanceof CommonEnum)) {
                throw ExFactory.throwBusiness("枚举:{},未继承CommonEnum", enumType);
            }
            map.put(((CommonEnum) temporaryConstant).getCode(), temporaryConstant);
        }
        return map;
    }
}
