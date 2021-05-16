package com.github.tomato.util;

import com.github.tomato.support.Phone;
import com.github.tomato.support.User;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author liuxin
 * 2021/5/16 3:08 下午
 */
public class ExpressionUtilsTest {

    /**
     * 示例
     */
    @Test
    public void testExample() {
        // 表达式解析器
        User liuxin = new User("liuxin", 23,new Phone("123213321"));

        // 执行toString方法
        System.out.println(ExpressionUtils.getElValue("toString()", liuxin));
        System.out.println(ExpressionUtils.getThisElValue("${name}", liuxin));
        // 支持从根元素获取数据
        System.out.println(ExpressionUtils.getThisElValue("S_AO:${name}", liuxin));
        System.out.println(ExpressionUtils.getThisElValue("${name + '_后缀'}", liuxin));
        // 支持从变量元素获取数据，根元素 = c  #是变量，$是模板占位符
        System.out.println(ExpressionUtils.getThisElValue("${#c.name}, ${#c.age}", liuxin));
        // 获取toString方法
        System.out.println(ExpressionUtils.getThisElValue("${#c.toString()}", liuxin));
        // 获取值并处理
        System.out.println(ExpressionUtils.getThisElValue("${#c.age +'-'+ #c.age}", liuxin));
        // 获取值,并通过方法计算
        System.out.println(ExpressionUtils.getThisElValue("${T(Integer).parseInt(#c.age + 1)}", liuxin));
        // 计算哈希值
        System.out.println(ExpressionUtils.getThisElValue("${T(com.github.tomato.support.DefaultTokenProviderSupportTest).hash(#c.age + 1)}", liuxin));

        System.out.println(ExpressionUtils.getThisElValue("${T(com.github.tomato.support.DefaultTokenProviderSupportTest).json(#c)}", liuxin));

    }
}