package com.github.tomato.util;

/**
 * @author liuxin
 * 2020-01-03 22:33
 */
public class BaseTypeTools {
    /**
     * 判断对象属性是否是基本数据类型,包括是否包括string
     *
     * @param className 要判断的类
     * @param incString 是否包括string判断,如果为true就认为string也是基本数据类型
     * @return boolean
     */
    public static boolean isBaseType(Class<?> className, boolean incString) {
        if (incString && className.equals(String.class)) {
            return true;
        }
        return className.equals(Integer.class) ||
                className.equals(int.class) ||
                className.equals(Byte.class) ||
                className.equals(byte.class) ||
                className.equals(Long.class) ||
                className.equals(long.class) ||
                className.equals(Double.class) ||
                className.equals(double.class) ||
                className.equals(Float.class) ||
                className.equals(float.class) ||
                className.equals(Character.class) ||
                className.equals(char.class) ||
                className.equals(Short.class) ||
                className.equals(short.class) ||
                className.equals(Boolean.class) ||
                className.equals(boolean.class);
    }
}
