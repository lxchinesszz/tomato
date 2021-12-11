package com.github.tomato.core.cache;

import lombok.extern.slf4j.Slf4j;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author liuxin
 * 2021/12/10 9:03 下午
 */
@Slf4j
public class LocalCache {

    private static final String EMPTY = "-";


    private static final int MAX_CAPACITY = 100000;

    private static final int DEFAULT_CAPACITY = 1024;

    /**
     * 缓存集合
     */
    private static SoftReference<Map<String, CacheEntity>> CACHE_SOFT_REF = new SoftReference<>(new ConcurrentHashMap<>(DEFAULT_CAPACITY));

    private static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(5);

    static {
        EXECUTOR.scheduleWithFixedDelay(new TaskThread(), 0, 20, TimeUnit.MILLISECONDS);
    }

    /**
     * 注意当GC发生,且内存不够的时候,会自动释放重新创建缓存
     *
     * @return Map
     */
    public static Map<String, CacheEntity> getCache() {
        Map<String, CacheEntity> stringCacheEntityMap = CACHE_SOFT_REF.get();
        if (Objects.isNull(stringCacheEntityMap)) {
            CACHE_SOFT_REF = new SoftReference<>(new ConcurrentHashMap<>(DEFAULT_CAPACITY));
        }
        return CACHE_SOFT_REF.get();
    }

    /**
     * 带过期时间的key-value
     *
     * @param key       幂等键
     * @param cacheTime 过期时间
     * @return boolean
     */
    public boolean set(String key, long cacheTime) {
        if (key == null) {
            return false;
        }
        if (getCache().size() > MAX_CAPACITY) {
            log.error("[map space is full]");
            return false;
        }
        if (getCache().containsKey(key)) {
            return false;
        }
        CacheEntity cacheEntity = new CacheEntity(key, EMPTY, System.currentTimeMillis(), cacheTime, true);
        getCache().put(key, cacheEntity);
        return true;
    }

    public boolean addExpire(String key, long cacheTime) {
        if (key == null) {
            return false;
        }
        if (getCache().size() > MAX_CAPACITY) {
            log.error("[map space is full]");
            return false;
        }
        CacheEntity cacheEntity = new CacheEntity(key, EMPTY, System.currentTimeMillis(), cacheTime, true);
        getCache().put(key, cacheEntity);
        return true;
    }


    public Object remove(String key) {
        if (!getCache().containsKey(key)) {
            return true;
        }
        CacheEntity cacheEntity = getCache().remove(key);
        if (cacheEntity != null) {
            return cacheEntity.getValue();
        }
        return null;
    }

    public boolean containsKey(String key) {
        return getCache().containsKey(key);
    }

    private static class TaskThread implements Runnable {
        @Override
        public void run() {
            try {
                CacheEntity cacheEntity;
                Iterator<Map.Entry<String, CacheEntity>> iterator = getCache().entrySet().iterator();
                String key;
                while (iterator.hasNext()) {
                    Map.Entry<String, CacheEntity> entry = iterator.next();
                    key = entry.getKey();
                    cacheEntity = entry.getValue();
                    long currentTime = System.currentTimeMillis();
                    if (cacheEntity.getExpire() != null
                            && cacheEntity.getExpire()
                            && cacheEntity.getCreateTime() + cacheEntity.getCacheTime() <= currentTime) {
                        log.debug("[key过期][key:{}]", key);
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
                log.warn("定时任务异常:", e);
            }
        }
    }

    static class CacheEntity {

        /**
         * 缓存key
         */
        private final String key;

        private final Object value;

        //创建时间-当前秒
        private final long createTime;
        /*
         * 缓存时间-毫秒
         */
        private final long cacheTime;

        //是否需要过期
        private Boolean isExpire = false;

        public CacheEntity(String key, Object value, long createTime, long cacheTime, Boolean isExpire) {
            this.key = key;
            this.value = value;
            this.createTime = createTime;
            this.cacheTime = cacheTime;
            this.isExpire = isExpire;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public long getCreateTime() {
            return createTime;
        }

        public long getCacheTime() {
            return cacheTime;
        }

        public Boolean getExpire() {
            return isExpire;
        }
    }

}
