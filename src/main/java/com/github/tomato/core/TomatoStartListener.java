package com.github.tomato.core;

import com.github.tomato.util.TomatoBanner;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * 组件启动监听器,当监听到容器启动时间后会输出组件的banner
 *
 * @author liuxin
 * 2020-01-04 23:14
 */
public class TomatoStartListener implements ApplicationListener<ApplicationEvent> {


    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationStartedEvent) {
            TomatoBanner.print();
        }
    }
}
