package com.zlink.metadata.driver;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.zlink.common.assertion.Asserts;
import com.zlink.common.model.Schema;
import com.zlink.common.model.Table;
import com.zlink.metadata.query.IDBQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zs
 * @date 2022/11/29
 */
public abstract class AbstractDriver implements Driver {

    protected static Logger logger = LoggerFactory.getLogger(AbstractDriver.class);

    protected DriverConfig config;
    protected ThreadLocal<Connection> conn = new ThreadLocal<>();
    private DruidDataSource dataSource;


    public Driver setDriverConfig(DriverConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public boolean isHealth() {
        try {
            if (Asserts.isNotNull(conn.get())) {
                return !conn.get().isClosed();
            }
            return false;
        } catch (Exception e) {
            logger.error("{} 异常", config);
            return false;
        }
    }

    public DruidDataSource createDataSource() throws SQLException {
        if (null == dataSource) {
            synchronized (AbstractDriver.class) {
                if (null == dataSource) {
                    DruidDataSource ds = new DruidDataSource();
                    ds.setName(config.getName());
                    ds.setUrl(config.getUrl());
                    ds.setDriverClassName(config.getDriverClassName());
                    ds.setUsername(config.getUsername());
                    ds.setPassword(config.getPassword());
                    ds.init();
                    this.dataSource = ds;
                }
            }
        }
        return dataSource;
    }

    @Override
    public Driver connect() {
        if (null == conn.get()) {
            try {
                Class.forName(config.getDriverClassName());
                DruidPooledConnection connection = createDataSource().getConnection();
                conn.set(connection);
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    @Override
    public String test() {
        try {
            DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword()).close();
        } catch (Exception e) {
            logger.error("Jdbc链接测试失败！错误信息为：{}", e);
            return e.getMessage();
        }
        return "1";
    }

    @Override
    public boolean canHandle(String type) {
        return Asserts.isEqualsIgnoreCase(getType(), type);
    }

    @Override
    public void close() {
        try {
            if (null == conn.get()) {
                conn.get().close();
                conn.remove();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(PreparedStatement preparedStatement, ResultSet results) {
        try {
            if (Asserts.isNotNull(results)) {
                results.close();
            }
            if (Asserts.isNotNull(preparedStatement)) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Schema> getSchemasAndTables() {
        return listSchemas()
                .stream()
                .peek(schema -> schema.setTables(listTables(schema.getName())))
                .collect(Collectors.toList());
    }

    public abstract List<Schema> listSchemas();

    public List<Table> listTables(String schemaName) {
        List<Table> tableList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String sql = dbQuery.tablesSql(schemaName);
        try {
            preparedStatement = conn.get().prepareStatement(sql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            List<String> columnList = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnLabel(i));
            }
            while (results.next()) {
                Table tableInfo = new Table();
                String tableName = results.getString(dbQuery.tableName());
                tableInfo.setName(tableName);
                if (columnList.contains(dbQuery.tableComment())) {
                    tableInfo.setComment(results.getString(dbQuery.tableComment()));
                }
                tableInfo.setSchema(schemaName);
                if (columnList.contains(dbQuery.tableType())) {
                    tableInfo.setType(results.getString(dbQuery.tableType()));
                }
                if (columnList.contains(dbQuery.catalogName())) {
                    tableInfo.setCatalog(results.getString(dbQuery.catalogName()));
                }
                if (columnList.contains(dbQuery.engine())) {
                    tableInfo.setEngine(results.getString(dbQuery.engine()));
                }
                if (columnList.contains(dbQuery.options())) {
                    tableInfo.setOptions(results.getString(dbQuery.options()));
                }
                if (columnList.contains(dbQuery.rows())) {
                    tableInfo.setRows(results.getLong(dbQuery.rows()));
                }
                tableList.add(tableInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return tableList;
    }

}
