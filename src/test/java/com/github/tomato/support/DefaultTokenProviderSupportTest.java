package com.github.tomato.support;

import junit.framework.TestCase;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author liuxin
 * 2020/12/19 8:50 下午
 */
public class DefaultTokenProviderSupportTest extends TestCase {


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
    public void testSpringEl2() {
        System.out.println(parser("phone.phoneNo"));
    }

}