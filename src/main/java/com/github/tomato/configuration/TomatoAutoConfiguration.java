package com.github.tomato.configuration;

import com.github.tomato.core.*;
import com.github.tomato.support.DefaultTokenProviderSupport;
import com.github.tomato.support.TokenProviderSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
@ConditionalOnBean(StringRedisTemplate.class)
public class TomatoAutoConfiguration {


    @Bean
    public Idempotent idempotent(StringRedisTemplate redisTemplate) {
        return new RedisIdempotentTemplate(redisTemplate);
    }

    /**
     * 如果已经存在实现bean就不默认实现
     *
     * @return TokenProviderSupport
     */
    @Bean
    @ConditionalOnMissingBean(TokenProviderSupport.class)
    public TokenProviderSupport tokenProviderSupport() {
        return new DefaultTokenProviderSupport();
    }

    @Bean
    public TomatoInterceptor tomatoInterceptor(Idempotent idempotent, TokenProviderSupport tokenProviderSupport) {
        return new TomatoInterceptor(idempotent, tokenProviderSupport);
    }
}
