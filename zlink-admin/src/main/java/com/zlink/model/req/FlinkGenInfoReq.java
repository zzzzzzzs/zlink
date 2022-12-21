package com.zlink.model.req;

import com.zlink.cdc.FlinkInfo;
import com.zlink.common.model.Table;
import lombok.Data;

import java.util.List;

/**
 * @author zs
 * @date 2022/12/21
 * <p>
 * flink 生产任务参数
 */
@Data
public class FlinkGenInfoReq {
    String sourceId;
    String targetId;
    List<Table> sourceTables;
    List<Table> targetTables;
    Boolean remote;
    Integer parallelism;
    // 集群配置 id
    Integer flinkConfId;
    // local 模式的 flink 运行时信息
    List<FlinkInfo> infos;
}
