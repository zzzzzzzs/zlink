package com.zlink.cdc.mysql;

import com.zlink.cdc.FlinkCDCConfig;
import com.zlink.common.model.Column;
import com.zlink.common.model.Table;
import com.zlink.common.utils.NetUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zs
 * @date 2022/12/8
 */
public class MysqlCDCBuilder {

    private static Logger logger = LoggerFactory.getLogger(MysqlCDCBuilder.class);


    public static StreamTableEnvironment create(int port) {
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, port);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        env.setParallelism(1);
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        tableEnv.getConfig().setSqlDialect(SqlDialect.DEFAULT);
        return tableEnv;
    }

    // 生成数据源表
    public static String genFlinkSourceDDL(FlinkCDCConfig config) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("CREATE TABLE ")
                .append(config.getSourceTable().getName()).append(" (").append("\n")
        ;
        Table table = config.getSourceTable();
        for (Column column : table.getColumns()) {
            sb.append(column.getName()).append(" ").append(column.getFlinkType().getType()).append(",\n");
        }
        sb.append("primary key (id) not enforced\n");
        sb.append(") with ( \n")
                .append(" 'connector' = 'mysql-cdc',\n")
                .append(" 'hostname' = '").append(config.getSourceHostname()).append("',\n")
                .append(" 'port' = '").append(config.getSourcePort()).append("',\n")
                .append(" 'username' = '").append(config.getSourceUsername()).append("',\n")
                .append(" 'password' = '").append(config.getSourcePassword()).append("',\n")
                .append(" 'database-name' = '").append(config.getSinkTable().getSchema()).append("',\n")
                .append(" 'table-name' = '").append(config.getSourceTable().getName()).append("',\n")
                .append(" 'scan.startup.mode' = '").append(config.getStartupMode()).append("',\n")
                .append(")")
        ;
        sb.deleteCharAt(sb.length() - 3);
        return sb.toString();
    }

    // 生成目标表
    public static String genFlinkSinkDDL(FlinkCDCConfig config) {
        String url = config.getSinkUrl().substring(0, config.getSinkUrl().lastIndexOf("/")) + "/" + config.getSinkTable().getSchema();
        StringBuilder sb = new StringBuilder();
        sb
                .append("CREATE TABLE ")
                .append(config.getSinkTable().getName()).append(" (").append("\n")
        ;
        Table table = config.getSinkTable();
        for (Column column : table.getColumns()) {
            sb.append(column.getName()).append(" ").append(column.getFlinkType().getType()).append(",\n");
        }
        sb.append("primary key (id) not enforced\n");
        sb.append(") with ( \n")
                .append(" 'connector' = 'jdbc',\n")
                .append(" 'driver' = '").append(config.getSinkDriverClass()).append("',\n")
                .append(" 'url' = '").append(url).append("',\n")
                .append(" 'username' = '").append(config.getSinkUsername()).append("',\n")
                .append(" 'password' = '").append(config.getSourcePassword()).append("',\n")
                .append(" 'table-name' = '").append(config.getSinkTable().getName()).append("',\n")
                .append(")")
        ;
        sb.deleteCharAt(sb.length() - 3);
        return sb.toString();
    }

    // 生成插入表sql
    public static String genFlinkTransformDDL(FlinkCDCConfig config) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("insert into ")
                .append(config.getSinkTable().getName())
                .append(" select * from ")
                .append(config.getSourceTable().getName())
        ;
        return sb.toString();
    }

    public static void main(String[] args) {
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, NetUtils.getAvailablePort());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
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
}
