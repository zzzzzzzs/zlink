package com.zlink;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.junit.Test;

/**
 * @author zs
 * @date 2022/12/10
 */
public class MysqlCDCTest {
    @Test
    public void ddlTest(){
        String sql = "CREATE TABLE bbb (\n" +
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
        System.out.println(sql);
    }

    @Test
    public void cdcTest(){
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        tableEnv.getConfig().setSqlDialect(SqlDialect.DEFAULT);
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
    }

    @Test
    public void cdcStandAloneTest(){
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("192.168.52.154", 8081);
        env.setParallelism(1);
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
//        tableEnv.getConfig().setSqlDialect(SqlDialect.DEFAULT);
        String sourceDDL =
                "CREATE TABLE aaa (\n" +
                        " id INT,\n" +
                        " name STRING,\n" +
                        " primary key (id) not enforced\n" +
                        ") WITH (\n" +
                        " 'connector' = 'mysql-cdc',\n" +
                        " 'hostname' = 'localhost',\n" +
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
                        " 'url' = 'jdbc:mysql://localhost:3306/test',\n" +
                        " 'username' = 'root',\n" +
                        " 'password' = '123456',\n" +
                        " 'table-name' = 'bbb'\n" +
                        ")";
        // 简单的聚合处理
        String transformDmlSQL = "insert into bbb select * from aaa";


        tableEnv.executeSql(sourceDDL).print();
        tableEnv.executeSql(sinkDDL).print();
        tableEnv.executeSql(transformDmlSQL).print();
    }
}
