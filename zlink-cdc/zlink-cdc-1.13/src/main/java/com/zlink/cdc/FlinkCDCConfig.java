package com.zlink.cdc;

import com.zlink.common.model.Table;
import lombok.Builder;
import lombok.Data;

/**
 * @author zs
 * @date 2022/12/9
 */
@Data
@Builder
public class FlinkCDCConfig {
    private String sourceHostname;
    private Integer sourcePort;
    private String startupMode;
    private String sourceUsername;
    private String sourcePassword;
    private Integer parallelism;
    private Table sourceTable;

    private String sinkDriverClass;
    private String sinkUrl;
    private String sinkUsername;
    private String sinkPassWord;
    private Table sinkTable;
}
