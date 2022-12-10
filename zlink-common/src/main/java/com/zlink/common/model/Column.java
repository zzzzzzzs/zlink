package com.zlink.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zs
 * @date 2022/12/1
 */
@Data
@Accessors(chain = true)
public class Column {
    private String name;
    private String columnType;
    private String dataType;
    private Long length;
    private Integer scale;
    private String comment;
    private String columnKey;
    private String defaultValue;
    private String isNullable;
    private Integer position;
    // 对应的 Java 类型
    private JavaType javaType;
    // 对应的 flinksql 类型
    private FlinkType flinkType;
}
