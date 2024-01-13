package com.github.tomato.util;

import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 1.update 2024-01-13
 * @author liuxin 2020/11/1 10:55 上午
 */
public class StreamFilter {



    /**
     * 根据条件生成List集合
     *
     * @param dataStream
     *            数据流
     * @param predicate
     *            过滤条件
     * @param <T>
     *            数据泛型
     * @return List
     */
    public static <T> List<T> filter(Stream<T> dataStream, Predicate<? super T> predicate) {
        return dataStream.filter(predicate).collect(Collectors.toList());
    }

    /**
     * 根据条件过滤并转换生成新的list
     *
     * @param dataStream
     *            数据流
     * @param predicate
     *            过滤条件
     * @param applyMapping
     *            数据类型转换函数
     * @param <T>
     *            原始数据类型
     * @param <V>
     *            新数据类型
     * @return List
     */
    public static <T, V> List<V> filter(Stream<T> dataStream, Predicate<? super T> predicate,
        Function<? super T, ? extends V> applyMapping) {
        return dataStream.filter(predicate).map(applyMapping).collect(Collectors.toList());
    }

    /**
     * 查询唯一数据
     *
     * @param dataStream
     *            数据流
     * @param predicate
     *            过滤条件
     * @param <T>
     *            数据泛型
     * @return List
     */
    public static <T> Optional<T> filterSingle(Stream<T> dataStream, Predicate<? super T> predicate) {
        List<T> filter = filter(dataStream, predicate);
        if (filter == null || filter.isEmpty()) {
            return Optional.empty();
        }
        if (filter.size() > 1) {
            throw new DuplicateFormatFlagsException(filter.toString());
        }
        return Optional.ofNullable(filter.get(0));
    }

    /**
     * 查询唯一数据并制定异常
     *
     * @param dataStream
     *            数据流
     * @param predicate
     *            过滤条件
     * @param <T>
     *            数据泛型
     * @param exceptionSupplier
     *            异常生成
     * @param <X>
     *            异常泛型
     * @return List
     * @throws X
     *             Throwable
     */
    public static <T, X extends Throwable> T filterSingle(Stream<T> dataStream, Predicate<? super T> predicate,
        Supplier<? extends X> exceptionSupplier) throws X {
        List<T> filter = filter(dataStream, predicate);
        if (filter == null || filter.isEmpty()) {
            throw exceptionSupplier.get();
        }
        if (filter.size() > 1) {
            throw exceptionSupplier.get();
        }
        return filter.get(0);
    }
}
