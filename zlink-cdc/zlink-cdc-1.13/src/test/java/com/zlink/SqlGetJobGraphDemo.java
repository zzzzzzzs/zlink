package com.zlink;

import com.zlink.common.assertion.Asserts;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.client.deployment.ClusterClientJobClientAdapter;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.executors.PipelineExecutorUtils;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.DeploymentOptionsInternal;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.catalog.UnresolvedIdentifier;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.operations.CollectModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.operations.ddl.CreateTableOperation;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SqlGetJobGraphDemo {

    static String sql1 = "CREATE TABLE datagen_dijie3 (\n" +
            " f_sequence INT,\n" +
            " f_random INT,\n" +
            " f_random_str STRING\n" +
            ") WITH (\n" +
            " 'connector' = 'datagen',\n" +
            " 'rows-per-second'='5',\n" +
            " 'fields.f_sequence.kind'='sequence',\n" +
            " 'fields.f_sequence.start'='1',\n" +
            " 'fields.f_sequence.end'='1000',\n" +
            " 'fields.f_random.min'='1',\n" +
            " 'fields.f_random.max'='1000',\n" +
            " 'fields.f_random_str.length'='10'\n" +
            ")";
    static String sql2 = "select * from datagen_dijie3";
    static Configuration configuration;

    public static void main(String[] args) throws Exception {


        String FLINK_HOME = System.getenv("FLINK_HOME");
        if (Asserts.isNullString(FLINK_HOME)) {
            throw new RuntimeException("请配置 FLINK_HOME 环境变量");
        }
        String FLINK_CONF_DIR = FLINK_HOME + "/conf";
        String FLINK_LIB_DIR = FLINK_HOME + "/lib";
        String FLINK_PLUGINS_DIR = FLINK_HOME + "/plugins";

        configuration = GlobalConfiguration.loadConfiguration(FLINK_CONF_DIR);
        configuration.set(DeploymentOptions.TARGET, YarnDeploymentTarget.PER_JOB.getName());
        configuration.set(DeploymentOptions.SHUTDOWN_IF_ATTACHED, true);
        configuration.set(DeploymentOptionsInternal.CONF_DIR, FLINK_CONF_DIR);
//        configuration.set(
//                YarnConfigOptions.SHIP_FILES,
//                Stream.of(Arrays.asList(FLINK_LIB_DIR, FLINK_PLUGINS_DIR))
//                        .flatMap(Collection::stream)
//                        .collect(Collectors.toList()));
        configuration.set(YarnConfigOptions.FLINK_DIST_JAR, FLINK_LIB_DIR + "/flink-dist_2.12-1.13.6.jar");


        // 创建执行环境
        EnvironmentSettings settings = EnvironmentSettings.newInstance().inStreamingMode().build();
        TableEnvironmentImpl tableEnv = TableEnvironmentImpl.create(settings);

//        tableEnv.sqlUpdate(sql1);
//        Table table = tableEnv.sqlQuery(sql2);
//        table.execute().print();


        Class<? extends TableEnvironmentImpl> aClass = tableEnv.getClass();
        Field execEnvField = aClass.getDeclaredField("execEnv");
        execEnvField.setAccessible(true);
        Executor execEnv = (Executor) execEnvField.get(tableEnv);

        Field tableConfigField = aClass.getDeclaredField("tableConfig");
        tableConfigField.setAccessible(true);
        TableConfig tableConfig = (TableConfig) tableConfigField.get(tableEnv);

        // ==============================
        List<Operation> operations = tableEnv.getParser().parse(sql1);
        List<Operation> operations2 = tableEnv.getParser().parse(sql2);

        final UnresolvedIdentifier unresolvedIdentifier =
                UnresolvedIdentifier.of(
                        "Unregistered_Collect_Sink_" + CollectModifyOperation.getUniqueId());
        CatalogManager catalogManager = tableEnv.getCatalogManager();
        final ObjectIdentifier objectIdentifier =
                catalogManager.qualifyIdentifier(unresolvedIdentifier);


        CollectModifyOperation sinkOperation2 =
                new CollectModifyOperation(objectIdentifier, (QueryOperation) operations2.get(0));

//        List<Transformation<?>> transformations = tableEnv.getPlanner().translate(Collections.singletonList(sinkOperation));
//        List<Transformation<?>> transformations2 = tableEnv.getPlanner().translate(Collections.singletonList(sinkOperation2));

//        Pipeline pipeline = execEnv.createPipeline(transformations, tableConfig, "collect");
//        Pipeline pipeline2 = execEnv.createPipeline(transformations2, tableConfig, "collect2");

//        JobGraph jobGraph = PipelineExecutorUtils.getJobGraph(pipeline, settings.toConfiguration());
//        JobGraph jobGraph2 = PipelineExecutorUtils.getJobGraph(pipeline2, settings.toConfiguration());
//        System.out.println("hhhhhhhhhhhhhh: " + jobGraph);
//        System.out.println("hhhhhhhhhhhhhh: " + jobGraph2);

//        deployJobGraphInternal(jobGraph, SqlGetJobGraphDemo.class.getClassLoader());
    }


    protected static void deployJobGraphInternal(JobGraph jobGraph, ClassLoader userCodeClassLoader) throws Exception {
        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();

        YarnClusterDescriptor clusterDescriptor = clusterClientFactory.createClusterDescriptor(configuration);
        ClusterSpecification clusterSpecification = clusterClientFactory.getClusterSpecification(configuration);

        final ClusterClientProvider<ApplicationId> clusterClientProvider = clusterDescriptor.deployJobCluster(clusterSpecification, jobGraph, false);


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
