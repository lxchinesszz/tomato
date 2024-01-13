package com.github.tomato.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 命名: 模块_异常类型_ERROR() FORMAT_模块_异常类型_ERROR() 商品配合相关错误码 1000 模块:GOODS<br>
 * 品牌配置相关错误码 2000 模块:BRAND<br>
 * 在途库存相关错误码 3000 模块:INVENTORY_WAY<br>
 * 销售预估相关错误码 4000 模块:SALES_ESTIMATE<br>
 * DP相关错误码 5000 模块:DEMAND_PLAN<br>
 * 采购计划相关错误码 6000 模块:PLAN<br>
 * PR单相关错误码 7000 模块:PR<br>
 * PO单相关错误码 8000 模块:PO<br>
 * 到货通知单错误信息 9000<br>
 * 交易链路错误信息 10000<br>
 * 第三方调用异常 11000<br>
 * 操作日志 12000<br>
 * 履约历史记录 13000<br>
 * 过账监控 14000<br>
 * 第三方调用异常 11000<br>
 * 操作日志 12000<br>
 * 履约历史记录 13000<br>
 * 过账监控 14000<br>
 * 数据字典 15000<br/>
 * <p>
 * 权限相关错误码 系统错误码:
 *
 * @author liuxin 2020/12/8 1:31 下午
 */
@Slf4j
public enum CommonErrorEnum {

    // 0 ~ 999 非业务参数信息相关异常

    FORMAT_CLIENT_ERROR(ExType.SYSTEM_ERROR, 800, "{},接口异常"),

    PARAM_ERROR(ExType.BUSINESS_ERROR, 300, "参数错误"),

    /**
     * 导入异常
     */
    FORMAT_IMPORT_ERROR(ExType.BUSINESS_ERROR, 11008, "{}"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(ExType.SYSTEM_ERROR, 900, "系统异常,请联系管理员"),

    /**
     * 通用的异常
     */
    FORMAT_COMMON_ERROR(ExType.BUSINESS_ERROR, 11007, "异常:{}"),

    /**
     * 通用的异常
     */
    FORMAT_PARAM_VALIDATOR_ERROR(ExType.BUSINESS_ERROR, 11006, "参数校验未通过:{}"),

    /**
     * 通用的异常
     */
    ERROR_REQUEST_PARAM_ERROR(ExType.BUSINESS_ERROR, 11005, "请检查请求参数,输入必填值"),


    USER_TOKEN_EXPIRED(ExType.SYSTEM_ERROR, 50014, "Token expired"),

    FORWARD_PAGE(ExType.BUSINESS_ERROR, 2001, "页面跳转提示:{}"),

    /**
     * 温馨提示
     */
    FORMAT_TIP(ExType.BUSINESS_ERROR, 18001, "{}");;

    /**
     * 系统异常:10 系统繁忙:11 业务异常: 20
     */
    private final ExType type;

    /**
     * 异常code
     */
    private final int code;

    /**
     * 异常描述
     */
    public final String msg;

    /**
     * 指定异常类型
     *
     * @param type 异常类型
     * @param code 异常编码
     * @param msg  异常描述
     */
    CommonErrorEnum(ExType type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }

    /**
     * 异常类型
     *
     * @return ExType
     */
    public ExType getType() {
        return type;
    }

    /**
     * 错误码
     *
     * @return int
     */
    public int getCode() {
        return Integer.parseInt(type.getCode() + "" + code);
    }

    /**
     * 错误描述
     *
     * @return String
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 异常类型
     *
     * @return ExType
     */
    public ExType getExType() {
        return type;
    }

    /**
     * 转换成异常基类
     *
     * @return ExDefinition
     */
    public ExDefinition toExDefinition() {
        return new ExDefinition(this.getType(), this.getCode(), this.getMsg());
    }

    /**
     * 参考logback语法
     *
     * @param msg 参数数组
     * @return ExDefinition
     */
    public ExDefinition formatExDefinition(Object... msg) {
        return new ExDefinition(this.getType(), this.getCode(), this.getMsg(), msg);
    }

    public static ExDefinition toExDefinition(String format, Object... msg) {
        return new ExDefinition(ExType.BUSINESS_ERROR, -1, format, msg);
    }

}
