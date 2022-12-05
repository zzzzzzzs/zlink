package com.zlink.metadata.query;

/**
 * @author zs
 * @date 2022/11/30
 */
public class MySqlQuery implements IDBQuery {

    @Override
    public String schemaAllSql() {
        return "show databases";
    }

    @Override
    public String tablesSql(String schemaName) {
        return "select table_name     as `table_name`,\n" +
                "       table_schema   as `database`,\n" +
                "       table_comment  as `table_comment`,\n" +
                "       table_catalog  as `catalog`,\n" +
                "       table_type     as `table_type`,\n" +
                "       engine         as `engine`,\n" +
                "       create_options as `options`,\n" +
                "       table_rows     as `rows`\n" +
                "from information_schema.tables\n" +
                "where table_schema = '" + schemaName + "'";
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return "select column_name                    as column_name\n" +
                "     , column_type                    as column_type\n" +
                "     , data_type                      as data_type\n" +
                "     , case\n" +
                "           when character_maximum_length is not null then character_maximum_length\n" +
                "           else numeric_precision end as column_length\n" +
                "     , numeric_scale                  as scale\n" +
                "     , column_comment                 as column_comment\n" +
                "     , column_key                     as column_key\n" +
                "     , column_default                 as default_value\n" +
                "     , is_nullable                    as is_nullable\n" +
                "     , ordinal_position               as column_position\n" +
                "from information_schema.columns\n" +
                "where table_schema = '" + schemaName + "'\n" +
                "  and table_name = '" + tableName + "'\n" +
                "order by ordinal_position;\n";
    }

    @Override
    public String schemaName() {
        return "Database";
    }

    @Override
    public String catalogName() {
        return "catalog";
    }

    @Override
    public String tableName() {
        return "table_name";
    }

    @Override
    public String tableComment() {
        return "table_comment";
    }

    @Override
    public String tableType() {
        return "table_type";
    }

    @Override
    public String engine() {
        return "engine";
    }

    @Override
    public String options() {
        return "options";
    }

    @Override
    public String rows() {
        return "rows";
    }

    @Override
    public String columnName() {
        return "column_name";
    }

    @Override
    public String columnType() {
        return "column_type";
    }

    @Override
    public String dataType() {
        return "data_type";
    }

    @Override
    public String columnLength() {
        return "column_length";
    }

    @Override
    public String scale() {
        return "scale";
    }

    @Override
    public String columnComment() {
        return "column_comment";
    }

    @Override
    public String columnKey() {
        return "column_key";
    }

    @Override
    public String defaultValue() {
        return "default_value";
    }

    @Override
    public String isNullable() {
        return "is_nullable";
    }

    @Override
    public String columnPosition() {
        return "column_position";
    }
}
