package com.zlink.cdc.mysql;

import com.zlink.cdc.FlinkCDCConfig;
import com.zlink.common.model.Column;
import com.zlink.common.model.Table;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zs
 * @date 2022/12/8
 */
public class MysqlCDCBuilder {

    private static Logger logger = LoggerFactory.getLogger(MysqlCDCBuilder.class);


    /**
     * @return
     * @author: zs
     * @Description
     * @Date 2022/12/16
     * @Param remote
     * @param: port
     **/
    public static StreamTableEnvironment create(FlinkCDCConfig config) {
        StreamExecutionEnvironment env;
        if (config.getRemote()) {
            env = StreamExecutionEnvironment.createRemoteEnvironment(config.getRemoteIp(), config.getRemotePort());
            env.setParallelism(config.getParallelism());
        } else {
            Configuration conf = new Configuration();
            conf.setInteger(RestOptions.PORT, config.getLocalPort());
            env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
            env.setParallelism(1);
        }
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
                .append(config.getSourceTable().getName()).append("_source").append(" (").append("\n")
        ;
        Table table = config.getSourceTable();
        for (Column column : table.getColumns()) {
            sb.append("`").append(column.getName()).append("`").append(" ").append(column.getFlinkType().getType()).append(",\n");
        }
        sb.append("primary key (id) not enforced\n");
        sb.append(") with ( \n")
                .append(" 'connector' = 'mysql-cdc',\n")
                .append(" 'hostname' = '").append(config.getSourceHostname()).append("',\n")
                .append(" 'port' = '").append(config.getSourcePort()).append("',\n")
                .append(" 'username' = '").append(config.getSourceUsername()).append("',\n")
                .append(" 'password' = '").append(config.getSourcePassword()).append("',\n")
                .append(" 'database-name' = '").append(config.getSourceTable().getSchema()).append("',\n")
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
                .append(config.getSinkTable().getName()).append("_sink").append(" (").append("\n")
        ;
        Table table = config.getSinkTable();
        for (Column column : table.getColumns()) {
            sb.append("`").append(column.getName()).append("`").append(" ").append(column.getFlinkType().getType()).append(",\n");
        }
        sb.append("primary key (id) not enforced\n");
        sb.append(") with ( \n")
                .append(" 'connector' = 'jdbc',\n")
                .append(" 'driver' = '").append(config.getSinkDriverClass()).append("',\n")
                .append(" 'url' = '").append(url).append("',\n")
                .append(" 'username' = '").append(config.getSinkUsername()).append("',\n")
                .append(" 'password' = '").append(config.getSinkPassWord()).append("',\n")
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
                .append(config.getSinkTable().getName()).append("_sink")
                .append(" select * from ")
                .append(config.getSourceTable().getName()).append("_source")
        ;
        return sb.toString();
    }

    public static TableResult perTask(FlinkCDCConfig config) {
        StreamTableEnvironment tableEnv = create(config);
        String sourceDDL = genFlinkSourceDDL(config);
        String sinkDDL = genFlinkSinkDDL(config);
        String transformDDL = genFlinkTransformDDL(config);
        logger.info("sourceDDL : {}", sourceDDL);
        logger.info("sinkDDL : {}", sinkDDL);
        logger.info("transformDDL : {}", transformDDL);
        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(sinkDDL);
        return tableEnv.executeSql(transformDDL);
    }

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1); // TODO why only set 1
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
//        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
//        TableEnvironmentImpl tableEnv = StreamTableEnvironmentImpl.create(settings);
//        tableEnv.getConfig().setSqlDialect(SqlDialect.DEFAULT);

        TableConfig tableConfig = new TableConfig();
        tableConfig.setSqlDialect(SqlDialect.DEFAULT);
        StreamTableEnvironment tableEnv = StreamTableEnvironmentImpl.create(env, settings, tableConfig);

//        EnvironmentSettings settings = EnvironmentSettings.newInstance()
//                .useBlinkPlanner()
//                .inBatchMode()
//                .build();
//        TableEnvironmentImpl tableEnv = TableEnvironmentImpl.create(settings);

//        EnvironmentSettings envSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
//        TableEnvironment tableEnv = TableEnvironment.create(envSettings);


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
        String transformDmlSQL = "insert into user_info_sink select * from user_info_source";


        tableEnv.executeSql(sourceDDL).print();
        tableEnv.executeSql(sinkDDL).print();
        tableEnv.executeSql(transformDmlSQL).print();
    }
}
