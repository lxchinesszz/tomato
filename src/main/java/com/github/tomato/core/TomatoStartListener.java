package com.github.tomato.core;

import com.github.tomato.util.TomatoBanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * 组件启动监听器,当监听到容器启动时间后会输出组件的banner
 *
 * @author liuxin
 * 2020-01-04 23:14
 */
@Slf4j
public class TomatoStartListener implements ApplicationListener<ApplicationEvent> {



    private Idempotent idempotent;

    public TomatoStartListener(Idempotent idempotent) {
        this.idempotent = idempotent;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationStartedEvent) {
            boolean localMapCache = "LocalCacheIdempotentTemplate".equals(idempotent.getClass().getSimpleName());
            boolean redisCache = "RedisIdempotentTemplate".equals(idempotent.getClass().getSimpleName());
            String type = localMapCache ? "LocalMapCache" : (redisCache ? "RedisCache" : "unknown");
            TomatoBanner.print(type);
        }
    }
}
