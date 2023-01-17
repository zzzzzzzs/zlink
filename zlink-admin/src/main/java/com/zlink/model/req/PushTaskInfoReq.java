package com.zlink.model.req;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zs
 * @date 2022/12/21
 */
@Data
public class PushTaskInfoReq {
    private Integer clusterId;
    private Integer parallelism;
    private List<String> jobIds;
    private Map<String, Object> props;
}
