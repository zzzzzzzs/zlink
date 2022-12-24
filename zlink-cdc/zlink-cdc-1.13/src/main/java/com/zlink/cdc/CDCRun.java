package com.zlink.cdc;

import com.zlink.cdc.mysql.MysqlCDCBuilder;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zs
 * @date 2022/12/11
 * flink local info
 */
public class CDCRun {

    private static Logger logger = LoggerFactory.getLogger(CDCRun.class);

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
        String sourceDDL = null;
        String sinkDDL = null;
        switch (config.getSourceDataBaseType()) {
            case "mysql":
                sourceDDL = MysqlCDCBuilder.genFlinkSourceDDL(config);
                break;
            default:
                break;
        }
        switch (config.getSinkDataBaseType()) {
            case "mysql":
                sinkDDL = MysqlCDCBuilder.genFlinkSinkDDL(config);
                break;
            default:
                break;
        }
        String transformDDL = genFlinkTransformDDL(config);
        logger.info("sourceDDL : {}", sourceDDL);
        logger.info("sinkDDL : {}", sinkDDL);
        logger.info("transformDDL : {}", transformDDL);
        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(sinkDDL);
        return tableEnv.executeSql(transformDDL);
    }

    public static void main(String[] args) {
    }
}
