package com.zlink.deploy;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.flink.configuration.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 使用自定义 CustomTableEnvironmentImpl cdc
public class CustomEnvCDCSqlDeployPerJobDemo {
    public static void main(String[] args) throws Exception {
        String[] sqls = Resources.asCharSource(Resources.getResource("demo-cdc.sql"), Charsets.UTF_8).read().split(";");

        DeployJobParam param = new DeployJobParam();
        param.setSqls(List.of(sqls));
        param.setFLINK_HOME("/opt/module/flink-1.13.6");
        param.setHADOOP_CORE_SITE_DIR("/opt/module/hadoop-3.1.3/etc/hadoop/core-site.xml");
        param.setHADOOP_HDFS_SITE_DIR("/opt/module/hadoop-3.1.3/etc/hadoop/hdfs-site.xml");
        param.setHADOOP_YARN_SITE_DIR("/opt/module/hadoop-3.1.3/etc/hadoop/yarn-site.xml");
        YarnPerjobDeployer.deploySql(param);
    }
}
