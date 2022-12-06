package com.zlink;

import com.zlink.metadata.driver.Driver;
import com.zlink.metadata.driver.DriverConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ServiceLoader;

@SpringBootTest
class ZlinkAdminApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void jdbcall() throws SQLException {
        String url = "jdbc:mysql://192.168.52.154:3306/zlink";
        String username = "root";
        String password = "123456";
        Connection conn = DriverManager.getConnection(url, username, password);//用参数得到连接对象
        System.out.println("连接成功！");
        System.out.println(conn);
    }

    @Test
    public void spiTest() {
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers) {
            System.out.println(driver);
        }
    }

    @Test
    public void flinkCDCTest() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 每隔1000 ms进行启动一个检查点【设置checkpoint的周期】
        env.setParallelism(1);

        EnvironmentSettings Settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, Settings);
        tableEnv.getConfig().setSqlDialect(SqlDialect.DEFAULT);

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

        tableEnv.executeSql(sourceDDL).print();
        tableEnv.executeSql(sinkDDL);
        tableEnv.executeSql(transformDmlSQL).print();

        env.execute("sync-flink-cdc");
    }

}
