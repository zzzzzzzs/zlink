package com.zlink.deploy;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.common.protocol.types.Field;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlinkUtils {
    // Get flink-dist_*.jar path
    public static String getFlinkDistPath(String path) {
        if (path == null || "".equals(path)) {
            throw new IllegalArgumentException(path + " is null");
        }

        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException(path + " is not exists");
        }

        final File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            throw new RuntimeException("there is no file in the path:" + path);
        }

        // 遍历files，满足正则表达式的就返回；如果有多个的话要提示报错
        List<File> distPath = Arrays.stream(files).filter(ele -> ele.getName().matches("flink-dist_.*")).collect(Collectors.toList());

        if (distPath.size() == 0) {
            throw new RuntimeException(
                    "there is no flink-dist file in the path:" + path + ",please check");
        } else if (distPath.size() > 1) {
            throw new RuntimeException(
                    "there are " + distPath.size() + " file-dist file in the path,only need one");
        } else {
            return distPath.get(0).getPath();
        }
    }

    // Get Yarn RUNNING flink task info
    public static void byYarnGetRunningFLinkInfo() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.25.110:8088/ws/v1/cluster/apps?state=RUNNING&applicationTypes=Apache Flink")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    public static void getFlinkOnYarnAssignTaskInfo(String appId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.25.110:8088/ws/v1/cluster/apps/" + appId)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }


    public static void main(String[] args) throws IOException {
        byYarnGetRunningFLinkInfo();

        getFlinkOnYarnAssignTaskInfo("application_1673589454594_0005");
    }
}
