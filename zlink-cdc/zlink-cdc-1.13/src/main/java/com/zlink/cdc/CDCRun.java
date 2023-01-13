package com.zlink.cdc;

import com.zlink.cdc.mysql.MysqlCDCBuilder;
import com.zlink.cdc.postgresql.PostgresqlCDCBuilder;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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


    public static List<String> getCDCSqls(FlinkCDCConfig config) {
        String sourceDDL;
        String sinkDDL;
        // source
        switch (config.getSourceDataBaseType()) {
            case "mysql":
                sourceDDL = MysqlCDCBuilder.genFlinkSourceDDL(config);
                break;
            case "postgresql":
                sourceDDL = PostgresqlCDCBuilder.genFlinkSourceDDL(config);
                break;
            default:
                throw new RuntimeException("flink sink 不支持 " + config.getSinkDataBaseType());
        }
        // sink
        switch (config.getSinkDataBaseType()) {
            case "mysql":
                sinkDDL = MysqlCDCBuilder.genFlinkSinkDDL(config);
                break;
            case "postgresql":
                sinkDDL = PostgresqlCDCBuilder.genFlinkSinkDDL(config);
                break;
            default:
                throw new RuntimeException("flink sink 不支持 " + config.getSinkDataBaseType());
        }
        String transformDDL = genFlinkTransformDDL(config);
        logger.info("sourceDDL : {}", sourceDDL);
        logger.info("sinkDDL : {}", sinkDDL);
        logger.info("transformDDL : {}", transformDDL);
        return List.of(sourceDDL, sinkDDL, transformDDL);
    }

    public static TableResult perTask(FlinkCDCConfig config) {
        StreamTableEnvironment tableEnv = create(config);
        List<String> cdcSqls = getCDCSqls(config);
        if (cdcSqls.size() != 3) {
            throw new RuntimeException("please check generate sql num");
        }
        tableEnv.executeSql(cdcSqls.get(0));
        tableEnv.executeSql(cdcSqls.get(1));
        return tableEnv.executeSql(cdcSqls.get(2));
    }

    public static void main(String[] args) {
    }
}
