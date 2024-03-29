package com.zlink;

import com.zlink.common.assertion.Asserts;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.client.deployment.ClusterClientJobClientAdapter;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.DeploymentOptionsInternal;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.util.concurrent.CompletableFuture;

/**
 * @author zs
 * @date 2022/12/10
 */
public class CDCTest1 {
    public static void main(String[] args) throws Exception {
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        Configuration configuration = new Configuration();
        configuration.setString("execution.checkpointing.interval", "3s");
        configuration.setInteger("parallelism.default", 1);
        TableEnvironmentImpl tableEnv = TableEnvironmentImpl.create(settings);
        tableEnv.getConfig().setSqlDialect(SqlDialect.DEFAULT);

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

        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(sinkDDL);
        tableEnv.executeSql(transformDmlSQL);
    }

}
