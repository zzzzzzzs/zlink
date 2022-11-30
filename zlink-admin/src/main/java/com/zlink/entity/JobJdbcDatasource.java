package com.zlink.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import com.zlink.metadata.driver.DriverConfig;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * jdbc数据源配置
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("job_jdbc_datasource")
public class JobJdbcDatasource extends BaseEntity {

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据源类型
     */
    @TableField("database_type")
    private String databaseType;

    /**
     * 数据源名称
     */
    @TableField("database_name")
    private String databaseName;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * jdbc url
     */
    @TableField("jdbc_url")
    private String jdbcUrl;

    /**
     * jdbc驱动类
     */
    @TableField("jdbc_driver_class")
    private String jdbcDriverClass;

    /**
     * 备注
     */
    @TableField("comments")
    private String comments;

    /**
     * System 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * System 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否是内网
     */
    @TableField(exist = false)
    private Boolean isInner;

    public DriverConfig getDriverConfig() {
        return new DriverConfig(databaseName, databaseType, jdbcDriverClass, jdbcUrl, userName, password);
    }
}
