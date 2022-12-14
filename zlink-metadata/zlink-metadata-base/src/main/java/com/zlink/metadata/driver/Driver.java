package com.zlink.metadata.driver;

import com.google.common.base.Preconditions;
import com.zlink.common.exception.MetaDataException;
import com.zlink.common.model.Column;
import com.zlink.common.model.Schema;
import com.zlink.common.model.SyncTableInfo;
import com.zlink.common.model.Table;
import com.zlink.metadata.query.IDBQuery;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * @author zs
 * @date 2022/11/29
 */
public interface Driver extends AutoCloseable {

    static Optional<Driver> get(DriverConfig config) {
        Preconditions.checkNotNull(config, "数据源配置不能为空");
        // 扫描 classpath 下所有的实现 Driver 的类
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers) {
            if (driver.canHandle(config.getType())) {
                return Optional.of(driver.setDriverConfig(config));
            }
        }
        return Optional.empty();
    }

    static Driver build(DriverConfig config) {
        String key = config.getName();
        if (DriverPool.exist(key)) {
            return getHealthDriver(key);
        }
        synchronized (Driver.class) {
            Optional<Driver> optionalDriver = Driver.get(config);
            if (!optionalDriver.isPresent()) {
                throw new MetaDataException("缺少数据源类型【" + config.getType() + "】的依赖，请在 lib 下添加对应的扩展依赖");
            }
            Driver driver = optionalDriver.get().connect();
            DriverPool.push(key, driver);
            return driver;
        }
    }

    static Driver getHealthDriver(String key) {
        Driver driver = DriverPool.get(key);
        if (driver.isHealth()) {
            return driver;
        } else {
            return driver.connect();
        }
    }

    Driver setDriverConfig(DriverConfig config);

    boolean isHealth();

    Driver connect();

    String test();

    String getType();

    void close();

    boolean canHandle(String type);

    IDBQuery getDBQuery();

    List<Schema> getSchemasAndTables();

    List<Column> listColumns(String schemaName, String tableName);

    String generateCreateTableSql(Table table, String targetSchema, SyncTableInfo syncTableInfo);

    boolean execute(String sql) throws Exception;
}
