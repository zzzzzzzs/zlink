package com.zlink.deploy;

import org.apache.flink.configuration.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YarnPerjobDeployer {
    public static Logger logger = LoggerFactory.getLogger(YarnPerjobDeployer.class);

    public static void  deploySql(DeployJobParam param) throws Exception {
        Configuration configuration = GlobalConfiguration.loadConfiguration(param.getFLINK_CONF_DIR());
        // https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/deployment/config/
        configuration.set(DeploymentOptions.TARGET, YarnDeploymentTarget.PER_JOB.getName());
        configuration.set(DeploymentOptions.SHUTDOWN_IF_ATTACHED, true);
        configuration.set(DeploymentOptionsInternal.CONF_DIR, param.getFLINK_CONF_DIR());
        configuration.set(CoreOptions.DEFAULT_PARALLELISM, 1);
        configuration.set(
                YarnConfigOptions.SHIP_FILES,
                Stream.of(Arrays.asList(param.getFLINK_LIB_DIR(), param.getFLINK_LIB_DIR()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));
        configuration.set(YarnConfigOptions.FLINK_DIST_JAR, param.getFLINK_DIST_JAR());
        configuration.setString("pipeline.max-parallelism", "1");
        configuration.setString("taskmanager.numberOfTaskSlots", "1");
        // https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/dev/table/config/
        configuration.setString("table.exec.resource.default-parallelism", "1");
        configuration.addAllToProperties(param.getProps());

        TableConfig tableConfig = new TableConfig();
        tableConfig.addConfiguration(configuration);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        CustomTableEnvironmentImpl tableEnv = CustomTableEnvironmentImpl.create(env, settings, tableConfig);

        tableEnv.deploySqlByYarnPerJob(param);
    }

}
