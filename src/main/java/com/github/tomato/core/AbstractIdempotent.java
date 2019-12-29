package com.github.tomato.core;

import com.github.tomato.util.Md5Tools;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * @author liuxin
 * 2019-12-29 22:37
 */
@Slf4j
public abstract class AbstractIdempotent implements Idempotent {

    /**
     * 幂等处理
     *
     * @param uniqueCode  唯一键
     * @param millisecond 毫秒
     * @return boolean
     */
    @Override
    public boolean idempotent(String uniqueCode, Long millisecond) {
        String uniqueToken = Md5Tools.md5(uniqueCode);
        log.debug("Idempotent: key[" + uniqueToken + "],expire:[" + millisecond + "ms]");
        boolean idempotent = doIdempotent(uniqueToken, millisecond);
        expire(uniqueToken, millisecond);
        return idempotent;
    }

    /**
     * 幂等处理
     *
     * @param uniqueCode        唯一键
     * @param millisecond       毫秒
     * @param exceptionSupplier 指定要抛的异常
     */
    @Override
    public <E extends Throwable> void idempotent(String uniqueCode, Long millisecond, Supplier<? extends E> exceptionSupplier) throws E {
        if (!idempotent(uniqueCode, millisecond)) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 幂等处理
     *
     * @param uniqueToken 加密后的唯一键
     * @param millisecond 毫秒
     * @return boolean
     */
    public abstract boolean doIdempotent(String uniqueToken, Long millisecond);

    /**
     * 过期key
     *
     * @param uniqueToken 加密后的唯一键
     * @param millisecond 毫秒
     */
    public abstract void expire(String uniqueToken, Long millisecond);
}
