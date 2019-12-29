package com.github.tomato.core;

import java.util.function.Supplier;

/**
 * @author liuxin
 * 2019-12-29 22:30
 */
public interface Idempotent {

    /**
     * @param uniqueCode  唯一码
     * @param millisecond 幂等事件
     * @return Boolean
     */
    boolean idempotent(String uniqueCode, Long millisecond);

    /**
     * @param uniqueCode        唯一码
     * @param millisecond       幂等事件
     * @param exceptionSupplier 异常生成
     * @param <E>               异常泛型
     * @throws E 泛型
     */
    <E extends Throwable> void idempotent(String uniqueCode, Long millisecond, Supplier<? extends E> exceptionSupplier) throws E;
}
