package com.zlink.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zlink.common.model.Column;
import com.zlink.common.model.Schema;
import com.zlink.common.model.Table;
import com.zlink.common.utils.JacksonObject;
import com.zlink.dao.DatasourceMapper;
import com.zlink.entity.JobJdbcDatasource;
import com.zlink.metadata.driver.Driver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zs
 * @since 2022-11-26
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetaDataService extends ServiceImpl<DatasourceMapper, JobJdbcDatasource> {

    public List<Schema> getSchemaAndTable(Integer id) {
        JobJdbcDatasource datasource = getById(id);
        Driver driver = Driver.build(datasource.getDriverConfig());
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        driver.close();
        return schemasAndTables;
    }

    public List<Column> listColumns(Integer id, String schemaName, String tableName) {
        JobJdbcDatasource dataBase = getById(id);
        Driver driver = Driver.build(dataBase.getDriverConfig());
        List<Column> columns = driver.listColumns(schemaName, tableName);
        driver.close();
        return columns;
    }


    public boolean syncTableStruct(JacksonObject json) {
        Integer sourceId = json.getBigInteger("sourceId").intValue();
        Integer targetId = json.getNode("targetData").get("targetId").asInt();
        String targetSchema = json.getNode("targetData").get("targetSchema").asText();
        List<Table> tables = json.getObject("tables", new TypeReference<List<Table>>() {
        });
        // 获取列信息
        JobJdbcDatasource source = getById(sourceId);
        JobJdbcDatasource target = getById(targetId);
        Driver sourceDriver = Driver.build(source.getDriverConfig());
        tables.forEach(ele -> {
            ele.setColumns(sourceDriver.listColumns(ele.getSchema(), ele.getName()));
        });
        // 创建表
        Driver targetDriver = Driver.build(target.getDriverConfig());
        try {
            List<String> targetCreateTableSqls = tables.stream().map(ele -> targetDriver.generateCreateTableSql(ele, targetSchema)).collect(Collectors.toList());
            for (String sql : targetCreateTableSqls) {
                targetDriver.execute(sql);
            }
            sourceDriver.close();
            targetDriver.close();
            return true;
        } catch (Exception e) {
            log.error("出现错误 {}", e);
            return false;
        } finally {
            sourceDriver.close();
            targetDriver.close();
        }
    }

    public Object localFlinkCDC(JacksonObject json) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 每隔1000 ms进行启动一个检查点【设置checkpoint的周期】
        env.setParallelism(1);

        EnvironmentSettings Settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, Settings);
        tableEnv.getConfig().setSqlDialect(SqlDialect.DEFAULT);


        // 生成数据源表

        // 数据源表
        String sourceDDL =
                "CREATE TABLE aaa (\n" +
                        " id INT,\n" +
                        " name STRING,\n" +
                        " primary key (id) not enforced\n" +
                        ") WITH (\n" +
                        " 'connector' = 'mysql-cdc',\n" +
                        " 'hostname' = '192.168.52.154',\n" +
                        " 'port' = '3306',\n" +
                        " 'username' = 'root',\n" +
                        " 'password' = '123456',\n" +
                        " 'database-name' = 'test',\n" +
                        " 'table-name' = 'aaa',\n" +
                        " 'scan.startup.mode' = 'initial'\n" +
                        ")";
        // 输出目标表
        String sinkDDL =
                "CREATE TABLE bbb (\n" +
                        " id INT,\n" +
                        " name STRING,\n" +
                        " primary key (id) not enforced\n" +
                        ") WITH (\n" +
                        " 'connector' = 'jdbc',\n" +
                        " 'driver' = 'com.mysql.cj.jdbc.Driver',\n" +
                        " 'url' = 'jdbc:mysql://192.168.52.154:3306/test',\n" +
                        " 'username' = 'root',\n" +
                        " 'password' = '123456',\n" +
                        " 'table-name' = 'bbb'\n" +
                        ")";
        // 简单的聚合处理
        String transformDmlSQL = "insert into bbb select * from aaa";

        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(sinkDDL);
        tableEnv.executeSql(transformDmlSQL);

        env.execute("sync-flink-cdc");
        return null;
    }
}
