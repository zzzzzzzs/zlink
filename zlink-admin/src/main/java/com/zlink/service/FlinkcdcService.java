package com.zlink.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zlink.cdc.FlinkCDCConfig;
import com.zlink.cdc.mysql.MysqlCDCBuilder;
import com.zlink.common.model.Table;
import com.zlink.common.utils.JacksonObject;
import com.zlink.dao.DatasourceMapper;
import com.zlink.entity.JobJdbcDatasource;
import com.zlink.metadata.driver.Driver;
import lombok.RequiredArgsConstructor;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zs
 * @since 2022-12-09
 */
@Service
@RequiredArgsConstructor
public class FlinkcdcService extends ServiceImpl<DatasourceMapper, JobJdbcDatasource> {

    private static Logger logger = LoggerFactory.getLogger(FlinkcdcService.class);

    private final DatasourceService datasourceService;

    // jobId, TableResult
    private Map<String, JobClient> flinkInfoMap = new HashMap<>();


    public boolean localFlinkCDC(JacksonObject json) {
        try {
            String sourceId = json.getJacksonObject("source").getString("id");
            String targetId = json.getJacksonObject("target").getString("id");

            JobJdbcDatasource source = datasourceService.getById(sourceId);
            JobJdbcDatasource target = datasourceService.getById(targetId);

            // 获取源表信息
            Driver sourceDriver = Driver.build(source.getDriverConfig());
            List<Table> srouceTables = json.getJacksonObject("source").getObject("sourceArr", new TypeReference<List<Table>>() {
            });
            srouceTables.forEach(ele -> {
                ele.setColumns(sourceDriver.listColumns(ele.getSchema(), ele.getName()));
            });

            // 获取目标表信息
            Driver targetDriver = Driver.build(target.getDriverConfig());
            List<Table> targetTables = json.getJacksonObject("target").getObject("targetArr", new TypeReference<List<Table>>() {
            });
            targetTables.forEach(ele -> {
                ele.setColumns(targetDriver.listColumns(ele.getSchema(), ele.getName()));
            });

            String[] url = source.getJdbcUrl().split(":");
            String sourceIp = source.getJdbcUrl().split(":")[url.length - 2].split("//")[1];
            int sourcePort = Integer.parseInt(source.getJdbcUrl().split(":")[url.length - 1].split("/")[0]);


            StreamTableEnvironment tableEnv = MysqlCDCBuilder.create();
            // 生成数据源表
            for (int i = 0, size = srouceTables.size(); i < size; i++) {
                Table sourceTable = srouceTables.get(i);
                Table targetTable = targetTables.get(i);
                FlinkCDCConfig config = FlinkCDCConfig.builder()
                        .startupMode("initial")
                        .parallelism(1)
                        .sourceHostname(sourceIp)
                        .sourcePort(sourcePort)
                        .sourceUsername(source.getUserName())
                        .sourcePassword(source.getPassword())
                        .sourceTable(sourceTable)
                        .sinkDriverClass(target.getJdbcDriverClass())
                        .sinkUrl(target.getJdbcUrl())
                        .sinkUsername(target.getUserName())
                        .sinkPassWord(target.getPassword())
                        .sinkTable(targetTable)
                        .build();
                String sourceDDL = MysqlCDCBuilder.genFlinkSourceDDL(config);
                String sinkDDL = MysqlCDCBuilder.genFlinkSinkDDL(config);
                String transformDDL = MysqlCDCBuilder.genFlinkTransformDDL(config);
                logger.info("sourceDDL : {}", sourceDDL);
                logger.info("sinkDDL : {}", sinkDDL);
                logger.info("transformDDL : {}", transformDDL);
                TableResult sourceResult = tableEnv.executeSql(sourceDDL);
                TableResult sinkResult = tableEnv.executeSql(sinkDDL);
                TableResult transResult = tableEnv.executeSql(transformDDL);

                // 获取客户端信息
                Optional<JobClient> sourceClient = sourceResult.getJobClient();
                Optional<JobClient> sinkClient = sinkResult.getJobClient();
                Optional<JobClient> transClient = transResult.getJobClient();
                if (!sourceClient.isEmpty()) {
                    JobClient jobClient = sourceClient.get();
                    flinkInfoMap.put(jobClient.getJobID().toHexString(), jobClient);
                }
                if (!sinkClient.isEmpty()) {
                    JobClient jobClient = sinkClient.get();
                    flinkInfoMap.put(jobClient.getJobID().toHexString(), jobClient);
                }
                if (!transClient.isEmpty()) {
                    JobClient jobClient = transClient.get();
                    flinkInfoMap.put(jobClient.getJobID().toHexString(), jobClient);
                }
            }
        } catch (NumberFormatException e) {
            log.error("{}", e);
            return false;
        }
        return true;
    }

    public Map<String, JobClient> getLocalFlinkInfo() {
        return flinkInfoMap;
    }
}
