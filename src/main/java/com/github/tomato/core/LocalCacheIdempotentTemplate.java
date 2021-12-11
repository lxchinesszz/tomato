package com.github.tomato.core;

import com.github.tomato.core.cache.LocalCache;
import lombok.extern.slf4j.Slf4j;


/**
 * 一个基于本地缓存的拦截方式,该方式只会在没有配置redis的情况下被自动启用。
 * 注意该方式不具备分布式的能力
 *
 * @author liuxin
 * 2021/12/10 8:32 下午
 */
@Slf4j
public class LocalCacheIdempotentTemplate extends AbstractIdempotent {

    /**
     * 本地缓存
     */
    private static final LocalCache LOCAL_CACHE = new LocalCache();

    /**
     * 新增缓存
     *
     * @param uniqueToken 加密后的唯一键
     * @param millisecond 毫秒
     * @return boolean
     */
    @Override
    public boolean doIdempotent(String uniqueToken, Long millisecond) {
        return LOCAL_CACHE.set(uniqueToken, millisecond);
    }

    /**
     * 续期
     *
     * @param uniqueToken 加密后的唯一键
     * @param millisecond 毫秒
     */
    @Override
    public void expire(String uniqueToken, Long millisecond) {
        LOCAL_CACHE.addExpire(uniqueToken, millisecond);
    }

    /**
     * 移除key
     *
     * @param uniqueToken 加密后的唯一键
     * @return boolean
     */
    @Override
    public boolean delKey(String uniqueToken) {
        LOCAL_CACHE.remove(uniqueToken);
        return true;
    }
}
