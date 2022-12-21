package com.zlink.cdc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.apache.flink.core.execution.JobClient;

/**
 * @author zs
 * @date 2022/12/11
 * flink local info
 */
@Data
@Builder
public class FlinkInfo {
    private String jobId;
    @JsonIgnore // 返回的数据不包含该属性
    private JobClient jobClient;
    @JsonIgnore
    private FlinkCDCConfig config;
    // 执行模式：local, yarn, session, k8s
    private String model;
    private String url;
    // run, stop
    private String status;
}
