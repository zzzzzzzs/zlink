package com.zlink.metadata.driver;

import com.zlink.common.assertion.Asserts;
import com.zlink.common.model.Column;
import com.zlink.common.model.JavaType;
import com.zlink.common.model.Schema;
import com.zlink.common.model.Table;
import com.zlink.metadata.query.IDBQuery;
import com.zlink.metadata.query.MySqlQuery;
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
public class MySqlDriver extends AbstractDriver {

    protected static Logger logger = LoggerFactory.getLogger(MySqlDriver.class);


    @Override
    public IDBQuery getDBQuery() {
        return new MySqlQuery();
    }

    @Override
    public String getType() {
        return "mysql";
    }

    @Override
    public List<Schema> listSchemas() {
        List<Schema> schemas = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        String schemasSql = getDBQuery().schemaAllSql();
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
    // Java 类型映射
    private void mapJavaType(Column column) {
        JavaType javaType = new JavaType();
        switch (column.getDataType()) {
            // 数值相关
            case "bigint":
                javaType.setType("java.lang.Long");
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

    private String mapMysqlType(JavaType javaType) {
        switch (javaType.getType()) {
            case "java.lang.Integer":
                return String.format("int");
            case "java.lang.Long":
                return String.format("bigint");
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
    public String generateCreateTableSql(Table table, String targetSchema) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ")
                .append(targetSchema)
                .append(".")
                .append(table.getName()).append(" (\n");
        for (Column column : table.getColumns()) {
            try {
                sb.append("  `")
                        .append(column.getName()).append("`  ")
                        .append(mapMysqlType(column.getJavaType()));
                if (Asserts.isAllNotNullString(column.getComment())) {
                    sb.append(" COMMENT '").append(column.getComment()).append("'");
                }
                sb.append(",\r\n");
            } catch (Exception e) {
                logger.error("Column {} 出现异常 {}", column, e);
            }
        }
        sb.deleteCharAt(sb.length() - 3);
        sb.append(") ENGINE=InnoDB ");
        if (Asserts.isNotNullString(table.getComment())) {
            sb.append(" COMMENT='").append(table.getComment()).append("'");
        }
        sb.append(";");
        logger.info("Auto generateCreateTableSql {}", sb);
        return sb.toString();
    }
}
