package com.github.tomato.core;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author liuxin
 * 2021/12/11 12:54 下午
 */
public class LocalCacheIdempotentTemplateTest {

    @Test
    public void doIdempotent() throws Exception{
        LocalCacheIdempotentTemplate cache = new LocalCacheIdempotentTemplate();
        // true
        System.out.println(cache.doIdempotent("12312", 1000L));
        // false
        System.out.println(cache.doIdempotent("12312", 1000L));
        Thread.sleep(2000L);
        // true
        System.out.println(cache.doIdempotent("12312", 1000L));
    }

    @Test
    public void expire() throws Exception{
        LocalCacheIdempotentTemplate cache = new LocalCacheIdempotentTemplate();
        // true
        System.out.println(cache.doIdempotent("12312", 1000L));
        // false
        System.out.println(cache.doIdempotent("12312", 1000L));
        Thread.sleep(2000L);
        cache.expire("12312",1000L);
        // false
        System.out.println(cache.doIdempotent("12312", 1000L));
    }

    @Test
    public void delKey() {
        LocalCacheIdempotentTemplate cache = new LocalCacheIdempotentTemplate();
        // true
        System.out.println(cache.doIdempotent("12312", 1000L));
        cache.delKey("12312");
        // true
        System.out.println(cache.doIdempotent("12312", 1000L));
    }
}