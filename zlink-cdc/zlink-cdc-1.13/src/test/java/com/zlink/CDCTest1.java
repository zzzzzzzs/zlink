package com.zlink;

import com.zlink.common.assertion.Asserts;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.FlinkPipelineTranslationUtil;
import org.apache.flink.client.deployment.ClusterClientJobClientAdapter;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.executors.PipelineExecutorUtils;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.DeploymentOptionsInternal;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * @author zs
 * @date 2022/12/10
 */
public class CDCTest1 {
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



        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
//        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        TableEnvironmentImpl tableEnv = TableEnvironmentImpl.create(settings);
        tableEnv.getConfig().setSqlDialect(SqlDialect.DEFAULT);

        String sourceDDL =
                "CREATE TABLE dy_order_source (\n" +
                        "`id` INT,\n" +
                        "`shop_id` BIGINT,\n" +
                        "`shop_name` STRING,\n" +
                        "`open_id` STRING,\n" +
                        "`order_id` STRING,\n" +
                        "`order_level` BIGINT,\n" +
                        "`biz` BIGINT,\n" +
                        "`biz_desc` STRING,\n" +
                        "`order_type` BIGINT,\n" +
                        "`order_type_desc` STRING,\n" +
                        "`trade_type` STRING,\n" +
                        "`trade_type_desc` STRING,\n" +
                        "`order_status` BIGINT,\n" +
                        "`order_status_desc` STRING,\n" +
                        "`main_status` BIGINT,\n" +
                        "`main_status_desc` STRING,\n" +
                        "`pay_time` STRING,\n" +
                        "`order_expire_time` BIGINT,\n" +
                        "`finish_time` STRING,\n" +
                        "`create_time` STRING,\n" +
                        "`update_time` STRING,\n" +
                        "`cancel_reason` STRING,\n" +
                        "`buyer_words` STRING,\n" +
                        "`seller_words` STRING,\n" +
                        "`b_type` BIGINT,\n" +
                        "`b_type_desc` STRING,\n" +
                        "`sub_b_type` BIGINT,\n" +
                        "`sub_b_type_desc` STRING,\n" +
                        "`app_id` BIGINT,\n" +
                        "`pay_type` BIGINT,\n" +
                        "`channel_payment_no` STRING,\n" +
                        "`order_amount` BIGINT,\n" +
                        "`pay_amount` BIGINT,\n" +
                        "`post_amount` BIGINT,\n" +
                        "`post_insurance_amount` BIGINT,\n" +
                        "`modify_amount` BIGINT,\n" +
                        "`modify_post_amount` BIGINT,\n" +
                        "`promotion_amount` BIGINT,\n" +
                        "`promotion_shop_amount` BIGINT,\n" +
                        "`promotion_platform_amount` BIGINT,\n" +
                        "`shop_cost_amount` BIGINT,\n" +
                        "`platform_cost_amount` BIGINT,\n" +
                        "`promotion_talent_amount` BIGINT,\n" +
                        "`promotion_pay_amount` BIGINT,\n" +
                        "`post_tel` STRING,\n" +
                        "`post_receiver` STRING,\n" +
                        "`post_addr` STRING,\n" +
                        "`exp_ship_time` STRING,\n" +
                        "`ship_time` STRING,\n" +
                        "`logistics_info` STRING,\n" +
                        "`promotion_detail` STRING,\n" +
                        "`sku_order_list` STRING,\n" +
                        "`seller_remark_stars` BIGINT,\n" +
                        "`order_phase_list` STRING,\n" +
                        "`doudian_open_id` STRING,\n" +
                        "`serial_number_list` STRING,\n" +
                        "`promotion_redpack_amount` BIGINT,\n" +
                        "`promotion_redpack_platform_amount` BIGINT,\n" +
                        "`promotion_redpack_talent_amount` BIGINT,\n" +
                        "`user_id_info` STRING,\n" +
                        "`appointment_ship_time` STRING,\n" +
                        "`d_car_shop_biz_data` STRING,\n" +
                        "`shop_order_tag_ui` STRING,\n" +
                        "`total_promotion_amount` BIGINT,\n" +
                        "`post_origin_amount` BIGINT,\n" +
                        "`post_promotion_amount` BIGINT,\n" +
                        "`user_tag_ui` STRING,\n" +
                        "`author_cost_amount` BIGINT,\n" +
                        "`only_platform_cost_amount` BIGINT,\n" +
                        "`promise_info` STRING,\n" +
                        "`mask_post_receiver` STRING,\n" +
                        "`mask_post_tel` STRING,\n" +
                        "`mask_post_addr` STRING,\n" +
                        "`user_coordinate` STRING,\n" +
                        "`earliest_receipt_time` STRING,\n" +
                        "`latest_receipt_time` STRING,\n" +
                        "`early_arrival` INT,\n" +
                        "`target_arrival_time` STRING,\n" +
                        "`packing_amount` BIGINT,\n" +
                        "primary key (id) not enforced\n" +
                        ") with ( \n" +
                        " 'connector' = 'mysql-cdc',\n" +
                        " 'hostname' = 'pro-spark-shop-slave-01.mysql.rds.aliyuncs.com',\n" +
                        " 'port' = '3306',\n" +
                        " 'username' = 'bigdata_quickbi_user',\n" +
                        " 'password' = 'oz!oO&fVmUdIm4Qq',\n" +
                        " 'database-name' = 'shop_order',\n" +
                        " 'table-name' = 'dy_order',\n" +
                        " 'scan.startup.mode' = 'initial'\n" +
                        ")";
        // 输出目标表
        String sinkDDL =
                "CREATE TABLE dy_order_sink (\n" +
                        "`id` INT,\n" +
                        "`shop_id` BIGINT,\n" +
                        "`shop_name` STRING,\n" +
                        "`open_id` STRING,\n" +
                        "`order_id` STRING,\n" +
                        "`order_level` BIGINT,\n" +
                        "`biz` BIGINT,\n" +
                        "`biz_desc` STRING,\n" +
                        "`order_type` BIGINT,\n" +
                        "`order_type_desc` STRING,\n" +
                        "`trade_type` STRING,\n" +
                        "`trade_type_desc` STRING,\n" +
                        "`order_status` BIGINT,\n" +
                        "`order_status_desc` STRING,\n" +
                        "`main_status` BIGINT,\n" +
                        "`main_status_desc` STRING,\n" +
                        "`pay_time` STRING,\n" +
                        "`order_expire_time` BIGINT,\n" +
                        "`finish_time` STRING,\n" +
                        "`create_time` STRING,\n" +
                        "`update_time` STRING,\n" +
                        "`cancel_reason` STRING,\n" +
                        "`buyer_words` STRING,\n" +
                        "`seller_words` STRING,\n" +
                        "`b_type` BIGINT,\n" +
                        "`b_type_desc` STRING,\n" +
                        "`sub_b_type` BIGINT,\n" +
                        "`sub_b_type_desc` STRING,\n" +
                        "`app_id` BIGINT,\n" +
                        "`pay_type` BIGINT,\n" +
                        "`channel_payment_no` STRING,\n" +
                        "`order_amount` BIGINT,\n" +
                        "`pay_amount` BIGINT,\n" +
                        "`post_amount` BIGINT,\n" +
                        "`post_insurance_amount` BIGINT,\n" +
                        "`modify_amount` BIGINT,\n" +
                        "`modify_post_amount` BIGINT,\n" +
                        "`promotion_amount` BIGINT,\n" +
                        "`promotion_shop_amount` BIGINT,\n" +
                        "`promotion_platform_amount` BIGINT,\n" +
                        "`shop_cost_amount` BIGINT,\n" +
                        "`platform_cost_amount` BIGINT,\n" +
                        "`promotion_talent_amount` BIGINT,\n" +
                        "`promotion_pay_amount` BIGINT,\n" +
                        "`post_tel` STRING,\n" +
                        "`post_receiver` STRING,\n" +
                        "`post_addr` STRING,\n" +
                        "`exp_ship_time` STRING,\n" +
                        "`ship_time` STRING,\n" +
                        "`logistics_info` STRING,\n" +
                        "`promotion_detail` STRING,\n" +
                        "`sku_order_list` STRING,\n" +
                        "`seller_remark_stars` BIGINT,\n" +
                        "`order_phase_list` STRING,\n" +
                        "`doudian_open_id` STRING,\n" +
                        "`serial_number_list` STRING,\n" +
                        "`promotion_redpack_amount` BIGINT,\n" +
                        "`promotion_redpack_platform_amount` BIGINT,\n" +
                        "`promotion_redpack_talent_amount` BIGINT,\n" +
                        "`user_id_info` STRING,\n" +
                        "`appointment_ship_time` STRING,\n" +
                        "`d_car_shop_biz_data` STRING,\n" +
                        "`shop_order_tag_ui` STRING,\n" +
                        "`total_promotion_amount` BIGINT,\n" +
                        "`post_origin_amount` BIGINT,\n" +
                        "`post_promotion_amount` BIGINT,\n" +
                        "`user_tag_ui` STRING,\n" +
                        "`author_cost_amount` BIGINT,\n" +
                        "`only_platform_cost_amount` BIGINT,\n" +
                        "`promise_info` STRING,\n" +
                        "`mask_post_receiver` STRING,\n" +
                        "`mask_post_tel` STRING,\n" +
                        "`mask_post_addr` STRING,\n" +
                        "`user_coordinate` STRING,\n" +
                        "`earliest_receipt_time` STRING,\n" +
                        "`latest_receipt_time` STRING,\n" +
                        "`early_arrival` INT,\n" +
                        "`target_arrival_time` STRING,\n" +
                        "`packing_amount` BIGINT,\n" +
                        "primary key (id) not enforced\n" +
                        ") with ( \n" +
                        " 'connector' = 'jdbc',\n" +
                        " 'driver' = 'com.mysql.cj.jdbc.Driver',\n" +
                        " 'url' = 'jdbc:mysql://172.16.5.164:3306/zlink',\n" +
                        " 'username' = 'zlink_user',\n" +
                        " 'password' = '$m!hQ!X&j%nZa#KnB',\n" +
                        " 'table-name' = 'dy_order'\n" +
                        ")";
        // 简单的聚合处理
        String transformDmlSQL = "insert into dy_order_sink select * from dy_order_source";

        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(sinkDDL);
        TableResult tableResult = tableEnv.executeSql(transformDmlSQL);

//        deployJobGraphInternal(jobGraph, CDCTest1.class.getClassLoader());
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
