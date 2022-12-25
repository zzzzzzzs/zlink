package com.zlink.metadata.query;

/**
 * @author zs
 * @date 2022/11/30
 */
public class PostgresqlQuery implements IDBQuery {

    @Override
    public String schemaAllSql() {
        return "SELECT nspname AS schema_name\n" +
                "FROM pg_namespace\n" +
                "WHERE nspname NOT LIKE 'pg_%'\n" +
                "  AND nspname != 'information_schema'\n" +
                "ORDER BY nspname";
    }

    @Override
    public String tablesSql(String schemaName) {
        return "SELECT n.nspname              AS schema_name\n" +
                "     , c.relname              AS table_name\n" +
                "     , obj_description(c.oid) AS table_comment\n" +
                "     , c.reltuples            as rows\n" +
                "FROM pg_class c\n" +
                "         LEFT JOIN pg_namespace n ON n.oid = c.relnamespace\n" +
                "WHERE ((c.relkind = 'r'::\"char\") OR (c.relkind = 'f'::\"char\") OR (c.relkind = 'p'::\"char\"))\n" +
                "  AND n.nspname = '" + schemaName + "'\n" +
                "ORDER BY n.nspname, table_name";
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return "SELECT col.column_name                              as column_name\n" +
                "     , col.udt_name                                 as data_type\n" +
                "     , case\n" +
                "           when col.character_maximum_length is not null then col.character_maximum_length\n" +
                "           else col.numeric_precision end           as column_length\n" +
                "     , col.numeric_scale                            as scale\n" +
                "     , col_description(c.oid, col.ordinal_position) AS column_comment\n" +
                "     , (CASE\n" +
                "            WHEN (SELECT COUNT(*) FROM pg_constraint AS PC WHERE b.attnum = PC.conkey[1] AND PC.contype = 'p') > 0\n" +
                "                THEN 'PRI'\n" +
                "            ELSE '' END)                            AS column_key\n" +
                "     , col.column_default                           AS default_value\n" +
                "     , col.is_nullable                              as is_nullable\n" +
                "     , col.ordinal_position                         as column_position\n" +
                "FROM information_schema.columns AS col\n" +
                "         LEFT JOIN pg_namespace ns ON ns.nspname = col.table_schema\n" +
                "         LEFT JOIN pg_class c ON col.table_name = c.relname AND c.relnamespace = ns.oid\n" +
                "         LEFT JOIN pg_attribute b ON b.attrelid = c.oid AND b.attname = col.column_name\n" +
                "WHERE col.table_schema = '" + schemaName + "'\n" +
                "  AND col.table_name = '" + tableName + "'\n" +
                "ORDER BY col.table_schema, col.table_name, col.ordinal_position";
    }

    @Override
    public String schemaName() {
        return "schema_name";
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
