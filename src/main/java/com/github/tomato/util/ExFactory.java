package com.github.tomato.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import java.util.List;
import java.util.Objects;

/**
 * 采购业务异常工厂类 <br>
 * 1. 中间件提供的不方便定制 <br>
 * 2. 只采用中间件定义的模型 ExDefinition基类,BusinessException业务异常
 *
 * @author liuxin 2020/12/8 2:07 下午
 */
public final class ExFactory {

    /**
     * 根据异常基类构建业务异常
     *
     * @param descFormat 异常描述
     * @return BusinessException
     */
    public static BusinessException throwBusiness(String descFormat, Object... args) {
        String message = MessageFormatter.arrayFormat(descFormat, args).getMessage();
        return new BusinessException(CommonErrorEnum.FORMAT_TIP.formatExDefinition(message));
    }

    /**
     * 根据异常基类构建业务异常
     *
     * @param exDefinitionTip 异常描述
     * @return BusinessException
     */
    public static BusinessException throwBusiness(String exDefinitionTip) {
        ;
        return new BusinessException(CommonErrorEnum.FORMAT_TIP.formatExDefinition(exDefinitionTip));
    }

    /**
     * 根据异常基类构建业务异常
     *
     * @param exDefinition 异常描述基类
     * @return BusinessException
     */
    public static BusinessException throwBusiness(ExDefinition exDefinition) {
        return new BusinessException(exDefinition);
    }

    public static BusinessException tipBusiness(String formatMsg, Object... args) {
        String msg = MessageFormatterUtils.arrayFormat(formatMsg, args);
        return ExFactory.throwBusiness(msg);
    }

    public static BusinessException tipBusiness(CommonErrorEnum commonErrorEnum, Object... msg) {
        return ExFactory.throwBusiness(commonErrorEnum.formatExDefinition(msg));
    }

    /**
     * 构建采购业务异常
     *
     * @param commonErrorEnum 采购业务异常
     * @return BusinessException
     */
    public static BusinessException throwBusiness(CommonErrorEnum commonErrorEnum) {
        return new BusinessException(commonErrorEnum.toExDefinition());
    }

    /**
     * 构建采购业务异常 <br>
     * 语法:<br>
     * "A:{}",1231 = A:1231
     *
     * @param commonErrorEnum 采购业务异常
     * @return BusinessException
     */
    public static BusinessException throwBusiness(CommonErrorEnum commonErrorEnum, Object... msg) {
        return new BusinessException(commonErrorEnum.formatExDefinition(msg));
    }
}
