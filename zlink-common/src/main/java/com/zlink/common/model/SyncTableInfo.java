package com.zlink.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zs
 * @date 2022/12/13
 * <p>
 * 同步表的额外信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyncTableInfo {
    // 表前缀
    private String tablePrefix;
    // 表后缀
    private String tableSuffix;
}
