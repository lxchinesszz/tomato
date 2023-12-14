package com.github.tomato.configuration;

import com.github.tomato.core.*;
import com.github.tomato.support.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * 基于Spring.factories实现自动配置
 *
 * @author liuxin
 * 2019-12-29 22:02
 */
@Slf4j
@Configuration
public class TomatoAutoConfiguration {


    /**
     * 如果项目中是具备了Redis的能力,会自动启动分布式的幂等能力
     *
     * @param redisTemplate redis模板类
     * @return Idempotent
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.redis", name = "host")
    public Idempotent redisIdempotent(StringRedisTemplate redisTemplate) {
        return new RedisIdempotentTemplate(redisTemplate);
    }

    /**
     * 如果项目中不具备Redis的能力,会注册一个基于本地缓存的拦截能力。
     * 注意这个能力是不具备分布式的能力。切当请求量很大的情况下会导致内存占用过大。
     * 当内存占用过大会自动的释放内存,最终导致拦截失效
     *
     * @return Idempotent
     */
    @Bean
    @ConditionalOnProperty(name = "spring.redis.host", matchIfMissing = true)
    @ConditionalOnMissingBean(Idempotent.class)
    public Idempotent localIdempotent() {
        return new LocalCacheIdempotentTemplate();
    }

    /**
     * 如果已经存在实现bean就不默认实现
     * 当缺少web环境就注册默认的,默认的实现不处理http head
     *
     * @return TokenProviderSupport
     */
    @Bean
    @ConditionalOnMissingBean(TokenProviderSupport.class)
    @ConditionalOnMissingClass("javax.servlet.http.HttpServletRequest")
    public TokenProviderSupport tokenProviderSupport() {
        return new DefaultTokenProviderSupport();
    }

    /**
     * 如果已经存在实现bean就不默认实现
     * 如果是web项目,则处理http head
     *
     * @return TokenProviderSupport
     */
    @Bean
    @ConditionalOnMissingBean(TokenProviderSupport.class)
    @ConditionalOnClass(HttpServletRequest.class)
    public TokenProviderSupport webTokenProviderSupport() {
        return new WebTokenProviderSupport();
    }

    /**
     * 如果已经存在实现bean就不默认实现
     *
     * @return TokenProviderSupport
     */
    @Bean
    @ConditionalOnMissingBean(RepeatToInterceptSupport.class)
    public RepeatToInterceptSupport toInterceptSupport() {
        return new DefaultRepeatToInterceptSupport();
    }

    /**
     * 注册拦截器
     * 当存在web环境则生效
     *
     * @param idempotent               使用自动配置的拦截器
     * @param tokenProviderSupport     token解析扩展类
     * @param repeatToInterceptSupport 拦截处理器
     * @return TomatoV2Interceptor
     */
    @Bean
    @ConditionalOnBean({Idempotent.class})
    @ConditionalOnClass(HttpServletRequest.class)
    public TomatoV2Interceptor tomatoInterceptor(Idempotent idempotent, TokenProviderSupport tokenProviderSupport, RepeatToInterceptSupport repeatToInterceptSupport) {
        return new TomatoV2Interceptor(idempotent, tokenProviderSupport, repeatToInterceptSupport);
    }

    @Bean
    @ConditionalOnBean({Idempotent.class})
    public TomatoStartListener listener(Idempotent idempotent) {
        return new TomatoStartListener(idempotent);
    }
}
