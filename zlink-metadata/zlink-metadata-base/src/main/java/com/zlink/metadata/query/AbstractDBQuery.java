package com.zlink.metadata.query;

/**
 * @author zs
 * @date 2022/11/29
 */
public abstract class AbstractDBQuery {


    /**
     * 所有数据库信息查询 SQL
     */
    abstract String schemaAllSql();

    /**
     * 表信息查询 SQL
     */
    abstract String tablesSql(String schemaName);

    /**
     * 表字段信息查询 SQL
     */
    abstract String columnsSql(String schemaName, String tableName);

    /**
     * 建表 SQL
     */
    abstract String createTableSql(String schemaName, String tableName);

    /**
     * 建表语句列名
     */
    abstract String createTableName();

    /**
     * 建视图语句列名
     */
    abstract String createViewName();

    /**
     * 数据库、模式、组织名称
     */
    abstract String schemaName();

    /**
     * catalog 名称
     */
    abstract String catalogName();

    /**
     * 表名称
     */
    abstract String tableName();

    /**
     * 表注释
     */
    abstract String tableComment();

    /**
     * 表类型
     */
    abstract String tableType();

    /**
     * 表引擎
     */
    abstract String engine();

    /**
     * 表配置
     */
    abstract String options();

    /**
     * 表记录数
     */
    abstract String rows();

    /**
     * 创建时间
     */
    abstract String createTime();

    /**
     * 更新时间
     */
    abstract String updateTime();

    /**
     * 字段名称
     */
    abstract String columnName();

    /**
     * 字段序号
     */
    abstract String columnPosition();

    /**
     * 字段类型
     */
    abstract String columnType();

    /**
     * 字段长度
     */
    abstract String columnLength();

    /**
     * 字段注释
     */
    abstract String columnComment();

    /**
     * 主键字段
     */
    abstract String columnKey();

    /**
     * 主键自增
     */
    abstract String autoIncrement();

    /**
     * 默认值
     */
    abstract String defaultValue();

    /**
     * @return 是否允许为 NULL
     */
    abstract String isNullable();

    /**
     * @return 精度
     */
    abstract String precision();

    /**
     * @return 小数范围
     */
    abstract String scale();

    /**
     * @return 字符集名称
     */
    abstract String characterSet();

    /**
     * @return 排序规则
     */
    abstract String collation();

    /**
     * 自定义字段名称
     */
    abstract String[] columnCustom();

    /**
     * @return 主键值
     */
    abstract String isPK();

    /**
     * @return 允许为空的值
     */
    abstract String nullableValue();
}
