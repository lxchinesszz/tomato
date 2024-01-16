package com.github.tomato.util;

import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 年-月-日 时:分:秒.毫秒-2019-11-05 17:43:29.383<br>
 * </> 年-月-日 时:分:秒-2019-11-05 17:43:29<br>
 * 时:分:秒-17:43:29<br>
 * 年-月-日 时:分-2019-11-05 17:43<br>
 * 年-月-日-2019-11-05<br>
 * 年-月-2019-11<br>
 * 年-2019<br>
 * 月-11<br>
 * 日-05<br>
 * 时-17<br>
 * 分-43<br>
 * 秒-29<br>
 * 中文格式年月日时分秒毫秒-2019年11月05日 17时43分29秒386毫秒<br>
 * 中文格式年月日时分秒-2019年11月05日 17时43分29秒<br>
 * 中文格式年月日-2019年11月05日<br>
 * 中文格式年月-2019年11月<br>
 * 中文格式年-2019年<br>
 * 中文格式时分秒-17时43分29秒<br>
 * 无间隔符的年月日时分秒-20191105174329<br>
 * 无间隔符的年月日时分秒毫秒-20191105174329387<br>
 * 无间隔符的年月日-20191105<br>
 *
 * @author liuxin
 */
public enum DatePatternEnum {

    /**
     * yyyy-MM-dd HH:mm:ss.SSS
     */
    DATE_TIME_MS_PATTERN(0, "yyyy-MM-dd HH:mm:ss.SSS", "年-月-日 时:分:秒.毫秒"),

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    DATE_TIME_PATTERN(1, "yyyy-MM-dd HH:mm:ss", "年-月-日 时:分:秒"),

    /**
     * 时:分:秒
     */
    TIME_PATTERN(2, "HH:mm:ss", "时:分:秒"),

    /**
     * 年-月-日 时:分
     */
    MINUTE_PATTERN(3, "yyyy-MM-dd HH:mm", "年-月-日 时:分"),

    /**
     * 年-月-日
     */
    DATE_PATTERN(4, "yyyy-MM-dd", "年-月-日"),

    /**
     * 年-月
     */
    MONTH_PATTERN(5, "yyyy-MM", "年-月"),

    /**
     * 年
     */
    ONLY_YEAR_PATTERN(6, "yyyy", "年"),

    /**
     * 月
     */
    ONLY_MONTH_PATTERN(7, "MM", "月"),

    /**
     * 日
     */
    ONLY_DAY_PATTERN(8, "dd", "日"),

    /**
     * 时
     */
    ONLY_HOUR_PATTERN(9, "HH", "时"),

    /**
     * 分
     */
    ONLY_MINUTE_PATTERN(10, "mm", "分"),

    /**
     * 秒
     */
    ONLY_SECOND_PATTERN(11, "ss", "秒"),

    /**
     * 中文格式年月日时分秒毫秒
     */
    ZN_DATE_TIME_MS_PATTERN(12, "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒", "中文格式年月日时分秒毫秒"),

    /**
     * 中文格式年月日时分秒
     */
    ZN_DATE_TIME_PATTERN(13, "yyyy年MM月dd日 HH时mm分ss秒", "中文格式年月日时分秒"),

    /**
     * 中文格式年月日
     */
    ZN_DATE_PATTERN(14, "yyyy年MM月dd日", "中文格式年月日"),

    /**
     * 中文格式年月
     */
    ZN_MONTH_PATTERN(15, "yyyy年MM月", "中文格式年月"),

    /**
     * 中文格式年
     */
    ZN_YEAR_ONLY_PATTERN(16, "yyyy年", "中文格式年"),

    /**
     * 中文格式时分秒
     */
    ZN_TIME_PATTERN(17, "HH时mm分ss秒", "中文格式时分秒"),

    /**
     * 无间隔符的年月日时分秒
     */
    GAP_LESS_DATE_TIME_PATTERN(18, "yyyyMMddHHmmss", "无间隔符的年月日时分秒"),

    /**
     * 无间隔符的年月日时分秒毫秒
     */
    GAP_LESS_DATE_TIME_MS_PATTERN(19, "yyyyMMddHHmmssSSS", "无间隔符的年月日时分秒毫秒"),

