package com.zlink.service;

import cn.hutool.core.net.NetUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlink.cdc.CDCRun;
import com.zlink.cdc.FlinkCDCConfig;
import com.zlink.cdc.FlinkInfo;
import com.zlink.cdc.mysql.MysqlCDCBuilder;
import com.zlink.common.model.Table;
import com.zlink.common.utils.NetUtils;
import com.zlink.dao.DatasourceMapper;
import com.zlink.deploy.DeployJobParam;
import com.zlink.deploy.YarnPerjobDeployer;
import com.zlink.entity.JobFlinkConf;
import com.zlink.entity.JobJdbcDatasource;
import com.zlink.metadata.driver.Driver;
import com.zlink.model.req.FlinkGenInfoReq;
import com.zlink.model.req.PushTaskInfoReq;
import lombok.RequiredArgsConstructor;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author zs
 * @since 2022-12-09
 */
@Service
@RequiredArgsConstructor
public class FlinkCDCService extends ServiceImpl<DatasourceMapper, JobJdbcDatasource> {

    private static Logger logger = LoggerFactory.getLogger(FlinkCDCService.class);

    private final DatasourceService datasourceService;
    private final FlinkConfService flinkConfService;

    // jobId, TableResult
    private Map<String, FlinkInfo> flinkInfoMap = new LinkedHashMap<>();


    public boolean localFlinkCDC(FlinkGenInfoReq req) throws ExecutionException, InterruptedException {
        try {
            String sourceId = req.getSourceId();
            String targetId = req.getTargetId();

            JobJdbcDatasource source = datasourceService.getById(sourceId);
            JobJdbcDatasource target = datasourceService.getById(targetId);

            // 获取源表信息
            Driver sourceDriver = Driver.build(source.getDriverConfig());
            List<Table> srouceTables = req.getSourceTables();
            srouceTables.forEach(ele -> ele.setColumns(sourceDriver.listColumns(ele.getSchema(), ele.getName())));

            // 获取目标表信息
            Driver targetDriver = Driver.build(target.getDriverConfig());
            List<Table> targetTables = req.getTargetTables();
            targetTables.forEach(ele -> ele.setColumns(targetDriver.listColumns(ele.getSchema(), ele.getName())));

            String[] url = source.getJdbcUrl().split(":");
            String sourceIp = source.getJdbcUrl().split(":")[url.length - 2].split("//")[1];
            int sourcePort = Integer.parseInt(source.getJdbcUrl().split(":")[url.length - 1].split("/")[0]);

            // 生成数据源表
            for (int i = 0, size = srouceTables.size(); i < size; i++) {
                Table sourceTable = srouceTables.get(i);
                Table targetTable = targetTables.get(i);
                int port = NetUtil.getUsableLocalPort(50000, 65535);
                FlinkCDCConfig config = FlinkCDCConfig.builder()
                        // source
                        .sourceDataBaseType(source.getDatabaseType())
                        .sourceHostname(sourceIp)
                        .sourcePort(sourcePort)
                        .sourceUsername(source.getUserName())
                        .sourcePassword(source.getPassword())
                        .sourceTable(sourceTable)
                        // sink
                        .sinkDataBaseType(target.getDatabaseType())
                        .sinkDriverClass(target.getJdbcDriverClass())
                        .sinkUrl(target.getJdbcUrl())
                        .sinkUsername(target.getUserName())
                        .sinkPassWord(target.getPassword())
                        .sinkTable(targetTable)
                        // flink conf
                        .startupMode("initial")
                        .parallelism(req.getParallelism())
                        .localPort(port)
                        .remote(false)
                        .build();
                TableResult transResult = CDCRun.perTask(config);
                // 获取客户端信息
                Optional<JobClient> transClient = transResult.getJobClient();
                if (!transClient.isEmpty()) {
                    JobClient jobClient = transClient.get();
                    FlinkInfo flinkLocalInfo = FlinkInfo.builder()
                            .jobId(jobClient.getJobID().toHexString())
                            .model("local")
                            .url("localhost:" + port)
                            .jobClient(jobClient)
                            .config(config)
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

    public List<FlinkInfo> getLocalFlinkInfo() {
        for (Map.Entry<String, FlinkInfo> entry : flinkInfoMap.entrySet()) {
            FlinkInfo value = entry.getValue();
            try {
                value.setStatus(value.getJobClient().getJobStatus().get().name());
            } catch (ExecutionException | InterruptedException | IllegalStateException e) {
                flinkInfoMap.remove(value.getJobId());
                logger.error("{} 意外停止，信息为：{}", value.getJobId(), e);
            }
        }
        return flinkInfoMap.values().stream().collect(Collectors.toList());
    }

    public boolean stopFlinkTask(List<FlinkInfo> infos) {
        for (FlinkInfo info : infos) {
            JobClient client = flinkInfoMap.get(info.getJobId()).getJobClient();
            client.cancel();
            flinkInfoMap.remove(info.getJobId());
        }
        return true;
    }

    public boolean pushTask(PushTaskInfoReq req) throws Exception {
        JobFlinkConf flinkConf = flinkConfService.getById(req.getClusterId());
        for (String jobId : req.getJobIds()) {
            FlinkCDCConfig config = flinkInfoMap.get(jobId).getConfig();
            config
                    .setRemote(true)
                    .setRemoteIp(flinkConf.getIp())
                    .setRemotePort(flinkConf.getPort())
                    .setParallelism(req.getParallelism())
            ;
            // 推送任务到集群上
            DeployJobParam param = new DeployJobParam();
            param.setFLINK_HOME(flinkConf.getFlinkHome());
            param.setHADOOP_CORE_SITE_DIR(flinkConf.getCoreSite());
            param.setHADOOP_HDFS_SITE_DIR(flinkConf.getHdfsSite());
            param.setHADOOP_YARN_SITE_DIR(flinkConf.getYarnSite());
            param.setSqls(CDCRun.getCDCSqls(config));
            YarnPerjobDeployer.deploySql(param);
            // 停止本地任务
            JobClient client = flinkInfoMap.get(jobId).getJobClient();
            client.cancel();
            flinkInfoMap.remove(jobId);
        }
        return true;
    }
}
