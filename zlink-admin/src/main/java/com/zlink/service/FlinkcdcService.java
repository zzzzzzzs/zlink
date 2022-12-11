package com.zlink.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zlink.cdc.FlinkCDCConfig;
import com.zlink.cdc.FlinkLocalInfo;
import com.zlink.cdc.mysql.MysqlCDCBuilder;
import com.zlink.common.model.Table;
import com.zlink.common.utils.JacksonObject;
import com.zlink.common.utils.NetUtils;
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

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    private Map<String, FlinkLocalInfo> flinkInfoMap = new LinkedHashMap<>();


    public boolean localFlinkCDC(JacksonObject json) throws ExecutionException, InterruptedException {
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

            // 生成数据源表
            for (int i = 0, size = srouceTables.size(); i < size; i++) {
                Table sourceTable = srouceTables.get(i);
                Table targetTable = targetTables.get(i);
                int port = NetUtils.getAvailablePort();
                StreamTableEnvironment tableEnv = MysqlCDCBuilder.create(port);
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
                tableEnv.executeSql(sourceDDL);
                tableEnv.executeSql(sinkDDL);
                TableResult transResult = tableEnv.executeSql(transformDDL);

                // 获取客户端信息
                Optional<JobClient> transClient = transResult.getJobClient();
                if (!transClient.isEmpty()) {
                    JobClient jobClient = transClient.get();
                    FlinkLocalInfo flinkLocalInfo = FlinkLocalInfo.builder()
                            .jobId(jobClient.getJobID().toHexString())
                            .url("localhost:" + port)
                            .jobClient(jobClient)
                            .status(jobClient.getJobStatus().get().name())
                            .build();
                    flinkInfoMap.put(jobClient.getJobID().toHexString(), flinkLocalInfo);
                }
            }
        } catch (NumberFormatException e) {
            log.error("{}", e);
            return false;
        }
        return true;
    }

    public List<FlinkLocalInfo> getLocalFlinkInfo() throws ExecutionException, InterruptedException {
        for (Map.Entry<String, FlinkLocalInfo> entry : flinkInfoMap.entrySet()) {
            FlinkLocalInfo value = entry.getValue();
            value.setStatus(value.getJobClient().getJobStatus().get().name());
        }
        return flinkInfoMap.values().stream().collect(Collectors.toList());
    }
}