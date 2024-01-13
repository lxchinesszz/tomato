package com.github.tomato.exception;

/**
 * 当重复请求会默认抛出这个异常
 * 1.update 2024-01-13
 *
 * @author liuxin
 * 2020-01-06 22:57
 */
public class RepeatOptException extends RuntimeException {

    public RepeatOptException() {
        super();
    }

    public RepeatOptException(String message) {
        super(message);
    }

}
