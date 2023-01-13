package com.zlink.deploy;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.client.deployment.ClusterClientJobClientAdapter;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptionsInternal;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.catalog.GenericInMemoryCatalog;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.ExecutorFactory;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.delegation.PlannerFactory;
import org.apache.flink.table.factories.ComponentFactoryService;
import org.apache.flink.table.module.ModuleManager;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.operations.ddl.CreateTableOperation;
import org.apache.flink.table.planner.delegation.ExecutorBase;
import org.apache.flink.table.planner.utils.ExecutorUtils;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnLogConfigUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * 参考 StreamTableEnvironmentImpl
 */

public class CustomTableEnvironmentImpl extends TableEnvironmentImpl {

    private final StreamExecutionEnvironment executionEnvironment;
    private final ClassLoader classLoader;

    public CustomTableEnvironmentImpl(
            CatalogManager catalogManager,
            ModuleManager moduleManager,
            FunctionCatalog functionCatalog,
            TableConfig tableConfig,
            StreamExecutionEnvironment executionEnvironment,
            Planner planner,
            Executor executor,
            boolean isStreamingMode,
            ClassLoader userClassLoader) {
        super(
                catalogManager,
                moduleManager,
                tableConfig,
                executor,
                functionCatalog,
                planner,
                isStreamingMode,
                userClassLoader);
        this.executionEnvironment = executionEnvironment;
        this.classLoader = userClassLoader;
    }

    public static CustomTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment,
            EnvironmentSettings settings,
            TableConfig tableConfig) {

        if (!settings.isStreamingMode()) {
            throw new TableException(
                    "StreamTableEnvironment can not run in batch mode for now, please use TableEnvironment.");
        }

        // temporary solution until FLINK-15635 is fixed
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        ModuleManager moduleManager = new ModuleManager();

        CatalogManager catalogManager =
                CatalogManager.newBuilder()
                        .classLoader(classLoader)
                        .config(tableConfig.getConfiguration())
                        .defaultCatalog(
                                settings.getBuiltInCatalogName(),
                                new GenericInMemoryCatalog(
                                        settings.getBuiltInCatalogName(),
                                        settings.getBuiltInDatabaseName()))
                        .executionConfig(executionEnvironment.getConfig())
                        .build();

        FunctionCatalog functionCatalog =
                new FunctionCatalog(tableConfig, catalogManager, moduleManager);

        Map<String, String> executorProperties = settings.toExecutorProperties();
        Executor executor = lookupExecutor(executorProperties, executionEnvironment);

        Map<String, String> plannerProperties = settings.toPlannerProperties();
        Planner planner =
                ComponentFactoryService.find(PlannerFactory.class, plannerProperties)
                        .create(
                                plannerProperties,
                                executor,
                                tableConfig,
                                functionCatalog,
                                catalogManager);

        return new CustomTableEnvironmentImpl(
                catalogManager,
                moduleManager,
                functionCatalog,
                tableConfig,
                executionEnvironment,
                planner,
                executor,
                settings.isStreamingMode(),
                classLoader);
    }

    private static Executor lookupExecutor(
            Map<String, String> executorProperties,
            StreamExecutionEnvironment executionEnvironment) {
        try {
            ExecutorFactory executorFactory =
                    ComponentFactoryService.find(ExecutorFactory.class, executorProperties);
            Method createMethod =
                    executorFactory
                            .getClass()
                            .getMethod("create", Map.class, StreamExecutionEnvironment.class);

            return (Executor)
                    createMethod.invoke(executorFactory, executorProperties, executionEnvironment);
        } catch (Exception e) {
            throw new TableException(
                    "Could not instantiate the executor. Make sure a planner module is on the classpath",
                    e);
        }
    }

    public void deploySqlByYarnPerJob(DeployJobParam param) throws Exception {
        JobGraph jobGraph = getJobGraph(param.getSqls());
        deployJobGraphInternal(jobGraph, param);
    }


    JobGraph getJobGraph(List<String> sqls) throws NoSuchFieldException, IllegalAccessException {
        // 创建执行环境
        // 使用 configuration 中设置的参数
        CatalogManager catalogManager = this.getCatalogManager();

        List<ModifyOperation> modifyOperations = new ArrayList();
        for (String sql : sqls) {
            List<Operation> operations = this.getParser().parse(sql);
            if (operations.size() != 1) {
                throw new TableException("Only single statement is supported." + sql);
            } else {
                Operation operation = operations.get(0);
                if (operation instanceof ModifyOperation) { // insert
                    modifyOperations.add((ModifyOperation) operation);
                } else if (operation instanceof CreateTableOperation) { // 创建表
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
                    throw new UnsupportedOperationException("目前不支持 select 语法");
                } else {
                    throw new TableException("Only insert statement is supported now." + sql);
                }
            }
        }

        List<Transformation<?>> trans = this.getPlanner().translate(modifyOperations);
        StreamGraph streamGraph;
        if (execEnv instanceof ExecutorBase) { // 继承的父类，并且是 protected 修饰的
            streamGraph = ExecutorUtils.generateStreamGraph(((ExecutorBase) execEnv).getExecutionEnvironment(), trans);
        } else {
            throw new TableException("Unsupported SQL query! ExecEnv need a ExecutorBase.");
        }

        return streamGraph.getJobGraph();
    }

    private String deployJobGraphInternal(JobGraph jobGraph, DeployJobParam param) throws Exception {
        Configuration configuration = tableConfig.getConfiguration();
        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();
        final String configurationDirectory = configuration.get(DeploymentOptionsInternal.CONF_DIR);
        YarnLogConfigUtil.setLogConfigFileInConfig(configuration, configurationDirectory);

        final YarnClient yarnClient = YarnClient.createYarnClient();
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.addResource(HadoopUtils.getHadoopConfiguration(configuration)); // 这一步会加载基本的配置文件
        yarnConfiguration.addResource(new Path(param.getHADOOP_CORE_SITE_DIR()));
        yarnConfiguration.addResource(new Path(param.getHADOOP_HDFS_SITE_DIR()));
        yarnConfiguration.addResource(new Path(param.getHADOOP_YARN_SITE_DIR()));

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
                                clusterClientProvider, jobGraph.getJobID(), this.classLoader));

        final ClusterClientJobClientAdapter<ApplicationId> jobClientProvider =
                jobClientAdapterCompletableFuture.get();

        // 获取 application id
        CompletableFuture<JobExecutionResult> jobExecutionResult = jobClientProvider.getJobExecutionResult();
        return clusterClientProvider.getClusterClient().getClusterId().toString();
    }
}
