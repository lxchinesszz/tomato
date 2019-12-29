package com.github.tomato.core;

import com.github.tomato.constant.TomatoConstant;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;


/**
 * @author liuxin
 * 2019-12-29 22:34
 */
public class RedisIdempotentTemplate extends AbstractIdempotent {

    private StringRedisTemplate redisTemplate;

    public RedisIdempotentTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean doIdempotent(String uniqueCode, Long millisecond) {
        return redisTemplate.opsForHash().putIfAbsent(uniqueCode, uniqueCode, TomatoConstant.DEFAULT_VALUE);
    }

    @Override
    public void expire(String uniqueCode, Long millisecond) {
        redisTemplate.expire(uniqueCode,millisecond, TimeUnit.MILLISECONDS);
    }
}
