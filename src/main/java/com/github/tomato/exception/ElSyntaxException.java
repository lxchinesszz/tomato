package com.github.tomato.exception;

/**
 * 如果你填写了错误的Spring EL表达式,将会遇到这个异常
 * 1.update 2024-01-13
 * @author liuxin
 * 2021/10/25 3:19 下午
 */
public class ElSyntaxException extends RuntimeException {

    public ElSyntaxException() {
        super();
    }

    public ElSyntaxException(String message) {
        super(message);
    }

}