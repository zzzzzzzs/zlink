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
public class FlinkLocalInfo {
    private String jobId;
    @JsonIgnore
    private JobClient jobClient;
    private String url;
    // run, stop
    private String status;
}
