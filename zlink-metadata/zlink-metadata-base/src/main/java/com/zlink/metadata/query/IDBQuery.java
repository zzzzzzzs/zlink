package com.zlink.metadata.query;

/**
 * @author zs
 * @date 2022/11/30
 */
public interface IDBQuery {

    /**
     * 所有数据库信息查询 SQL
     */
    String schemaAllSql();

    /**
     * 表信息查询 SQL
     */
    String tablesSql(String schemaName);

    /**
     * 表字段信息查询 SQL
     */
    String columnsSql(String schemaName, String tableName);

    /**
     * 数据库、模式、组织名称
     */
    String schemaName();

    /**
     * catalog 名称
     */
    String catalogName();

    /**
     * 表名称
     */
    String tableName();

    /**
     * 表注释
     */
    String tableComment();

    /**
     * 表类型
     */
    String tableType();

    /**
     * 表引擎
     */
    String engine();

    /**
     * 表配置
     */
    String options();

    /**
     * 表记录数
     */
    String rows();

    /**
     * 字段名称
     */
    String columnName();

    /**
     * 字段类型
     */
    String columnType();

    /**
     * 数据类型
     */
    String dataType();

    /**
     * 字段长度
     */
    String columnLength();

    /**
     * @return 小数范围
     */
    String scale();

    /**
     * 字段注释
     */
    String columnComment();

    /**
     * 主键字段
     */
    String columnKey();

    /**
     * 默认值
     */
    String defaultValue();

    /**
     * @return 是否允许为 NULL
     */
    String isNullable();

    /**
     * 字段序号
     */
    String columnPosition();
}
