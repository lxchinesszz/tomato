package com.github.tomato.util;

/**
 * @author liuxin
 * 2023/2/22 16:01
 */
public class BusinessException extends RuntimeException {

    private ExDefinition exDefinition;

    public BusinessException() {
    }

    public BusinessException(ExDefinition code) {
        super(code.getDesc());
        this.exDefinition = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }


    public ExDefinition getExDefinition() {
        return exDefinition;
    }
}
