package com.zlink.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Builder
@Accessors(chain = true)
@TableName("job_jdbc_datasource")
public class JobJdbcDatasource extends BaseEntity {

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据源名称
     */
    @TableField("datasource_name")
    private String datasourceName;

    /**
     * 数据源
     */
    @TableField("datasource")
    private String datasource;

    /**
     * 数据源分组
     */
    @TableField("datasource_group")
    private String datasourceGroup;

    /**
     * 数据库名
     */
    @TableField("database_name")
    private String databaseName;

    /**
     * 用户名
     */
    @TableField("jdbc_username")
    private String jdbcUsername;

    /**
     * 密码
     */
    @TableField("jdbc_password")
    private String jdbcPassword;

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
     * zk 地址
     */
    @TableField("zk_adress")
    private String zkAdress;

    /**
     * enum 状态. 0:禁用;1:启用;
     */
    @TableField("status")
    private Boolean status;

    /**
     * 备注
     */
    @TableField("comments")
    private String comments;

    /**
     * System 组 ID
     */
    @TableField("gid")
    private Integer gid;

    /**
     * System 企业 ID
     */
    @TableField("enterprise_id")
    private Long enterpriseId;

    /**
     * System 创建人
     */
    @TableField("create_uid")
    private Long createUid;

    /**
     * System 创建人
     */
    @TableField("create_uname")
    private String createUname;

    /**
     * System 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * System 更新人
     */
    @TableField("update_uid")
    private Long updateUid;

    /**
     * System 更新人
     */
    @TableField("update_uname")
    private String updateUname;

    /**
     * System 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * System 删除标记
     */
    @TableField("delete_flag")
    private Integer deleteFlag;
}
