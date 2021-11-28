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

    public RedisIdempotentTemplate(StringRedisTemplate redisTemplate, String prefix) {
        this.redisTemplate = redisTemplate;
        setPrefix(prefix);
    }


    @Override
    public boolean doIdempotent(String uniqueCode, Long millisecond) {
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(uniqueCode, TomatoConstant.DEFAULT_VALUE, millisecond, TimeUnit.MILLISECONDS);
        return setIfAbsent != null ? setIfAbsent : false;
    }

    @Override
    public void expire(String uniqueCode, Long millisecond) {
        redisTemplate.expire(uniqueCode, millisecond, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean delKey(String uniqueToken) {
        Boolean delete = redisTemplate.opsForValue().getOperations().delete(uniqueToken);
        return delete != null ? delete : false;
    }
}
