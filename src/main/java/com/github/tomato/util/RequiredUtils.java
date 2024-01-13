package com.github.tomato.util;

import com.scm.common.exception.BusinessException;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 简化Optional操作步骤 使用场景: <br>
 * 1. 当一个方法中需要多个非空判断且非空判断的错误描述异常一样时候,可使用对象模式 <br>
 * 2. 当一个方法中需要多个非空判断,但是非空的错误描述异常不同时候,可使用静态方法<br>
 *
 * @author liuxin 2020/11/24 3:47 下午
 */
public final class RequiredUtils {

    /**
     * 实例异常
     */
    private final Supplier<ExDefinition> exceptionSupplier;

    private RequiredUtils(Supplier<ExDefinition> exceptionSupplier) {
        this.exceptionSupplier = exceptionSupplier;
    }

    /**
     * 唯一的示例方法,目的使用通用的异常
     *
     * @param supplier
     *            异常函数
     * @return Optionals
     */
    public static RequiredUtils ofException(Supplier<ExDefinition> supplier) {
        return new RequiredUtils(supplier);
    }

    /**
     * 静态方法提供设置默认值,不抛出异常
     *
     * @param value
     *            预期值
     * @param other
     *            默认值
     * @param <T>
     *            值类型
     * @return T
     */
    public static <T> T orElse(T value, T other) {
        return value != null ? value : other;
    }

    public static <T, R> R orElse(T value, Function<T, R> func, R defaultValue) {
        return Optional.ofNullable(value).map(func).orElse(defaultValue);
    }

    /**
     * 静态方法,获取预期值,如果预期值为null,就报错
     *
     * @param value
     *            预期值
     * @param exceptionSupplier
     *            指定异常
     * @param <T>
     *            预期值类型
     * @return T 预期值
     */
    public static <T> T getRequired(T value, Supplier<ExDefinition> exceptionSupplier) {
        Objects.requireNonNull(exceptionSupplier);
        if (value != null) {
            return value;
        } else {
            ExDefinition exDefinition = exceptionSupplier.get();
            throw new BusinessException(exDefinition);
        }
    }

    /**
     * 静态方法,获取预期值,如果预期值为null,就报错
     *
     * @param value
     *            预期值
     * @param commonErrorEnum
     *            指定异常
     * @param <T>
     *            预期值类型
     * @return T 预期值
     */
    public static <T> T getRequired(T value, CommonErrorEnum commonErrorEnum) {
        Objects.requireNonNull(commonErrorEnum);
        if (value != null) {
            return value;
        } else {
            throw ExFactory.throwBusiness(commonErrorEnum);
        }
    }

    /**
     * 静态方法,获取预期值,如果预期值为null,就报错
     *
     * @param value
     *            预期值
     * @param commonErrorEnum
     *            指定异常
     * @param <T>
     *            预期值类型
     * @return T 预期值
     */
    public static <T> T getRequired(T value, CommonErrorEnum commonErrorEnum, Object... errArgs) {
        Objects.requireNonNull(commonErrorEnum);
        if (value != null) {
            return value;
        } else {
            throw ExFactory.throwBusiness(commonErrorEnum, errArgs);
        }
    }

    /**
     * 静态方法,获取预期值,如果预期值为null,就报错
     *
     * @param value
     *            预期值
     * @param <T>
     *            预期值类型
     * @return T 预期值
     */
    public static <T> T getRequired(T value) {
        if (value != null) {
            return value;
        } else {
            throw new BusinessException(CommonErrorEnum.PARAM_ERROR.toExDefinition());
        }
    }

    /**
     * 实例方法,获取预期值,如果预期值为null,就报错
     *
     * @param value
     *            预期值
     * @param <T>
     *            预期值类型
     * @return T 预期值
     */
    public <T> T getRequiredThrows(T value) {
        Objects.requireNonNull(exceptionSupplier);
        if (value != null) {
            return value;
        } else {
            throw ExFactory.throwBusiness(exceptionSupplier.get());
        }
    }

}
