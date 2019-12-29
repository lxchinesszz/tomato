package com.github.tomato.configuration;

import com.github.tomato.core.Idempotent;
import com.github.tomato.core.RedisIdempotentTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author liuxin
 * 2019-12-29 22:02
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedisAutoConfiguration.class)
public class TomatoAutoConfiguration {


    @Bean
    public Idempotent idempotent(StringRedisTemplate redisTemplate) {
        return new RedisIdempotentTemplate(redisTemplate);
    }
}
