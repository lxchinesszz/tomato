package com.github.tomato.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 使用用法包含在单侧里面 旨在减少代码开发量
 * 1.update 2024-01-13
 *
 * @author liuxin
 */
public final class EnhanceStream<T> {

    public static <T> List<T> singletonList(T o) {
        return Collections.singletonList(o);
    }

    /**
     * 生成list
     *
     * @param elements 元素
     * @param <T>      泛型
     * @return List
     */
    @SafeVarargs
    public static <T> List<T> toList(T... elements) {
        List<T> list = new ArrayList<>();
        if (Objects.nonNull(elements)) {
            for (T element : elements) {
                if (Objects.nonNull(element)) {
                    list.add(element);
                }
            }
        }
        return list;
    }

    public static <T> void addAll(Collection<T> collection, Collection<T> add) {
        if (isNotEmpty(add)) {
            collection.addAll(add);
        }
    }

    public static <T> void add(Collection<T> collection, T add) {
        if (Objects.nonNull(add)) {
            collection.add(add);
        }
    }

    /**
     * 两个Map进行合并
     *
     * @param map 原始map
     * @param add 要合并的map
     * @param <K> key泛型
     * @param <V> 值泛型
     */
    public static <K, V> void addAll(Map<K, V> map, Map<K, V> add) {
        if (isNotEmpty(add) && Objects.nonNull(map)) {
            for (Map.Entry<K, V> kvEntry : add.entrySet()) {
                map.put(kvEntry.getKey(), kvEntry.getValue());
            }
        }
    }

    public static <T> List<T> setToList(Set<T> set) {
        if (EnhanceStream.isNotEmpty(set)) {
            return new ArrayList<>(set);
        } else {
            return EnhanceStream.emptyList();
        }
    }

    public static boolean isEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isEmptyAndBlank(final Collection<String> coll) {
        return coll == null || coll.isEmpty() || coll.stream().allMatch(StringUtils::isBlank);
    }

