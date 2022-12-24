package com.zlink.cdc;

import com.squareup.okhttp.*;

import java.io.IOException;

/**
 * @author zs
 * @date 2022/12/11
 * flink local info
 * <p>
 * yarn 官网 rest api
 * https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/ResourceManagerRest.html#Enabling_CORS_support
 */

public class YarnGetInfo {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8088/ws/v1/cluster/apps?state=RUNNING&applicationTypes=Apache Flink")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }
}
