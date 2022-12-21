package com.zlink.cdc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zlink.common.model.Table;
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
    @JsonIgnore
    Table sourceTable;
    @JsonIgnore
    Table targetTable;
    @JsonIgnore // 返回的数据不包含该属性
    private JobClient jobClient;
    // 执行模式：local, yarn, session, k8s
    private String model;
    private String url;
    // run, stop
    private String status;
}
