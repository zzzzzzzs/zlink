package com.zlink.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 数据源类型
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("job_datasource_type")
public class JobDatasourceType {
    /**
     * 数据源类型
     */
    @TableField("database_type")
    private String databaseType;

    /**
     * jdbc driver class
     */
    @TableField("jdbc_driver_class")
    private String jdbcDriverClass;
}
