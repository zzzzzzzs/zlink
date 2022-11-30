package com.zlink.common.model;

import lombok.Data;

import java.util.List;

/**
 * @author zs
 * @date 2022/11/28
 */
@Data
public class Schema {
    private String name;
    private List<Table> tables;
    private List<String> views;
    private List<String> functions;
    private List<String> userFunctions;
    private List<String> modules;
}
