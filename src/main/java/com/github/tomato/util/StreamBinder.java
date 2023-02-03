package com.github.tomato.util;


import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author liuxin 数据分组或绑定操作 使用场景: 1. 分组 2. 分组并对数据结构调整 3. 数据拆分绑定(注意key相同情况会覆盖)
 * @author liuxin 2020/11/1 10:57 上午
 */
public class StreamBinder {

    /**
     * one to one 拆解,将数据拆解成一一对应的关系
     *
     * @param dataSourceStream
     *            数据流
     * @param keyApply
     *            key生成器
     * @param <K>
     *            key
     * @param <T>
     *            数据源
     * @return Map
     */
    public static <K, T> Map<K, T> dismantling(Stream<T> dataSourceStream, Function<? super T, ? extends K> keyApply) {
        return dismantling(dataSourceStream, keyApply, Function.identity());
    }

    /**
     * one to one 拆解,将数据拆解成一一对应的关系
     *
     * @param dataSourceStream
     *            数据流
     * @param keyApply
     *            key生成器
     * @param exceptionSupplier
     *            异常生成
     * @param <K>
     *            key
     * @param <T>
     *            数据源
     * @param <X>
     *            异常泛型
     * @return Map
     * @throws X
     *             Throwable
     */
    public static <K, T, X extends Throwable> Map<K, T> dismantling(Stream<T> dataSourceStream,
        Function<? super T, ? extends K> keyApply, Supplier<? extends X> exceptionSupplier) throws X {
        return dismantling(dataSourceStream, keyApply, t -> t, exceptionSupplier);
    }

    /**
     * one to one 拆解,将数据拆解成一一对应的关系,如果出现一对多，只去第一个
     *
     * @param dataSourceStream
     *            数据流
     * @param keyApply
     *            key生成器
     * @param <K>
     *            key
     * @param <V>
     *            value
     * @param <T>
     *            数据源
     * @return Map
     */
    public static <K, V, T> Map<K, T> dismantlingFirst(Stream<T> dataSourceStream,
        Function<? super T, ? extends K> keyApply) {
        Map<? extends K, List<T>> group = group(dataSourceStream, keyApply);
        Map<K, T> result = new HashMap<>(group.size());
        for (Map.Entry<? extends K, List<T>> entry : group.entrySet()) {
            K key = entry.getKey();
            List<T> oldValues = entry.getValue();
            if (oldValues == null || oldValues.isEmpty()) {
                continue;
            }
            T newValue = oldValues.get(0);
            result.put(key, newValue);
        }
        return result;
    }

    /**
     * one to one 拆解,将数据拆解成一一对应的关系,如果出现一对多，只去第一个
     *
     * @param dataSourceStream
     *            数据流
     * @param keyApply
     *            key生成器
     * @param valueApply
     *            value生成器
     * @param <K>
     *            key
     * @param <V>
     *            value
     * @param <T>
     *            数据源
     * @return Map
     */
    public static <K, V, T> Map<K, V> dismantlingFirst(Stream<T> dataSourceStream,
        Function<? super T, ? extends K> keyApply, Function<? super T, ? extends V> valueApply) {
        Map<? extends K, List<T>> group = group(dataSourceStream, keyApply);
        Map<K, V> result = new HashMap<>(group.size());
        for (Map.Entry<? extends K, List<T>> entry : group.entrySet()) {
            K key = entry.getKey();
            List<T> oldValues = entry.getValue();
            if (oldValues == null || oldValues.isEmpty()) {
                continue;
            }
            V newValue = valueApply.apply(oldValues.get(0));
            result.put(key, newValue);
        }
        return result;
    }

    public static <K1, K2, T> Map<K2, T> dismantlingSuperKeyFirst(List<T> dataSourceStream,
        Function<? super T, ? extends K1> key1Fun, Function<? super K1, ? extends K2> key2Func) {
        Stream<T> stream;
        if (!CollectionUtils.isEmpty(dataSourceStream)) {
            stream = dataSourceStream.stream();
        } else {
            stream = Stream.empty();
        }
        Map<? extends K1, List<T>> group = group(stream, key1Fun);
        Map<K2, T> result = new HashMap<>(group.size());
        for (Map.Entry<? extends K1, List<T>> entry : group.entrySet()) {
            K1 key = entry.getKey();
            K2 key2 = key2Func.apply(key);
            List<T> oldValues = entry.getValue();
            if (oldValues == null || oldValues.isEmpty()) {
                continue;
            }
            result.put(key2, oldValues.get(0));
        }
        return result;
    }

