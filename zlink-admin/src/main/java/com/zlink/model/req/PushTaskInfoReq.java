package com.zlink.model.req;

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
    // TODO 这里本来是 Properties 但是不知道怎么传递，后面再说
    private List<Object> props;
}
