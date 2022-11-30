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
}
