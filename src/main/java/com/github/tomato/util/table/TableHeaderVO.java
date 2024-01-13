package com.github.tomato.util.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author liuxin
 * 2021/4/23 2:29 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableHeaderVO implements Serializable {

    /**
     * 列字段名
     */
    String columnName;

    /**
     * 表头列描述
     */
    String columnComment;

    /**
     * 时间戳
     */
    Boolean timestamp;

    /**
     * 是否有字菜单
     */
    Boolean sub;
}
