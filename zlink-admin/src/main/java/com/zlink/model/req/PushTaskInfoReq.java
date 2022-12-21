package com.zlink.model.req;

import com.zlink.cdc.FlinkInfo;
import lombok.Data;

import java.util.List;

/**
 * @author zs
 * @date 2022/12/21
 */
@Data
public class PushTaskInfoReq {
    private Integer clusterId;
    private Integer parallelism;
    private List<String> jobIds;
}
