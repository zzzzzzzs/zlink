package com.zlink.deploy;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;

public class DatagenSqlDemo {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        TableConfig tableConfig = new TableConfig();
        StreamTableEnvironment tableEnv = StreamTableEnvironmentImpl.create(env, settings, tableConfig);

        String sql1 = "CREATE TABLE datagen_1 (\n" +
                " f0 INT,\n" +
                " f1 INT,\n" +
                " f2 STRING\n" +
                ") WITH (\n" +
                " 'connector' = 'datagen',\n" +
                " 'rows-per-second'='5',\n" +
                " 'fields.f0.kind'='sequence',\n" +
                " 'fields.f0.start'='1',\n" +
                " 'fields.f0.end'='1000',\n" +
                " 'fields.f1.min'='1',\n" +
                " 'fields.f1.max'='1000',\n" +
                " 'fields.f2.length'='10'\n" +
                ")";
        tableEnv.executeSql(sql1);
        String sql2 = "CREATE TABLE print_table (\n" +
                "  f0 INT,\n" +
                "  f1 INT,\n" +
                "  f2 STRING\n" +
                ") WITH (\n" +
                "  'connector' = 'print'\n" +
                ")";

        tableEnv.executeSql(sql2);
        tableEnv.executeSql("insert into print_table select * from datagen_1");
    }
}
