package com.zlink.cdc;

import com.zlink.common.model.Table;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zs
 * @date 2022/12/9
 */
@Data
@Builder
@Accessors(chain = true)
public class FlinkCDCConfig {

    private String sourceDataBaseType; // 数据库类型
    private String sourceHostname;
    private Integer sourcePort;
    private String sourceUsername;
    private String sourcePassword;
    private Table sourceTable;

    private String sinkDataBaseType; // 数据库类型
    private String sinkDriverClass;
    private String sinkUrl;
    private String sinkUsername;
    private String sinkPassWord;
    private Table sinkTable;

    private String startupMode;
    private Integer parallelism;

    private Integer localPort;

    private Boolean remote; // 远程标志
    private String remoteIp;
    private Integer remotePort;
}
