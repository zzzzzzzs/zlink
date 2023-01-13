package com.zlink.deploy;


import lombok.Data;

import java.util.List;
import java.util.Properties;

@Data
public class DeployJobParam {
    List<String> sqls;
    // 额外的 flink 参数，需要传入到 org.apache.flink.configuration.Configuration 中
    Properties props = new Properties();
    String FLINK_HOME;
    String FLINK_CONF_DIR;
    String FLINK_LIB_DIR;
    String FLINK_PLUGINS_DIR;
    String FLINK_DIST_JAR;
    String HADOOP_CORE_SITE_DIR;
    String HADOOP_HDFS_SITE_DIR;
    String HADOOP_YARN_SITE_DIR;

    public void setFLINK_HOME(String FLINK_HOME) {
        this.FLINK_HOME = FLINK_HOME;
        this.FLINK_CONF_DIR = FLINK_HOME + "/conf";
        this.FLINK_LIB_DIR = FLINK_HOME + "/lib";
        this.FLINK_PLUGINS_DIR = FLINK_HOME + "/plugins/";
        this.FLINK_DIST_JAR = FlinkUtils.getFlinkDistPath(this.FLINK_LIB_DIR);
    }
}
