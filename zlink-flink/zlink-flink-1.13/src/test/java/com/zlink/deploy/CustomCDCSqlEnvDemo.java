package com.zlink.deploy;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.TableConfig;

// 使用自定义 CustomTableEnvironmentImpl cdc
public class CustomCDCSqlEnvDemo {
    public static void main(String[] args) {

        TableConfig tableConfig = new TableConfig();
        tableConfig.setSqlDialect(SqlDialect.DEFAULT);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(3000);
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        CustomTableEnvironmentImpl tableEnv = CustomTableEnvironmentImpl.create(env, settings, tableConfig);

        String sourceDDL = "CREATE TABLE user_info_source (\n" +
                "`id` INT,\n" +
                "`name` STRING,\n" +
                "`age` INT,\n" +
                "primary key (id) not enforced\n" +
                ") with ( \n" +
                " 'connector' = 'mysql-cdc',\n" +
                " 'hostname' = '192.168.25.110',\n" +
                " 'port' = '3366',\n" +
                " 'username' = 'root',\n" +
                " 'password' = '111',\n" +
                " 'database-name' = 'test',\n" +
                " 'table-name' = 'user_info',\n" +
                " 'scan.startup.mode' = 'initial'\n" +
                ")";
        // 输出目标表
        String sinkDDL = "CREATE TABLE user_info_sink (\n" +
                "`id` INT,\n" +
                "`name` STRING,\n" +
                "`age` INT,\n" +
                "primary key (id) not enforced\n" +
                ") with ( \n" +
                " 'connector' = 'jdbc',\n" +
                " 'driver' = 'com.mysql.cj.jdbc.Driver',\n" +
                " 'url' = 'jdbc:mysql://192.168.25.110:3366/cdc',\n" +
                " 'username' = 'root',\n" +
                " 'password' = '111',\n" +
                " 'table-name' = 'user_info'\n" +
                ")";
        // 简单的聚合处理
        String transformDmlSQL = "upsert into user_info_sink select * from user_info_source";

        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(sinkDDL);
        tableEnv.executeSql(transformDmlSQL);
    }
}
