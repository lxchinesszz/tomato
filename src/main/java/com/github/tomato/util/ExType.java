package com.github.tomato.util;

/**
 * @author liuxin
 * 2023/2/22 16:02
 */

public enum ExType {
    SYSTEM_ERROR(10),
    SYSTEM_BUSY(11),
    BUSINESS_ERROR(20);

    private int code;

    private ExType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
