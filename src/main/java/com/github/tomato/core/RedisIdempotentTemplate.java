package com.github.tomato.core;

import com.github.tomato.constant.TomatoConstant;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;


/**
 * 基于redis实现幂等拦截的，这个类封装了redis的操作
 *
 * @author liuxin
 * 2019-12-29 22:34
 */
public class RedisIdempotentTemplate extends AbstractIdempotent {

    /**
     * redis的实现类
     */
    private StringRedisTemplate redisTemplate;

    public RedisIdempotentTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisIdempotentTemplate(StringRedisTemplate redisTemplate, String prefix) {
        this.redisTemplate = redisTemplate;
        setPrefix(prefix);
    }


    /**
     * 新增key并设置过期时间
     *
     * @param uniqueCode  幂等键
     * @param millisecond 毫秒
     * @return boolean
     */
    @Override
    public boolean doIdempotent(String uniqueCode, Long millisecond) {
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(uniqueCode, TomatoConstant.DEFAULT_VALUE, millisecond, TimeUnit.MILLISECONDS);
        return setIfAbsent != null ? setIfAbsent : false;
    }

    /**
     * 给一个key续期
     *
     * @param uniqueCode  幂等键
     * @param millisecond 毫秒
     */
    @Override
    public void expire(String uniqueCode, Long millisecond) {
        redisTemplate.expire(uniqueCode, millisecond, TimeUnit.MILLISECONDS);
    }

    /**
     * 删除一个key
     *
     * @param uniqueToken 加密后的唯一键
     * @return boolean
     */
    @Override
    public boolean delKey(String uniqueToken) {
        Boolean delete = redisTemplate.opsForValue().getOperations().delete(uniqueToken);
        return delete != null ? delete : false;
    }
}