    /**
     * Null-safe check if the specified collection is not empty.
     * <p>
     * Null returns false.
     *
     * @param coll the collection to check, may be null
     * @return true if non-null and non-empty
     * @since 3.2
     */
    public static boolean isNotEmpty(final Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(final Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 返回空集合
     *
     * @param <T> 泛型
     * @return List
     */
    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    public static final <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }

    /**
     * 返回空集合
     *
     * @param <T> 泛型
     * @return List
     */
    public static <T> Set<T> emptySet() {
        return Collections.emptySet();
    }

    /**
     * 去重
     *
     * @param dataSources 数据
     * @param <T>         泛型
     * @return List 列表
     */
    public static <T> List<T> distinct(List<T> dataSources) {
        return newStream(dataSources).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    /**
     * 去重
     *
     * @param dataSources 数据
     * @param <T>         泛型
     * @param <R>         泛型
     * @param mapper      函数
     * @return List 列表
     */
    public static <T, R> List<R> distinct(List<T> dataSources, Function<T, R> mapper) {
        List<R> rs = mapToList(dataSources, mapper);
        return newStream(rs).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    /**
     * 去重
     *
     * @param dataSources 数据
     * @param <T>         泛型
     * @param <T1>泛型
     * @param mapper1     函数
     * @param mapper2     函数
     * @param <R>         泛型
     * @return List 列表
     */
    public static <T, T1, R> List<R> distinct(List<T> dataSources, Function<T, T1> mapper1, Function<T1, R> mapper2) {
        List<T1> rs = mapToList(dataSources, mapper1);
        return newStream(rs).filter(Objects::nonNull).map(mapper2).distinct().collect(Collectors.toList());
    }

    /**
     * 去重
     *
     * @param dataSources 数据
     * @param <R>         泛型
     * @param mapper      函数
     * @param <T>         泛型
     * @return List 集合
     */
    public static <T, R> List<T> distinctByKey(List<T> dataSources, Function<T, R> mapper) {
        Map<R, T> dismantling = dismantlingFirst(dataSources, mapper);
        return getValues(dismantling);
    }

    /**
     * 从流中获取Inter类型数据,非空处理后,求和
     *
     * @param dataStream 原始数据流
     * @param mapper     指定数字类型字段
     * @param <T>        泛型
     * @return Integer
     */
    public static <T> Integer mapToNonNullIntSum(Stream<T> dataStream, Function<T, Integer> mapper) {
        List<Integer> integers = mapToList(dataStream, mapper);
        int total = 0;
        for (Integer integer : integers) {
            if (Objects.nonNull(integer)) {
                total += integer;
            }
        }
        return total;
    }

    /**
     * 求和
     *
     * @param dataSources 数据源
     * @param mapper      函数
     * @param <T>         泛型
     * @return BigDecimal
     */
    public static <T> BigDecimal mapToNonNullBigSum(List<T> dataSources, Function<T, BigDecimal> mapper) {
        List<BigDecimal> integers = mapToList(newStream(dataSources), mapper);
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal bigDecimal : integers) {
            if (Objects.nonNull(bigDecimal)) {
                total = total.add(bigDecimal);
            }
        }
        return total;
    }

    /**
     * 从流中获取Inter类型数据,非空处理后,求和（如果元素为空，则返回空）
     *
     * @param dataStream 原始数据流
     * @param mapper     指定数字类型字段
     * @param <T>        泛型
     * @return Integer
     */
    public static <T> Integer mapToNullAbleIntSum(Stream<T> dataStream, Function<T, Integer> mapper) {
        List<Integer> integers = mapToList(dataStream, mapper);
        int total = 0;
        boolean haveNonNull = false;
        for (Integer integer : integers) {
            if (Objects.nonNull(integer)) {
                haveNonNull = true;
                total += integer;
            }
        }
        return haveNonNull ? total : null;
    }

    /**
     * 从流中获取Inter类型数据,非空处理后,求和
     *
     * @param dataStream 原始数据流
     * @param mapper     指定数字类型字段
     * @param <T>        泛型
     * @return Integer
     */
    public static <T> Long mapToNonNullLongSum(Stream<T> dataStream, Function<T, Long> mapper) {
        List<Long> longs = mapToList(dataStream, mapper);
        long total = 0;
        for (Long nextLong : longs) {
            if (Objects.nonNull(nextLong)) {
                total += nextLong;
            }
        }
        return total;
    }

    /**
     * 从流中获取Inter类型数据,非空处理后,求和
     *
     * @param dataSources 原始数据源
     * @param mapper      指定数字类型字段
     * @param <T>         泛型
     * @return Integer
     */
    public static <T> Integer mapToNonNullIntSum(List<T> dataSources, Function<T, Integer> mapper) {
        return mapToNonNullIntSum(newStream(dataSources), mapper);
    }

    /**
     * 从流中获取Inter类型数据,非空处理后,求和（如果元素为空，则返回空）
     *
     * @param dataSources 原始数据源
     * @param mapper      指定数字类型字段
     * @param <T>         泛型
     * @return Integer
     */
    public static <T> Integer mapToNullAbleIntSum(List<T> dataSources, Function<T, Integer> mapper) {
        return mapToNullAbleIntSum(newStream(dataSources), mapper);
    }

    /**
     * 从流中获取Inter类型数据,非空处理后,求和
     *
     * @param dataSources 原始数据源
     * @param mapper      指定数字类型字段
     * @param <T>         泛型
     * @return Integer
     */
    public static <T> Long mapToNonNullLongSum(List<T> dataSources, Function<T, Long> mapper) {
        return mapToNonNullLongSum(newStream(dataSources), mapper);
    }

    /**
     * 从map中获取所有的值的list
     *
     * @param map Map数据
     * @param <K> key泛型
     * @param <V> value泛型
     * @return List
     */
    public static <K, V> List<V> getValues(Map<K, V> map) {
        if (CollectionUtils.isEmpty(map)) {
            return Collections.emptyList();
        }
        Collection<V> values = map.values();
        return new ArrayList<>(values);
    }

    /**
     * 从map中获取所有的值的list
     *
     * @param map Map数据
     * @param <K> key泛型
     * @param <V> value泛型
     * @return List
     */
    public static <K, V> Optional<V> getValuesFirst(Map<K, V> map) {
        List<V> values = getValues(map);
        return values.stream().findFirst();
    }

    /**
     * 如果是数据分组格式key是一个，value是list的情况，只去value中的第一个。
     *
     * @param map Map数据
     * @param <K> key泛型
     * @param <V> value泛型
     * @return List
     */
    public static <K, V> List<V> eachGetValueFirst(Map<K, List<V>> map) {
        if (CollectionUtils.isEmpty(map)) {
            return Collections.emptyList();
        }
        List<V> result = new ArrayList<>(map.size());
        for (Map.Entry<K, List<V>> entry : map.entrySet()) {
            if (!CollectionUtils.isEmpty(entry.getValue())) {
                result.add(entry.getValue().get(0));
            }
        }
        return result;
    }

    /**
     * 从map中获取所有的key的list
     *
     * @param map Map数据
     * @param <K> key泛型
     * @param <V> value泛型
     * @return List
     */
    public static <K, V> List<K> getKeys(Map<K, V> map) {
        if (CollectionUtils.isEmpty(map)) {
            return Collections.emptyList();
        }
        Set<K> ks = map.keySet();
        return new ArrayList<>(ks);
    }

    /**
     * 从流中建立新的类型
     *
     * @param dataStream 数据流
     * @param mapping    转换函数
     * @param <T>        原数据类型
     * @param <R>        目标数据类型
     * @return List
     */
    public static <T, R> List<R> mapToList(Stream<T> dataStream, Function<T, R> mapping) {
        return newStream(dataStream).map(mapping).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 从流中建立新的类型
     *
     * @param dataSources 数据源
     * @param mapping     转换函数
     * @param <T>         原数据类型
     * @param <R>         目标数据类型
     * @return List
     */
    public static <T, R> List<R> mapToList(List<T> dataSources, Function<T, R> mapping) {
        return newStream(dataSources).map(mapping).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 从流中建立新的类型
     *
     * @param dataSources 数据源
     * @param mapping     转换函数
     * @param <T>         原数据类型
     * @param <R>         目标数据类型
     * @return List
     */
    public static <T, R> List<R> mapToUniqueList(List<T> dataSources, Function<T, R> mapping) {
        return newStream(dataSources).map(mapping).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    /**
     * 从流中建立新的类型
     *
     * @param dataSources 数据源
     * @param mapping     转换函数
     * @param mapping1    转换函数
     * @param <R1>        泛型
     * @param <T>         原数据类型
     * @param <R>         目标数据类型
     * @return List
     */
    public static <T, R1, R> List<R> mapToUniqueList(List<T> dataSources, Function<T, R1> mapping,
                                                     Function<R1, R> mapping1) {
        return newStream(dataSources).map(mapping).filter(Objects::nonNull).map(mapping1).filter(Objects::nonNull)
                .distinct().collect(Collectors.toList());
    }

    /**
     * 从流中建立新的类型
     *
     * @param dataSources 数据源
     * @param mapping     转换函数
     * @param mapping2    转换函数
     * @param <V>         泛型
     * @param <T>         原数据类型
     * @param <R>         目标数据类型
     * @return List
     */
    public static <T, R, V> List<V> supperMapToList(List<T> dataSources, Function<T, R> mapping,
                                                    Function<R, V> mapping2) {
        return newStream(dataSources).map(mapping).filter(Objects::nonNull).map(mapping2).filter(Objects::nonNull)
                .distinct().collect(Collectors.toList());
    }

    public static <T, R, V> List<V> supperMapMergeMerge(List<T> dataSources, Function<T, List<R>> mapping,
                                                        Function<R, V> mapping2) {
        List<R> rs = mapToMergeList(dataSources, mapping);
        return EnhanceStream.mapToList(rs, mapping2);
    }

    /**
     * 将多个list集合合并成一个 如果要保证添加顺序，请按照顺序添加
     *
     * @param <T>            泛型
     * @param dataSourceList 数据源
     * @return List
     */
    @SafeVarargs
    public static <T> List<T> mergeList(List<T>... dataSourceList) {
        if (Objects.isNull(dataSourceList)) {
            return emptyList();
        }
        List<T> result = new ArrayList<>();
        for (List<T> ts : dataSourceList) {
            if (isNotEmpty(ts)) {
                result.addAll(ts);
            }
        }
        return result;
    }

    /**
     * 从流中建立新的List类型,并将新的list
     *
     * @param dataSources 数据源
     * @param mapping     转换函数
     * @param <T>         原数据类型
     * @param <R>         目标数据类型
     * @return List
     */
    public static <T, R> List<R> mapToMergeList(List<T> dataSources, Function<T, List<R>> mapping) {
        List<List<R>> collect =
                newStream(dataSources).map(mapping).filter(Objects::nonNull).collect(Collectors.toList());
        List<R> allList = new ArrayList<>();
        for (List<R> rs : collect) {
            allList.addAll(rs);
        }
        return allList;
    }

    /**
     * 从流中建立新的类型
     *
     * @param dataSource 数据
     * @param mapping    转换函数
     * @param <T>        原数据类型
     * @param <R>        目标数据类型
     * @return List
     */
    public static <T, R> Set<R> mapToSet(List<T> dataSource, Function<T, R> mapping) {
        return newStream(dataSource).map(mapping).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * 从流中建立新的类型
     *
     * @param dataStream 数据流
     * @param mapping    转换函数
     * @param <T>        原数据类型
     * @param <R>        目标数据类型
     * @return List
     */
    public static <T, R> Set<R> mapToSet(Stream<T> dataStream, Function<T, R> mapping) {
        return newStream(dataStream).map(mapping).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * 从流中建立新的类型并去重
     *
     * @param dataSources 数据流
     * @param mapping     转换函数
     * @param <T>         原数据类型
     * @param <R>         目标数据类型
     * @return List
     */
    public static <T, R> List<R> distinctMapToList(List<T> dataSources, Function<T, R> mapping) {
        return newStream(newStream(dataSources)).map(mapping).filter(Objects::nonNull).distinct()
                .collect(Collectors.toList());
    }

    /**
     * 从流中建立新的类型并去重
     *
     * @param dataStream 数据流
     * @param mapping    转换函数
     * @param <T>        原数据类型
     * @param <R>        目标数据类型
     * @return List
     */
    public static <T, R> List<R> distinctMapToList(Stream<T> dataStream, Function<T, R> mapping) {
        return newStream(dataStream).map(mapping).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    /**
     * 对数据流进行过滤
     *
     * @param dataStream        数据流
     * @param predicate         过滤条件
     * @param exceptionSupplier 异常生成
     * @param <T>               数据泛型
     * @param <X>               异常泛型
     * @return List 过滤后的数据条件
     * @throws X Throwable
     */
    public static <T, X extends Throwable> T filterSingle(Stream<T> dataStream, Predicate<? super T> predicate,
                                                          Supplier<? extends X> exceptionSupplier) throws X {
        return StreamFilter.filterSingle(newStream(dataStream), predicate, exceptionSupplier);
    }

    /**
     * 过滤
     *
     * @param dataSource 数据源
     * @param predicate  过滤条件
     * @param <T>        数据泛型
     * @param <R>        泛型
     * @param function   函数
     * @return List
     */
    public static <T, R> List<T> superFilter(List<T> dataSource, Function<T, R> function,
                                             Predicate<? super R> predicate) {
        return superFilter(newStream(dataSource), function, predicate);
    }

    /**
     * 根据条件生成List集合
     *
     * @param <R>        泛型
     * @param function   函数
     * @param dataStream 数据流
     * @param predicate  过滤条件
     * @param <T>        数据泛型
     * @return List
     */
    public static <T, R> List<T> superFilter(Stream<T> dataStream, Function<T, R> function,
                                             Predicate<? super R> predicate) {
        List<T> result = new ArrayList<>();
        dataStream.filter(Objects::nonNull).forEach(t -> {
            R apply = function.apply(t);
            if (predicate.test(apply)) {
                result.add(t);
            }
        });
        return result;
    }

    /**
     * 对数据流进行过滤
     *
     * @param dataSource 数据源头
     * @param predicate  过滤条件
     * @param <T>        数据泛型
     * @return List 过滤后的数据条件
     */
    public static <T> List<T> filter(List<T> dataSource, Predicate<? super T> predicate) {
        return StreamFilter.filter(newStream(dataSource), predicate);
    }

    /**
     * 对数据流进行过滤
     *
     * @param dataStream 数据流
     * @param predicate  过滤条件
     * @param <T>        数据泛型
     * @return List 过滤后的数据条件
     */
    public static <T> List<T> filter(Stream<T> dataStream, Predicate<? super T> predicate) {
        return StreamFilter.filter(newStream(dataStream), predicate);
    }

    public static <T, X extends Throwable> T findFirst(Stream<T> dataStream, Supplier<? extends X> exceptionSupplier)
            throws X {
        return newStream(dataStream).findFirst().orElseThrow(exceptionSupplier);
    }

    public static <T> Optional<T> findFirst(Stream<T> dataStream) {
        return newStream(dataStream).findFirst();
    }

    public static <T> Optional<T> findFirst(List<T> dataList) {
        return newStream(dataList).findFirst();
    }

    public static <T> Optional<T> findAny(Stream<T> dataStream) {
        return newStream(dataStream).findAny();
    }

    /**
     * 从流中获取任意数据
     *
     * @param dataList 数据集合
     * @param <T>      泛型
     * @return Optional
     */
    public static <T> Optional<T> findAny(List<T> dataList) {
        return newStream(dataList).findAny();
    }

    /**
     * 从流中获取任意数据
     *
     * @param dataList 数据集合
     * @param <T>      泛型
     * @return Optional
     */
    public static <T> T findRequiredAny(List<T> dataList) {
        Optional<T> any = newStream(dataList).findAny();
        if (!any.isPresent()) {
            throw new RuntimeException("EnhanceStream.findRequiredAny:" + dataList);
        }
        return any.get();
    }

    /**
     * 对数据流进行过滤,并对过滤后的数据进行类型转换
     *
     * @param dataStream   数据流
     * @param predicate    过滤条件
     * @param applyMapping 数据转换器
     * @param <T>          数据泛型
     * @param <V>          转换后的数据类型
     * @return List 过滤后的数据条件
     */
    public static <T, V> List<V> filter(Stream<T> dataStream, Predicate<? super T> predicate,
                                        Function<? super T, ? extends V> applyMapping) {
        return StreamFilter.filter(newStream(dataStream), predicate, applyMapping);
    }

    /**
     * 对数据流进行分组
     *
     * @param dataStream 数据流
     * @param keyApply   分组项
     * @param <T>        数据泛型
     * @param <K>        分组项泛型
     * @return Map 分组后数据
     */
    public static <T, K> Map<K, List<T>> group(Stream<T> dataStream, Function<? super T, ? extends K> keyApply) {
        return StreamBinder.group(newStream(dataStream), keyApply);
    }

    /**
     * 对数据流进行分组,并对分组后的数据进行类型转换
     *
     * @param dataStream 数据流
     * @param keyApply   分组项
     * @param valueApply 数据转换器
     * @param <T>        数据泛型
     * @param <K>        分组项泛型
     * @param <V>        转换后的数据类型
     * @return Map 分组后数据
     */
    public static <T, K, V> Map<K, List<V>> group(Stream<T> dataStream, Function<? super T, ? extends K> keyApply,
                                                  Function<? super T, ? extends V> valueApply) {
        return StreamBinder.group(newStream(dataStream), keyApply, valueApply);
    }

    /**
     * 分组
     *
     * @param dataSource 商品配置
     * @param keyApply   key生成
     * @param <K>        key泛型
     * @param <T>        数据源泛型
     * @return Map
     */
    public static <K, T> Map<K, T> dismantlingFirst(List<T> dataSource, Function<? super T, ? extends K> keyApply) {
        return StreamBinder.dismantlingFirst(newStream(dataSource), keyApply);
    }

    /**
     * @param dataSource 商品配置
     * @param keyApply   key生成
     * @param <V>        泛型
     * @param valueApply 函数
     * @param <K>        key泛型
     * @param <T>        数据源泛型
     * @return Map 字典
     */
    public static <K, V, T> Map<K, V> dismantlingFirst(List<T> dataSource, Function<? super T, ? extends K> keyApply,
                                                       Function<? super T, ? extends V> valueApply) {
        return StreamBinder.dismantlingFirst(newStream(dataSource), keyApply, valueApply);
    }

    /**
     * @param dataSourceStream 商品配置
     * @param keyApply         key生成
     * @param <K>              key泛型
     * @param <T>              数据源泛型
     * @return Map
     */
    public static <K, T> Map<K, T> dismantlingFirst(Stream<T> dataSourceStream,
                                                    Function<? super T, ? extends K> keyApply) {
        return StreamBinder.dismantlingFirst(dataSourceStream, keyApply);
    }

    /**
     * 数据拆分「注意: 拆分后的数据,如果Key一样会产生异常,请谨慎使用
     *
     * @param dataStream 数据流
     * @param keyApply   Key转换器
     * @param <T>        原始数据泛型
     * @param <K>        Key泛型
     * @return Map 拆分后数据
     */
    public static <T, K> Map<K, T> dismantling(List<T> dataStream, Function<? super T, ? extends K> keyApply) {
        return StreamBinder.dismantling(newStream(dataStream), keyApply);
    }

    /**
     * 数据拆分「注意: 拆分后的数据,如果Key一样会产生异常,请谨慎使用
     *
     * @param dataStream 数据流
     * @param keyApply   Key转换器
     * @param <T>        原始数据泛型
     * @param <K>        Key泛型
     * @return Map 拆分后数据
     */
    public static <T, K> Map<K, T> dismantling(Stream<T> dataStream, Function<? super T, ? extends K> keyApply) {
        return StreamBinder.dismantling(newStream(dataStream), keyApply);
    }

    /**
     * 数据拆分「注意: 拆分后的数据,如果Key一样会产生异常,请谨慎使用
     *
     * @param dataSource        数据源
     * @param keyApply          Key转换器
     * @param <T>               原始数据泛型
     * @param <K>               Key泛型
     * @param <X>               泛型
     * @param exceptionSupplier 异常类型
     * @return Map 拆分后数据
     * @throws X 异常
     */
    public static <T, K, X extends Throwable> Map<K, T> dismantling(List<T> dataSource,
                                                                    Function<? super T, ? extends K> keyApply, Supplier<? extends X> exceptionSupplier) throws X {
        return StreamBinder.dismantling(newStream(dataSource), keyApply, exceptionSupplier);
    }

    /**
     * 数据拆分「注意: 拆分后的数据,如果Key一样会产生异常,请谨慎使用
     *
     * @param dataStream        数据流
     * @param keyApply          Key转换器
     * @param <T>               原始数据泛型
     * @param <K>               Key泛型
     * @param <X>               泛型
     * @param exceptionSupplier 异常类型
     * @return Map 拆分后数据
     * @throws X 异常类型
     */
    public static <T, K, X extends Throwable> Map<K, T> dismantling(Stream<T> dataStream,
                                                                    Function<? super T, ? extends K> keyApply, Supplier<? extends X> exceptionSupplier) throws X {
        return StreamBinder.dismantling(newStream(dataStream), keyApply, exceptionSupplier);
    }

    /**
     * 数据拆分「注意: 拆分后的数据,如果Key一样会产生异常,请谨慎使用
     *
     * @param dataStream 数据流
     * @param keyApply   Key转换器
     * @param valueApply Value转换器
     * @param <T>        原始数据泛型
     * @param <K>        Key泛型
     * @param <V>        Value泛型
     * @return Map 拆分后数据
     */
    public static <T, K, V> Map<K, V> dismantling(Stream<T> dataStream, Function<? super T, ? extends K> keyApply,
                                                  Function<? super T, ? extends V> valueApply) {
        return StreamBinder.dismantling(newStream(dataStream), keyApply, valueApply);
    }

    /**
     * 数据拆分「注意: 拆分后的数据,如果Key一样会产生异常,请谨慎使用
     *
     * @param dataStream        数据流
     * @param keyApply          Key转换器
     * @param valueApply        Value转换器
     * @param <T>               原始数据泛型
     * @param <K>               Key泛型
     * @param <V>               Value泛型
     * @param <X>               数据重复产生的异常描述
     * @param exceptionSupplier 异常类型
     * @return Map 拆分后数据
     * @throws X 异常
     */
    public static <T, K, V, X extends Throwable> Map<K, V> dismantling(Stream<T> dataStream,
                                                                       Function<? super T, ? extends K> keyApply, Function<? super T, ? extends V> valueApply,
                                                                       Supplier<? extends X> exceptionSupplier) throws X {
        return StreamBinder.dismantling(newStream(dataStream), keyApply, valueApply, exceptionSupplier);
    }

    private static <T> Stream<T> newStream(Stream<T> dataSourcesStream) {
        if (dataSourcesStream == null) {
            return Stream.empty();
        }
        return dataSourcesStream;
    }

    public static <T> Stream<T> newStream(List<T> dataSources) {
        if (dataSources == null) {
            return Stream.empty();
        }
        return dataSources.stream();
    }

    /**
     * 对数据流进行过滤,并对过滤后的数据进行类型转换
     *
     * @param dataSources  原始数据流
     * @param predicate    过滤条件
     * @param applyMapping 数据转换器
     * @param <V>          转换后的数据类型
     * @param <T>          原始数据类型
     * @return List 过滤后的数据条件
     */
    public static <T, V> List<V> filter(List<T> dataSources, Predicate<? super T> predicate,
                                        Function<? super T, ? extends V> applyMapping) {
        return StreamFilter.filter(newStream(dataSources), predicate, applyMapping);
    }

    /**
     * 数据拆分「注意: 拆分后的数据,如果Key一样会产生异常,请谨慎使用
     *
     * @param dataSources 原始数据
     * @param keyApply    Key转换器
     * @param valueApply  Value转换器
     * @param <K>         Key泛型
     * @param <V>         Value泛型
     * @param <T>         原始数据类型
     * @return Map 拆分后数据
     */
    public static <T, K, V> Map<K, V> dismantling(List<T> dataSources, Function<? super T, ? extends K> keyApply,
                                                  Function<? super T, ? extends V> valueApply) {
        return StreamBinder.dismantling(newStream(dataSources), keyApply, valueApply);
    }

    public static <T> void removeIf(List<T> dataSources, Predicate<T> predicate) {
        if (Objects.nonNull(dataSources) && !dataSources.isEmpty()) {
            dataSources.removeIf(predicate);
        }
    }

    public static <T> void removeNotIf(List<T> dataSources, Predicate<T> predicate) {
        if (Objects.nonNull(dataSources) && !dataSources.isEmpty()) {
            dataSources.removeIf(t -> !predicate.test(t));
        }
    }

    public static void main(String[] args) {
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);

        removeIf(list, (x) -> x.equals(1L));
        System.out.println(list);
        list.clear();
        list.add(1L);
        list.add(2L);
        removeNotIf(list, (x) -> x.equals(1L));
        System.out.println(list);

    }

    /**
     * 对数据流进行分组
     *
     * @param <T>         泛型
     * @param dataSources 原始数据流
     * @param keyApply    分组项
     * @param <K>         分组项泛型
     * @return Map 分组后数据
     */
    public static <K, T> Map<K, List<T>> group(List<T> dataSources, Function<? super T, ? extends K> keyApply) {
        return StreamBinder.group(newStream(dataSources), keyApply);
    }

    /**
     * 对数据流进行分组,并对分组后的数据进行类型转换
     *
     * @param dataSources 原始数据流
     * @param keyApply    分组项
     * @param valueApply  数据转换器
     * @param <T>         泛型
     * @param <K>         分组项泛型
     * @param <V>         转换后的数据类型
     * @return Map 分组后数据
     */
    public static <K, T, V> Map<K, List<V>> group(List<T> dataSources, Function<? super T, ? extends K> keyApply,
                                                  Function<? super T, ? extends V> valueApply) {
        return StreamBinder.group(newStream(dataSources), keyApply, valueApply);
    }

    /**
     * 升序
     *
     * @param dataSources     数据集合
     * @param compareFunction 排序字段
     * @param <T>             集合类型泛型类
     * @param <U>             泛型
     * @return List
     */
    public static <T, U extends Comparable<? super U>> List<T> sortedAsc(List<T> dataSources,
                                                                         Function<T, U> compareFunction) {
        return sorted(newStream(dataSources), compareFunction, SortStrategy.UP, SortNullStrategy.NULL_IGNORE);
    }

    /**
     * 升序
     *
     * @param dataStream      数据集合
     * @param compareFunction 排序字段
     * @param <U>             泛型
     * @param <T>             集合类型泛型类
     * @return List
     */
    public static <T, U extends Comparable<? super U>> List<T> sortedAsc(Stream<T> dataStream,
                                                                         Function<T, U> compareFunction) {
        return sorted(dataStream, compareFunction, SortStrategy.UP, SortNullStrategy.NULL_IGNORE);
    }

    /**
     * 升序
     *
     * @param dataSources      数据集合
     * @param compareFunction  排序字段
     * @param sortNullStrategy 排序控制策略
     * @param <U>              泛型
     * @param <T>              集合类型泛型类
     * @return List
     */
    public static <T, U extends Comparable<? super U>> List<T> sortedAsc(List<T> dataSources,
                                                                         Function<T, U> compareFunction, SortNullStrategy sortNullStrategy) {
        return sorted(newStream(dataSources), compareFunction, SortStrategy.UP, sortNullStrategy);
    }

    /**
     * 升序
     *
     * @param dataStream       数据集合
     * @param compareFunction  排序字段
     * @param <U>              泛型
     * @param sortNullStrategy 排序策略
     * @param <T>              集合类型泛型类
     * @return List
     */
    public static <T, U extends Comparable<? super U>> List<T> sortedAsc(Stream<T> dataStream,
                                                                         Function<T, U> compareFunction, SortNullStrategy sortNullStrategy) {
        return sorted(dataStream, compareFunction, SortStrategy.UP, sortNullStrategy);
    }

    /**
     * 降序
     *
     * @param dataSources     数据集合
     * @param compareFunction 排序字段
     * @param <T>             集合类型泛型类
     * @param <U>             泛型
     * @return List
     */
    public static <T, U extends Comparable<? super U>> List<T> sortedDesc(List<T> dataSources,
                                                                          Function<T, U> compareFunction) {
        return sorted(newStream(dataSources), compareFunction, SortStrategy.DOWN, SortNullStrategy.NULL_IGNORE);
    }

    /**
     * 降序
     *
     * @param dataStream      数据集合
     * @param compareFunction 排序字段
     * @param <T>             集合类型泛型类
     * @param <U>             泛型
     * @return List
     */
    public static <T, U extends Comparable<? super U>> List<T> sortedDesc(Stream<T> dataStream,
                                                                          Function<T, U> compareFunction) {
        return sorted(dataStream, compareFunction, SortStrategy.DOWN, SortNullStrategy.NULL_IGNORE);
    }

    /**
     * 降序
     *
     * @param dataSources      数据集合
     * @param compareFunction  排序字段
     * @param <T>              集合类型泛型类
     * @param <U>              泛型
     * @param sortNullStrategy 排序策略
     * @return List
     */
    public static <T, U extends Comparable<? super U>> List<T> sortedDesc(List<T> dataSources,
                                                                          Function<T, U> compareFunction, SortNullStrategy sortNullStrategy) {
        return sorted(newStream(dataSources), compareFunction, SortStrategy.DOWN, sortNullStrategy);
    }

    /**
     * 降序
     *
     * @param dataStream       数据集合
     * @param compareFunction  排序字段
     * @param <U>              泛型
     * @param sortNullStrategy 排序策略
     * @param <T>              集合类型泛型类
     * @return List
     */
    public static <T, U extends Comparable<? super U>> List<T> sortedDesc(Stream<T> dataStream,
                                                                          Function<T, U> compareFunction, SortNullStrategy sortNullStrategy) {
        return sorted(dataStream, compareFunction, SortStrategy.DOWN, sortNullStrategy);
    }

    public static <T, R> boolean isRepeat(List<T> details, Function<T, R> func) {
        Map<R, List<T>> group = group(details, func);
        for (Map.Entry<R, List<T>> entity : group.entrySet()) {
            if (entity.getValue().size() > 1) {
                return true;
            }
        }
        return false;
    }

    private enum SortStrategy {
        /**
         * 升序
         */
        UP,
        /**
         * 降序
         */
        DOWN;
    }

    public enum SortNullStrategy {

        /**
         * 如果值为空就忽略
         */
        NULL_IGNORE,

        /**
         * 值为空就放最后
         */
        NULL_LAST,

        /**
         * 值为空就放前面
         */
        NULL_FIRST;
    }

    /**
     * @param dataStream      数据集合
     * @param compareFunction 排序字段
     * @param sortStrategy    排序策略
     * @param sortStrategy    true报异常
     * @param <T>             集合类型泛型类
     * @return List<T>
     */
    private static <T, U extends Comparable<? super U>> List<T> sorted(Stream<T> dataStream,
                                                                       Function<T, U> compareFunction, SortStrategy sortStrategy, SortNullStrategy sortNullStrategy) {
        Comparator<T> comparing;
        if (Objects.isNull(sortStrategy) || SortStrategy.UP == sortStrategy) {
            if (sortNullStrategy == SortNullStrategy.NULL_LAST) {
                comparing = Comparator.comparing(compareFunction, Comparator.nullsLast(U::compareTo));
            } else if (sortNullStrategy == SortNullStrategy.NULL_FIRST) {
                comparing = Comparator.comparing(compareFunction, Comparator.nullsFirst(U::compareTo));
            } else {
                comparing = Comparator.comparing(compareFunction);
            }
        } else {
            if (sortNullStrategy == SortNullStrategy.NULL_LAST) {
                comparing = Comparator.comparing(compareFunction, Comparator.nullsLast(U::compareTo).reversed());
            } else if (sortNullStrategy == SortNullStrategy.NULL_FIRST) {
                comparing = Comparator.comparing(compareFunction, Comparator.nullsFirst(U::compareTo).reversed());
            } else {
                comparing = Comparator.comparing(compareFunction, Comparator.reverseOrder());
            }
        }
        return dataStream.sorted(comparing).collect(Collectors.toList());
    }

    public static <T> boolean contains(T[] dataSources, T... elements) {
        return contains(Stream.of(dataSources), elements);
    }

    public static <T> boolean containsAny(T[] dataSources, T... elements) {
        return containsAny(Stream.of(dataSources), elements);
    }

    public static <T> boolean contains(List<T> dataSource, T... elements) {
        return contains(newStream(dataSource), elements);
    }

    public static <T> boolean containsAny(List<T> dataSource, T... elements) {
        return containsAny(newStream(dataSource), elements);
    }

    public static <T> boolean contains(Stream<T> dataStream, T... elements) {
        List<T> ts = mapToList(newStream(dataStream), Function.identity());
        if (Objects.isNull(elements)) {
            return false;
        }
        for (T element : elements) {
            if (!ts.contains(element)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean containsAny(Stream<T> dataStream, T... elements) {
        List<T> ts = mapToList(dataStream, Function.identity());
        if (Objects.isNull(elements)) {
            return false;
        }
        for (T element : elements) {
            if (ts.contains(element)) {
                return true;
            }
        }
        return false;
    }

    @SafeVarargs
    public static <T, R> List<R> merge(Function<T, R> mapper, List<T>... lists) {
        List<R> result = new ArrayList<>();
        for (R r : distinct(lists[0], mapper)) {
            boolean temp = true;
            for (int i = 1; i < lists.length; i++) {
                List<R> rs = EnhanceStream.distinct(lists[i], mapper);
                Map<R, R> reMapping = EnhanceStream.dismantling(rs, Function.identity());
                if (Objects.isNull(reMapping.get(r))) {
                    temp = false;
                }
            }
            if (temp) {
                result.add(r);
            }
        }
        return EnhanceStream.distinct(result);
    }

    public static <T> T[] listToArr(List<T> dataList) {
        return (T[]) dataList.toArray(new Object[0]);
    }

    public static <T> List<T> arrToList(T[] dataList) {
        return Stream.of(dataList).collect(Collectors.toList());
    }

    /**
     * 对Map结构的数据value进行函数转换
     *
     * @param map  map数据
     * @param func 转换函数
     * @param <K>  泛型key
     * @param <V1> 转换前的泛型value
     * @param <V>  转换后的泛型value
     * @return 转换后的Map
     */
    public static <K, V1, V> Map<K, V> mapValue(Map<K, V1> map, Function<V1, V> func) {
        Map<K, V> result = new HashMap<>(isEmpty(map) ? 8 : map.size());
        if (isNotEmpty(map)) {
            for (Map.Entry<K, V1> entity : map.entrySet()) {
                result.put(entity.getKey(), func.apply(entity.getValue()));
            }
        }
        return result;
    }

    /**
     * 对Map结构的数据value进行函数转换
     *
     * @param map  map数据
     * @param func 转换函数
     * @param <V>  泛型value
     * @param <K1> 转换前的泛型key
     * @param <K>  转换后的泛型key
     * @return 转换后的Map
     */
    public static <K1, K, V> Map<K, V> mapKey(Map<K1, V> map, Function<K1, K> func) {
        Map<K, V> result = new HashMap<>(isEmpty(map) ? 8 : map.size());
        if (isNotEmpty(map)) {
            for (Map.Entry<K1, V> entity : map.entrySet()) {
                result.put(func.apply(entity.getKey()), entity.getValue());
            }
        }
        return result;
    }
}