    /**
     * one to one 拆解,将数据拆解成一一对应的关系
     *
     * @param dataSourceStream
     *            数据流
     * @param keyApply
     *            key生成器
     * @param valueApply
     *            value生成器
     * @param <K>
     *            key
     * @param <V>
     *            value
     * @param <T>
     *            数据源
     * @return Map
     */
    public static <K, V, T> Map<K, V> dismantling(Stream<T> dataSourceStream, Function<? super T, ? extends K> keyApply,
        Function<? super T, ? extends V> valueApply) {
        Map<? extends K, List<T>> group = group(dataSourceStream, keyApply);
        Map<K, V> result = new HashMap<>(group.size());
        for (Map.Entry<? extends K, List<T>> entry : group.entrySet()) {
            K key = entry.getKey();
            List<T> oldValues = entry.getValue();
            if (oldValues == null || oldValues.isEmpty()) {
                continue;
            }
            if (oldValues.size() > 1) {
                throw new DuplicateFormatFlagsException(String.format("数据重复,请检查绑定key=%s,value=[%s]", key, oldValues));
            }
            V newValue = valueApply.apply(oldValues.get(0));
            result.put(key, newValue);
        }
        return result;
    }

    /**
     * one to one 拆解,将数据拆解成一一对应的关系
     *
     * @param dataSourceStream
     *            数据流
     * @param keyApply
     *            key生成器
     * @param valueApply
     *            value生成器
     * @param exceptionSupplier
     *            异常生成
     * @param <K>
     *            key
     * @param <V>
     *            value
     * @param <T>
     *            数据源
     * @param <X>
     *            异常泛型
     * @return Map
     * @throws X
     *             Throwable
     */
    public static <K, V, T, X extends Throwable> Map<K, V> dismantling(Stream<T> dataSourceStream,
        Function<? super T, ? extends K> keyApply, Function<? super T, ? extends V> valueApply,
        Supplier<? extends X> exceptionSupplier) throws X {
        Map<? extends K, List<T>> group = group(dataSourceStream, keyApply);
        Map<K, V> result = new HashMap<>(group.size());
        for (Map.Entry<? extends K, List<T>> entry : group.entrySet()) {
            K key = entry.getKey();
            List<T> oldValues = entry.getValue();
            if (oldValues == null || oldValues.isEmpty()) {
                continue;
            }
            if (oldValues.size() > 1) {
                throw exceptionSupplier.get();
            }
            V newValue = valueApply.apply(oldValues.get(0));
            result.put(key, newValue);
        }
        return result;
    }

    /**
     * one to many 分组并对分组后的数据进行结构处理,注意出现相同的KEY值,会覆盖
     *
     * @param dataSourceStream
     *            数据流
     * @param keyApply
     *            key生成器
     * @param valueApply
     *            value生成器
     * @param <K>
     *            key
     * @param <V>
     *            value
     * @param <T>
     *            数据源
     * @return Map
     */
    public static <K, V, T> Map<K, List<V>> group(Stream<T> dataSourceStream, Function<? super T, ? extends K> keyApply,
        Function<? super T, ? extends V> valueApply) {
        Map<K, List<T>> beforeGroup = group(dataSourceStream, keyApply);
        Map<K, List<V>> afterGroup = new HashMap<>(beforeGroup.size());
        for (Map.Entry<K, List<T>> kListEntry : beforeGroup.entrySet()) {
            K groupKey = kListEntry.getKey();
            List<T> groupValues = kListEntry.getValue();
            List<V> collect = groupValues.stream().map(valueApply).collect(Collectors.toList());
            afterGroup.put(groupKey, collect);
        }
        return afterGroup;
    }

    /**
     * one to many 分组根据相同条件分组
     *
     * @param dataSourceStream
     *            数据流
     * @param keyApply
     *            key转换器
     * @param <K>
     *            key
     * @param <T>
     *            value
     * @return Map
     */
    public static <K, T> Map<K, List<T>> group(Stream<T> dataSourceStream, Function<? super T, ? extends K> keyApply) {
        List<T> dataSources = dataSourceStream.collect(Collectors.toList());
        Map<K, List<T>> result = new HashMap<>(dataSources.size());
        for (T dataSource : dataSources) {
            K key = keyApply.apply(dataSource);
            List<T> ts = result.get(key);
            if (ts == null || ts.isEmpty()) {
                ArrayList<T> values = new ArrayList<>();
                values.add(dataSource);
                result.put(key, values);
            } else {
                ts.add(dataSource);
            }
        }
        return result;
    }

}
