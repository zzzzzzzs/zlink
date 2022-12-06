package com.zlink.common.model;

import lombok.Data;

/**
 * @author zs
 * @date 2022/12/1
 */
@Data
public class JavaType {
    private String type;
    // 字符串长度 & 数值类型字节大小
    private Long size;
    private Integer scale;
}