    /**
     * 无间隔符的年月日
     */
    GAP_LESS_DATE_PATTERN(20, "yyyyMMdd", "无间隔符的年月日"),

    /**
     * 英文年月日
     */
    EN_DATE(21, "dd/MM/yyyy", "英文年月日");

    /**
     * 老的日期API缓存
     */
    private static final Map<DatePatternEnum, SimpleDateFormat> FORMAT_CACHE =
        new ConcurrentHashMap<>(initialCapacity());

    /**
     * 新的日期API缓存
     */
    private static final Map<DatePatternEnum, DateTimeFormatter> FORMATTER_CACHE =
        new ConcurrentHashMap<>(initialCapacity());

    static {
        checkCache();
    }

    /**
     * 索引备用
     */
    private final int index;
    /**
     * 日期格式
     */
    private final String pattern;
    /**
     * 日期描述
     */
    private final String desc;

    DatePatternEnum(int index, String pattern, String desc) {
        this.index = index;
        this.pattern = pattern;
        this.desc = desc;
    }

    /**
     * 一个数如果是奇数的话，那么他的二进制最后一位一定为1,如果为奇数+1返回
     *
     * @return int
     */
    private static int initialCapacity() {
        return (values().length & 1) == 1 ? values().length + 1 : values().length;
    }

    private static void checkCache() {
        if (FORMAT_CACHE.isEmpty() || FORMAT_CACHE.size() != values().length) {
            FORMAT_CACHE.clear();
            for (DatePatternEnum datePatternEnum : values()) {
                FORMAT_CACHE.put(datePatternEnum, new SimpleDateFormat(datePatternEnum.getPattern()));
            }
        }
        if (FORMATTER_CACHE.isEmpty() || FORMATTER_CACHE.size() != values().length) {
            FORMATTER_CACHE.clear();
            for (DatePatternEnum datePatternEnum : values()) {
                FORMATTER_CACHE.put(datePatternEnum, DateTimeFormatter.ofPattern(datePatternEnum.getPattern()));
            }
        }
    }

    public int getIndex() {
        return index;
    }

    public String getPattern() {
        return pattern;
    }

    public String getDesc() {
        return desc;
    }

    public DateTimeFormatter getFormatter() {
        checkCache();
        return FORMATTER_CACHE.getOrDefault(this, DateTimeFormatter.ofPattern(getPattern()));
    }

    private SimpleDateFormat getUnSafeDateFormat() {
        checkCache();
        return FORMAT_CACHE.getOrDefault(this, new SimpleDateFormat(getPattern()));
    }

    public String format() {
        return LocalDateTime.now().format(getFormatter());
    }

    public String format(Date date) {
        if (Objects.isNull(date)) {
            return "";
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(getFormatter());
    }

    public String format(Long times) {
        if (Objects.isNull(times)) {
            return "";
        } else {
            return format(new Date(times));
        }
    }

    @SneakyThrows
    public Date parse(String dateText) {
        return getUnSafeDateFormat().parse(dateText);
    }

    /**
     * 是否合法
     * 
     * @param dateFormat
     *            日期格式
     * @return boolean
     */
    public boolean isIllegal(String dateFormat) {
        try {
            this.parse(dateFormat);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * currentDate 如何在compareDate前则返回true
     * 
     * @param currentDate
     *            2022-03-10
     * @param compareDate
     *            2022-03-12
     * @return boolean true
     */
    public static boolean isBefore(Date currentDate, Date compareDate) {
        return currentDate.compareTo(compareDate) < 0;
    }

    /**
     * currentDate 如何在compareDate前则返回false
     * 
     * @param currentDate
     *            2022-03-10
     * @param compareDate
     *            2022-03-12
     * @return boolean false
     */
    public static boolean isAfter(Date currentDate, Date compareDate) {
        return currentDate.compareTo(compareDate) > 0;
    }

    public static void main(String[] args) throws Exception {
        Date parse1 = DatePatternEnum.DATE_PATTERN.parse("2022-03-10");
        Date parse2 = DatePatternEnum.DATE_PATTERN.parse("2019-03-12");

        System.out.println(isBefore(parse1, parse2));
        System.out.println(isAfter(parse1, parse2));

    }

}
