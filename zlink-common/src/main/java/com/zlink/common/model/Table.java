package com.zlink.common.model;

import lombok.Data;

import java.util.List;

/**
 * @author zs
 * @date 2022/11/28
 */
@Data
public class Table {
    private String name;
    private String schema;
    private String catalog;
    private String comment;
    private String type;
    private String engine;
    private String options;
    private Long rows;
    // 列信息
    private List<Column> columns;
}
