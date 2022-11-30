package com.zlink.metadata.driver;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.zlink.common.assertion.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
}
