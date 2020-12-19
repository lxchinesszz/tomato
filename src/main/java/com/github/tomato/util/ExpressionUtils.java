package com.github.tomato.util;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author liuxin
 * 2020/12/19 9:08 下午
 */
public class ExpressionUtils {

    /**
     * 根据el表达式获取值
     *
     * @param el         el表达式
     * @param rootObject 信息
     * @return Object
     */
    public static Object getElValue(String el, Object rootObject) {
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        Expression expression = spelExpressionParser.parseExpression(el);
        EvaluationContext context = new StandardEvaluationContext(rootObject);
        return expression.getValue(context);
    }
}
