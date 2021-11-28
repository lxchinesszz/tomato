package com.github.tomato.configuration;

import com.github.tomato.core.*;
import com.github.tomato.support.DefaultRepeatToInterceptSupport;
import com.github.tomato.support.DefaultTokenProviderSupport;
import com.github.tomato.support.RepeatToInterceptSupport;
import com.github.tomato.support.TokenProviderSupport;
import lombok.extern.slf4j.Slf4j;
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
public class TomatoAutoConfiguration {

    @Bean
    public TomatoStartListener listener(){
        return new TomatoStartListener();
    }

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


    /**
     * 如果已经存在实现bean就不默认实现
     *
     * @return TokenProviderSupport
     */
    @Bean
    @ConditionalOnMissingBean(RepeatToInterceptSupport.class)
    public RepeatToInterceptSupport toInterceptSupport(){
        return new DefaultRepeatToInterceptSupport();
    }

    @Bean
    public TomatoV2Interceptor tomatoInterceptor(Idempotent idempotent, TokenProviderSupport tokenProviderSupport,RepeatToInterceptSupport repeatToInterceptSupport) {
        return new TomatoV2Interceptor(idempotent, tokenProviderSupport,repeatToInterceptSupport);
    }
}
