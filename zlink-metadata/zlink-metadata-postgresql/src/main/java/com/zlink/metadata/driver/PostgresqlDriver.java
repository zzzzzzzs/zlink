package com.zlink.metadata.driver;

import com.zlink.common.assertion.Asserts;
import com.zlink.common.model.*;
import com.zlink.metadata.query.IDBQuery;
import com.zlink.metadata.query.PostgresqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zs
 * @date 2022/11/28
 */
public class PostgresqlDriver extends AbstractDriver {

    private static Logger logger = LoggerFactory.getLogger(PostgresqlDriver.class);


    @Override
    public IDBQuery getDBQuery() {
        return new PostgresqlQuery();
    }

    @Override
    public String getType() {
        return "postgresql";
    }

    @Override
    public List<Schema> listSchemas() {
        List<Schema> schemas = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        String schemasSql = getDBQuery().schemaAllSql();
        logger.info("schemasSql is {}", schemasSql);
        try {
            preparedStatement = conn.get().prepareStatement(schemasSql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                String schemaName = results.getString(getDBQuery().schemaName());
                schemas.add(new Schema(schemaName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return schemas;
    }

    @Override
    public List<Column> listColumns(String schemaName, String tableName) {
        List<Column> columns = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String columnsSql = dbQuery.columnsSql(schemaName, tableName);
        logger.info("columnsSql is : {}", columnsSql);
        try {
            preparedStatement = conn.get().prepareStatement(columnsSql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            List<String> columnList = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnLabel(i));
            }
            // TODO 后面优化
            while (results.next()) {
                Column column = new Column();
                column.setName(results.getString(dbQuery.columnName()));
                if (columnList.contains(dbQuery.columnType())) {
                    column.setColumnType(results.getString(dbQuery.columnType()));
                }
                if (columnList.contains(dbQuery.dataType())) {
                    column.setDataType(results.getString(dbQuery.dataType()));
                }
                if (columnList.contains(dbQuery.columnLength())) {
                    column.setLength(results.getLong(dbQuery.columnLength()));
                }
                if (columnList.contains(dbQuery.scale())) {
                    column.setScale(results.getInt(dbQuery.scale()));
                }
                if (columnList.contains(dbQuery.columnComment())) {
                    column.setComment(results.getString(dbQuery.columnComment()));
                }
                if (columnList.contains(dbQuery.columnKey())) {
                    column.setColumnKey(results.getString(dbQuery.columnKey()));
                }
                if (columnList.contains(dbQuery.defaultValue())) {
                    column.setDefaultValue(results.getString(dbQuery.defaultValue()));
                }
                if (columnList.contains(dbQuery.isNullable())) {
                    column.setIsNullable(results.getString(dbQuery.isNullable()));
                }
                if (columnList.contains(dbQuery.columnPosition())) {
                    column.setPosition(results.getInt(dbQuery.columnPosition()));
                }
                mapJavaType(column);
                mapFlinkType(column);
                columns.add(column);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return columns;
    }


    // TODO 后期优化
    // postgresql -> java
    private void mapJavaType(Column column) {
        JavaType javaType = new JavaType();
        switch (column.getDataType()) {
            // 数值相关
            case "int4":
                javaType.setType("java.lang.Integer");
                javaType.setSize(8L);
                break;
            case "tinyint":
                javaType.setType("java.lang.Integer");
                javaType.setSize(4L);
                break;
            case "binary":
                break;
            case "blob":
                break;
            case "decimal":
                javaType.setType("java.math.BigDecimal");
                javaType.setSize(column.getLength());
                javaType.setScale(column.getScale());
                break;
            case "double":
                javaType.setType("java.lang.Double");
                javaType.setSize(column.getLength());
                javaType.setScale(column.getScale());
                break;
            case "enum":
                break;
            case "float":
                javaType.setType("java.lang.Float");
                javaType.setSize(column.getLength());
                javaType.setScale(column.getScale());
                break;
            case "int":
                javaType.setType("java.lang.Integer");
                javaType.setSize(4L);
                break;
            case "json":
                javaType.setType("java.lang.String");
                javaType.setSize((long) Integer.MAX_VALUE);
                break;
            case "longblob":
                break;
            case "mediumblob":
                break;
            case "mediumtext":
                break;
            case "set":
                break;
            case "smallint":
                javaType.setType("java.lang.Integer");
                javaType.setSize(4L);
                break;
            // 时间相关
            case "time":
                javaType.setType("java.lang.String");
                javaType.setSize(32L);
                break;
            case "timestamp":
                javaType.setType("java.lang.String");
                javaType.setSize(32L);
                break;
            case "datetime":
                javaType.setType("java.lang.String");
                javaType.setSize(32L);
                break;
            case "date":
                javaType.setType("java.lang.String");
                javaType.setSize(32L);
                break;
            case "varbinary":
                javaType.setType("java.lang.String");
                javaType.setSize(column.getLength());
                break;
            // 字符串相关
            case "varchar":
                javaType.setType("java.lang.String");
                javaType.setSize(column.getLength());
                break;
            case "char":
                javaType.setType("java.lang.String");
                javaType.setSize(column.getLength());
                break;
            case "text":
                javaType.setType("java.lang.String");
                javaType.setSize((long) Integer.MAX_VALUE);
                break;
            case "longtext":
                javaType.setType("java.lang.String");
                javaType.setSize((long) Integer.MAX_VALUE);
                break;
            default:
                javaType.setType("java.lang.String");
                javaType.setSize(1024L);
                break;
        }
        column.setJavaType(javaType);
    }

    // Flink 类型映射
    // https://nightlies.apache.org/flink/flink-docs-release-1.16/docs/dev/table/types/
    private void mapFlinkType(Column column) {
        FlinkType flinkType = new FlinkType();
        switch (column.getJavaType().getType()) {
            case "java.lang.String":
                flinkType.setType("STRING");
                break;
            case "java.lang.Boolean":
                flinkType.setType("BOOLEAN");
                break;
            case "java.lang.Byte":
                flinkType.setType("TINYINT");
                break;
            case "java.lang.Short":
                flinkType.setType("SMALLINT");
                break;
            case "java.lang.Integer":
                flinkType.setType("INT");
                break;
            case "java.lang.Long":
                flinkType.setType("BIGINT");
                break;
            case "java.lang.Float":
                flinkType.setType("FLOAT");
                break;
            case "java.lang.Double":
                flinkType.setType("DOUBLE");
                break;
            case "java.sql.Date":
                flinkType.setType("DATE");
                break;
            case "java.time.LocalDate":
                flinkType.setType("DATE");
                break;
            case "java.sql.Time":
                flinkType.setType("TIME(0)");
                break;
            case "java.time.LocalTime":
                flinkType.setType("TIME(9)");
                break;
            case "java.sql.Timestamp":
                flinkType.setType("TIMESTAMP(9)");
                break;
            case "java.time.LocalDateTime":
                flinkType.setType("TIMESTAMP(9)");
                break;
            case "java.time.OffsetDateTime":
                flinkType.setType("TIMESTAMP(9) WITH TIME ZONE");
                break;
            case "java.time.Instant":
                flinkType.setType("TIMESTAMP_LTZ(9)");
                break;
            case "java.time.Duration":
                flinkType.setType("INTERVAL SECOND(9)");
                break;
            case "java.time.Period":
                flinkType.setType("INTERVAL YEAR(4) TO MONTH");
                break;
            default:
                break;
        }
        column.setFlinkType(flinkType);
    }


    // java -> postgresql
    private String mapPostGresqlType(JavaType javaType) {
        switch (javaType.getType()) {
            case "java.lang.Integer":
                return String.format("int4");
            case "java.lang.Long":
                return String.format("int8");
            case "java.lang.Double":
                return String.format("double(%s, %s)", javaType.getSize(), javaType.getScale());
            case "java.math.BigDecimal":
                return String.format("decimal(%s, %s)", javaType.getSize(), javaType.getScale());
            case "java.lang.Float":
                return String.format("float(%s, %s)", javaType.getSize(), javaType.getScale());
            case "java.lang.String":
                if (javaType.getSize() >= Integer.MAX_VALUE) {
                    return String.format("text");
                }
                return String.format("varchar(%s)", javaType.getSize());
            default:
                return String.format("varchar(%s)", 1024);
        }
    }

    @Override
    public String generateCreateTableSql(Table table, String targetSchema, SyncTableInfo syncTableInfo) {
        syncTableInfo =
                new SyncTableInfo(syncTableInfo.getTablePrefix().trim().isBlank() == true ? "" : syncTableInfo.getTablePrefix() + "_",
                        syncTableInfo.getTableSuffix().trim().isBlank() == true ? "" : "_" + syncTableInfo.getTableSuffix());

        StringBuilder sb = new StringBuilder();
        StringBuilder comments = new StringBuilder();
        sb.append("CREATE TABLE \"")
                .append(targetSchema)
                .append("\".\"")
                .append(syncTableInfo.getTablePrefix())
                .append(table.getName())
                .append(syncTableInfo.getTableSuffix())
                .append("\" (\r\n");

        for (Column column : table.getColumns()) {
            sb.append("  \"").append(column.getName()).append("\" ")
                    .append(mapPostGresqlType(column.getJavaType()))
            ;

            sb.append(",\r\n");
            // 字段注释
            if (Asserts.isNotNullString(column.getComment())) {
                comments.append("COMMENT ON COLUMN \"").append(targetSchema).append("\".\"")
                        .append(table.getName()).append("\".\"")
                        .append(column.getName()).append("\" IS '").append(column.getComment()).append("';\r\n");
            }
        }
        sb.deleteCharAt(sb.length() - 3);

        // 表注释
        if (Asserts.isNotNullString(table.getComment())) {
            comments.append("COMMENT ON TABLE \"").append(targetSchema).append("\".\"")
                    .append(table.getName()).append("\" IS '").append(table.getComment()).append("';");
        }
        sb.append(")\r\n;\r\n").append(comments);
        logger.info("postgresql auto generateCreateTableSql {}", sb);
        return sb.toString();
    }
}
