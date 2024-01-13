package com.github.tomato.util.table;

import com.github.tomato.util.EnhanceStream;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 动态表头
 *
 * @author liuxin 2021/4/23 2:26 下午
 */
public class DynamicTableHeaderUtils {

    public static <T> List<TableHeaderVO> buildTableHeaders(Class<?> data) {
        List<TableHeaderVO> result = new ArrayList<>();
        Field[] declaredFields = data.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            TableHeaderVO tableHeaderVO = new TableHeaderVO();
            String fieldName = declaredField.getName();
            TableHeader annotation = AnnotationUtils.findAnnotation(declaredField, TableHeader.class);
            // 如果没有注解,动态表头,都以表字段名为准
            if (Objects.nonNull(annotation)) {
                tableHeaderVO.setColumnComment(annotation.value());
                tableHeaderVO.setTimestamp(annotation.timestamp());
                tableHeaderVO.setColumnName(fieldName);
                tableHeaderVO.setSub(annotation.sub());
                result.add(tableHeaderVO);
            }
        }
        return result;
    }

    /**
     * 生成动态表头
     *
     * @param dataSources 表格数据
     * @param <T>         数据类型
     * @return List<TableHeaderVO>
     */
    public static <T> List<TableHeaderVO> buildTableHeaders(List<T> dataSources) {
        if (EnhanceStream.isEmpty(dataSources)) {
            return EnhanceStream.emptyList();
        }
        List<TableHeaderVO> result = new ArrayList<>();
        Optional<T> firstOpt = EnhanceStream.findFirst(dataSources);
        if (firstOpt.isPresent()) {
            T t = firstOpt.get();
            Class<?> dataTypeClass = t.getClass();
            Field[] declaredFields = dataTypeClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                TableHeaderVO tableHeaderVO = new TableHeaderVO();
                String fieldName = declaredField.getName();
                TableHeader annotation = AnnotationUtils.findAnnotation(declaredField, TableHeader.class);
                // 如果没有注解,动态表头,都以表字段名为准
                if (Objects.nonNull(annotation)) {
                    tableHeaderVO.setColumnComment(annotation.value());
                    tableHeaderVO.setTimestamp(annotation.timestamp());
                    tableHeaderVO.setSub(annotation.sub());
                    tableHeaderVO.setColumnName(fieldName);
                    result.add(tableHeaderVO);
                }
            }
        }
        return result;
    }


}
