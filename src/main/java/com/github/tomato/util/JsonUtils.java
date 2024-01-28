package com.github.tomato.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author liuxin 2023/2/28 21:35
 */
@Slf4j
public class JsonUtils {

    private static ObjectMapper mapper;

    static {
        // jackson
        mapper = new ObjectMapper();

        // 设置日期格式
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 禁用空对象转换json
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 设置null值不参与序列化(字段不被显示)
        // mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * json字符串转对象
     */
    public static <T> T parse(String jsonStr, Class<T> clazz) {
        try {
            return mapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            // 输出到日志文件中
            log.error(ErrorUtils.errorInfoToString(e));
        }
        return null;
    }

    public static <T> List<T> parseList(String jsonStr, TypeReference<List<T>> clazz) {
        try {
            return (mapper.readValue(jsonStr, clazz));
        } catch (Exception e) {
            // 输出到日志文件中
            log.error(ErrorUtils.errorInfoToString(e));
        }
        return null;
    }

    public static <T> List<T> parseList(String jsonStr, Class<T> clazz) {
        try {
            return mapper.convertValue(mapper.readValue(jsonStr, new TypeReference<List<T>>() {
            }), new TypeReference<List<T>>() {
            })
                    ;
        } catch (Exception e) {
            // 输出到日志文件中
            log.error(ErrorUtils.errorInfoToString(e));
        }
        return null;
    }

    /**
     * 对象转json字符串
     */
    public static String stringify(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            // 输出到日志文件中
            log.error(ErrorUtils.errorInfoToString(e));
        }
        return null;
    }

}
