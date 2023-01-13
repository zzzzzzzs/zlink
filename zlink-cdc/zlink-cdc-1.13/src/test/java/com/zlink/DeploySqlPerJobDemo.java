package com.zlink;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.zlink.common.assertion.Asserts;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.client.deployment.ClusterClientJobClientAdapter;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.*;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.operations.ddl.CreateTableOperation;
import org.apache.flink.table.planner.delegation.ExecutorBase;
import org.apache.flink.table.planner.utils.ExecutorUtils;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.flink.yarn.configuration.YarnLogConfigUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeploySqlPerJobDemo {
    public static void main(String[] args) throws Exception {

        // 读取 demo.sql
        String[] sqls = Resources.asCharSource(Resources.getResource("demo.sql"), Charsets.UTF_8).read().split(";");
        List<String> statements = List.of(sqls);

        String FLINK_HOME = System.getenv("FLINK_HOME");
        if (Asserts.isNullString(FLINK_HOME)) {
            throw new RuntimeException("请配置 FLINK_HOME 环境变量");
        }
        String FLINK_CONF_DIR = FLINK_HOME + "/conf";
        String FLINK_LIB_DIR = FLINK_HOME + "/lib";
        String FLINK_PLUGINS_DIR = FLINK_HOME + "/plugins";

        Configuration configuration = GlobalConfiguration.loadConfiguration(FLINK_CONF_DIR);
        // https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/deployment/config/
        configuration.set(DeploymentOptions.TARGET, YarnDeploymentTarget.PER_JOB.getName());
        configuration.set(DeploymentOptions.SHUTDOWN_IF_ATTACHED, true);
        configuration.set(DeploymentOptionsInternal.CONF_DIR, FLINK_CONF_DIR);
        configuration.set(CoreOptions.DEFAULT_PARALLELISM, 1);
        configuration.set(
                YarnConfigOptions.SHIP_FILES,
                Stream.of(Arrays.asList(FLINK_LIB_DIR, FLINK_PLUGINS_DIR))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));
        configuration.set(YarnConfigOptions.FLINK_DIST_JAR, FLINK_LIB_DIR + "/flink-dist_2.12-1.13.6.jar");
        configuration.setString("pipeline.max-parallelism", "1");
        configuration.setString("taskmanager.numberOfTaskSlots", "1");
        // https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/dev/table/config/
        configuration.setString("table.exec.resource.default-parallelism", "1");

        // 创建执行环境
        EnvironmentSettings settings = EnvironmentSettings.newInstance().inStreamingMode().build();
        // 使用 configuration 中设置的参数
        TableEnvironmentImpl tableEnv = TableEnvironmentImpl.create(configuration);

        CatalogManager catalogManager = tableEnv.getCatalogManager();
        // TODO 后期这里就不反射获取了，直接继承父类
        Class<? extends TableEnvironmentImpl> aClass = tableEnv.getClass();
        Field execEnvField = aClass.getDeclaredField("execEnv");
        execEnvField.setAccessible(true);
        Executor execEnv = (Executor) execEnvField.get(tableEnv);
        Field tableConfigField = aClass.getDeclaredField("tableConfig");
        tableConfigField.setAccessible(true);
        TableConfig tableConfig = (TableConfig) tableConfigField.get(tableEnv);

        // ==============================

        List<ModifyOperation> modifyOperations = new ArrayList();
        for (String statement : statements) {
            List<Operation> operations = tableEnv.getParser().parse(statement);
            if (operations.size() != 1) {
                throw new TableException("Only single statement is supported." + statement);
            } else {
                Operation operation = operations.get(0);
                if (operation instanceof ModifyOperation) {
                    modifyOperations.add((ModifyOperation) operation);
                } else if (operation instanceof CreateTableOperation) {
                    CreateTableOperation createTableOperation = (CreateTableOperation) operation;
                    if (createTableOperation.isTemporary()) {
                        catalogManager.createTemporaryTable(
                                createTableOperation.getCatalogTable(),
                                createTableOperation.getTableIdentifier(),
                                createTableOperation.isIgnoreIfExists());
                    } else {
                        catalogManager.createTable(
                                createTableOperation.getCatalogTable(),
                                createTableOperation.getTableIdentifier(),
                                createTableOperation.isIgnoreIfExists());
                    }
                } else if (operation instanceof QueryOperation) {
                    modifyOperations.add((ModifyOperation) operation);
                } else {
                    throw new TableException("Only insert statement is supported now." + statement);
                }
            }
        }

        List<Transformation<?>> trans = tableEnv.getPlanner().translate(modifyOperations);
        StreamGraph streamGraph;
        if (execEnv instanceof ExecutorBase) {
            streamGraph = ExecutorUtils.generateStreamGraph(((ExecutorBase) execEnv).getExecutionEnvironment(), trans);
        } else {
            throw new TableException("Unsupported SQL query! ExecEnv need a ExecutorBase.");
        }

        JobGraph jobGraph = streamGraph.getJobGraph();
        System.out.println("JobGraph is " + jobGraph);
        deployJobGraphInternal(jobGraph, DeploySqlPerJobDemo.class.getClassLoader(), configuration);
    }

    protected static void deployJobGraphInternal(JobGraph jobGraph, ClassLoader userCodeClassLoader, Configuration configuration) throws Exception {
        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();
//        YarnClusterDescriptor clusterDescriptor = clusterClientFactory.createClusterDescriptor(configuration);
        final String configurationDirectory = configuration.get(DeploymentOptionsInternal.CONF_DIR);
        YarnLogConfigUtil.setLogConfigFileInConfig(configuration, configurationDirectory);

        final YarnClient yarnClient = YarnClient.createYarnClient();

        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.addResource(HadoopUtils.getHadoopConfiguration(configuration)); // 这一步会加载基本的配置文件
        yarnConfiguration.addResource(new Path("/opt/module/hadoop-3.1.3/etc/hadoop/core-site.xml"));
        yarnConfiguration.addResource(new Path("/opt/module/hadoop-3.1.3/etc/hadoop/hdfs-site.xml"));
        yarnConfiguration.addResource(new Path("/opt/module/hadoop-3.1.3/etc/hadoop/yarn-site.xml"));

        yarnClient.init(yarnConfiguration);
        yarnClient.start();

        YarnClusterDescriptor clusterDescriptor = new YarnClusterDescriptor(
                configuration,
                yarnConfiguration,
                yarnClient,
                YarnClientYarnClusterInformationRetriever.create(yarnClient),
                false);



        ClusterSpecification clusterSpecification = clusterClientFactory.getClusterSpecification(configuration);
        final ClusterClientProvider<ApplicationId> clusterClientProvider = clusterDescriptor.deployJobCluster(clusterSpecification, jobGraph, true);
        final CompletableFuture<ClusterClientJobClientAdapter<ApplicationId>>
                jobClientAdapterCompletableFuture =
                CompletableFuture.completedFuture(
                        new ClusterClientJobClientAdapter<>(
                                clusterClientProvider, jobGraph.getJobID(), userCodeClassLoader));

        final ClusterClientJobClientAdapter<ApplicationId> jobClientProvider =
                jobClientAdapterCompletableFuture.get();

        // 获取 application id
        System.out.println(clusterClientProvider.getClusterClient().getClusterId());

        CompletableFuture<JobExecutionResult> jobExecutionResult = jobClientProvider.getJobExecutionResult();
    }
}
