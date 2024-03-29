package com.zlink.cdc;

import com.zlink.common.assertion.Asserts;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.client.deployment.ClusterClientJobClientAdapter;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.DeploymentOptionsInternal;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CompletableFuture;
public class DeployJarPerJob {
    private static Logger logger = LoggerFactory.getLogger(DeployJarPerJob.class);
    static Configuration configuration;

    public static void main(String[] args) throws Exception {


//        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
//        conf.addResource(DeployJarPerJob.class.getResource("/opt/module/hadoop-3.1.3/etc/hadoop/core-site.xml"));
//        conf.addResource(DeployJarPerJob.class.getResource("/opt/module/hadoop-3.1.3/etc/hadoop/hdfs-site.xml"));
//        conf.addResource(DeployJarPerJob.class.getResource("/opt/module/hadoop-3.1.3/etc/hadoop/yarn-site.xml"));


        System.out.println(System.getenv("HADOOP_HOME"));
//        System.out.println(System.getenv("HADOOP_CONF_DIR"));
        System.out.println(System.getenv("HADOOP_CLASSPATH"));
        String FLINK_HOME = System.getenv("FLINK_HOME");
        System.out.println("FLINK_HOME is " + FLINK_HOME);
        if (Asserts.isNullString(FLINK_HOME)) {
            throw new RuntimeException("请配置 FLINK_HOME 环境变量");
        }
        String FLINK_CONF_DIR = FLINK_HOME + "/conf";
        String FLINK_LIB_DIR = FLINK_HOME + "/lib";
        String FLINK_PLUGINS_DIR = FLINK_HOME + "/plugins";
        String HADOOP_CONF_DIR = "/opt/module/hadoop-3.1.3/etc/hadoop";

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

        PackagedProgram packagedProgram = PackagedProgram.newBuilder()
//                .setEntryPointClassName("你的flink程序文件主函数入口")
                //"你的flink程序文件"
                .setJarFile(new File("/opt/module/flink-1.13.6/examples/streaming/SocketWindowWordCount.jar"))
                //"savepoint的信息"
                .setSavepointRestoreSettings(SavepointRestoreSettings.none())
                .setArguments(args)
                .build();

        JobGraph jobGraph = PackagedProgramUtils.createJobGraph(packagedProgram, configuration, 1, true);
        System.out.println(jobGraph);
        deployJobGraphInternal(jobGraph, packagedProgram.getUserCodeClassLoader());
    }


    protected static void deployJobGraphInternal(JobGraph jobGraph, ClassLoader userCodeClassLoader) throws Exception {
        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();

        YarnClusterDescriptor clusterDescriptor = clusterClientFactory.createClusterDescriptor(configuration);

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
