package com.github.tomato.util;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Spring El表达式工具的封装
 * 1.update 2024-01-13
 *
 * @author liuxin
 * 2020/12/19 9:08 下午
 */
public class ExpressionUtils {

    /**
     * 根据el表达式获取值(对象直接就是根元素)
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

    /**
     * 根据el表达式获取值(即是跟元素也是变量元素,对象变量是c,占位符为${})
     *
     * @param el         el表达式
     * @param rootObject 信息
     * @return Object
     */
    public static Object getThisElValue(String el, Object rootObject) {
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        Expression expression = spelExpressionParser.parseExpression(el, new TemplateParserContext("${", "}"));
        EvaluationContext context = new StandardEvaluationContext(rootObject);
        context.setVariable("c", rootObject);
        return expression.getValue(context);
    }
}

