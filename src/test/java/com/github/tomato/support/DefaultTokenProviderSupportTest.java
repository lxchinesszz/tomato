package com.github.tomato.support;

import com.github.tomato.annotation.Repeat;
import com.github.tomato.annotation.TomatoToken;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author liuxin
 * 2020/12/19 8:50 下午
 */
public class DefaultTokenProviderSupportTest {


    private Object parser(String el) {
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        Expression expression = spelExpressionParser.parseExpression(el);
        EvaluationContext context = new StandardEvaluationContext(new User("liuxin", 25, new Phone("17601234466")));
        return expression.getValue(context);
    }

    /**
     * 解析对象中的属性值
     */
    public void testSpringEl0() {
        System.out.println(parser("name"));
    }

    /**
     * 解析数据并处理
     */
    public void testSpringEl1() {
        System.out.println(parser("age+1"));
    }

    /**
     * 解析手机号
     */
    @Test
    public void testSpringEl2() throws Exception {
        System.out.println(parser("phone.phoneNo"));
    }

    /**
     * 注意示例中的方法都要是静态公有属性
     *
     * @param arg 任意对象
     * @return 返回值
     */
    public static Integer hash(Object arg) {
        return Objects.hash(arg);
    }

    public static String json(Object arg){
        return Objects.toString(arg) + "?";
    }



}