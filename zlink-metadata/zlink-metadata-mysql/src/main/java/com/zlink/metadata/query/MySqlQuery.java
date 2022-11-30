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
        return "select table_name     as `name`,\n" +
                "       table_schema   as `database`,\n" +
                "       table_comment  as `comment`,\n" +
                "       table_catalog  as `catalog`,\n" +
                "       table_type     as `type`,\n" +
                "       engine         as `engine`,\n" +
                "       create_options as `options`,\n" +
                "       table_rows     as `rows`\n" +
                "from information_schema.tables\n" +
                "where table_schema = '" + schemaName + "'";
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return null;
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
        return "name";
    }

    @Override
    public String tableComment() {
        return "comment";
    }

    @Override
    public String tableType() {
        return "type";
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
}
