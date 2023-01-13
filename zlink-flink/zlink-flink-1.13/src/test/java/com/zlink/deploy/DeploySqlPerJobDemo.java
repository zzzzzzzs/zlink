package com.zlink.deploy;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.util.List;

public class DeploySqlPerJobDemo {
    public static void main(String[] args) throws Exception {
        String[] sqls = Resources.asCharSource(Resources.getResource("demo.sql"), Charsets.UTF_8).read().split(";");
        DeployJobParam param = new DeployJobParam();
        param.setSqls(List.of(sqls));
        param.setFLINK_HOME("/opt/module/flink-1.13.6");



        YarnPerjobDeployer.deploySql(param);
    }
}
