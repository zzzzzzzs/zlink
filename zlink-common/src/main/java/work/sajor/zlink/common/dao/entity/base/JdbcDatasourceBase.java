package work.sajor.zlink.common.dao.entity.base;


import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import java.sql.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import work.sajor.crap.core.mybatis.support.TableCode;
import work.sajor.crap.core.mybatis.facade.Entity;
import work.sajor.zlink.common.dao.entity.JdbcDatasource.StatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

/**
 * jdbc数据源配置
 * 数据表实体, 与数据库保持同步, 不可修改
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "job_jdbc_datasource", autoResultMap = true)
@ApiModel(value="JdbcDatasourceBase对象", description="jdbc数据源配置")
public class JdbcDatasourceBase implements Serializable,work.sajor.crap.core.mybatis.facade.Entity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自增主键")
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @JsonProperty("id")
    @JsonFormat(shape = Shape.STRING)
    protected Long id;

    @ApiModelProperty(value = "数据源名称")
    @TableField("`datasource_name`")
    @JsonProperty("datasource_name")
    protected String datasourceName;

    @ApiModelProperty(value = "数据源")
    @TableField("`datasource`")
    @JsonProperty("datasource")
    protected String datasource;

    @ApiModelProperty(value = "数据源分组")
    @TableField("`datasource_group`")
    @JsonProperty("datasource_group")
    protected String datasourceGroup;

    @ApiModelProperty(value = "数据库名")
    @TableField("`database_name`")
    @JsonProperty("database_name")
    protected String databaseName;

    @ApiModelProperty(value = "用户名")
    @TableField("`jdbc_username`")
    @JsonProperty("jdbc_username")
    protected String jdbcUsername;

    @ApiModelProperty(value = "密码")
    @TableField("`jdbc_password`")
    @JsonProperty("jdbc_password")
    protected String jdbcPassword;

    @ApiModelProperty(value = "jdbc url")
    @TableField("`jdbc_url`")
    @JsonProperty("jdbc_url")
    protected String jdbcUrl;

    @ApiModelProperty(value = "jdbc驱动类")
    @TableField("`jdbc_driver_class`")
    @JsonProperty("jdbc_driver_class")
    protected String jdbcDriverClass;

    @TableField("`zk_adress`")
    @JsonProperty("zk_adress")
    protected String zkAdress;

    @ApiModelProperty(value = "enum 状态. 0:禁用;1:启用;")
    @TableField("`status`")
    @JsonProperty("status")
    protected StatusEnum status;

    @ApiModelProperty(value = "备注")
    @TableField("`comments`")
    @JsonProperty("comments")
    protected String comments;

    @ApiModelProperty(value = "System 组 ID")
    @TableField("`gid`")
    @JsonProperty("gid")
    protected Integer gid;

    @ApiModelProperty(value = "System 企业 ID")
    @TableField("`enterprise_id`")
    @JsonProperty("enterprise_id")
    @JsonFormat(shape = Shape.STRING)
    protected Long enterpriseId;

    @ApiModelProperty(value = "System 创建人")
    @TableField("`create_uid`")
    @JsonProperty("create_uid")
    @JsonFormat(shape = Shape.STRING)
    protected Long createUid;

    @ApiModelProperty(value = "System 创建人")
    @TableField("`create_uname`")
    @JsonProperty("create_uname")
    protected String createUname;

    @ApiModelProperty(value = "System 创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createTime;

    @ApiModelProperty(value = "System 更新人")
    @TableField("`update_uid`")
    @JsonProperty("update_uid")
    @JsonFormat(shape = Shape.STRING)
    protected Long updateUid;

    @ApiModelProperty(value = "System 更新人")
    @TableField("`update_uname`")
    @JsonProperty("update_uname")
    protected String updateUname;

    @ApiModelProperty(value = "System 更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonProperty("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime updateTime;

    @ApiModelProperty(value = "System 删除标记")
    @TableField("job_jdbc_datasource.`delete_flag`")
    @JsonProperty("delete_flag")
    @TableLogic
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime deleteFlag;

    // ****************************** Db Field ******************************

    public static final String Table = "job_jdbc_datasource";

    public static class Fields {
        public static final String id = "id";
        public static final String datasourceName = "datasource_name";
        public static final String datasource = "datasource";
        public static final String datasourceGroup = "datasource_group";
        public static final String databaseName = "database_name";
        public static final String jdbcUsername = "jdbc_username";
        public static final String jdbcPassword = "jdbc_password";
        public static final String jdbcUrl = "jdbc_url";
        public static final String jdbcDriverClass = "jdbc_driver_class";
        public static final String zkAdress = "zk_adress";
        public static final String status = "status";
        public static final String comments = "comments";
        public static final String gid = "gid";
        public static final String enterpriseId = "enterprise_id";
        public static final String createUid = "create_uid";
        public static final String createUname = "create_uname";
        public static final String createTime = "create_time";
        public static final String updateUid = "update_uid";
        public static final String updateUname = "update_uname";
        public static final String updateTime = "update_time";
        public static final String deleteFlag = "delete_flag";
    }

    public static class Alias {
        public static final String id = "job_jdbc_datasource.id";
        public static final String datasourceName = "job_jdbc_datasource.datasource_name";
        public static final String datasource = "job_jdbc_datasource.datasource";
        public static final String datasourceGroup = "job_jdbc_datasource.datasource_group";
        public static final String databaseName = "job_jdbc_datasource.database_name";
        public static final String jdbcUsername = "job_jdbc_datasource.jdbc_username";
        public static final String jdbcPassword = "job_jdbc_datasource.jdbc_password";
        public static final String jdbcUrl = "job_jdbc_datasource.jdbc_url";
        public static final String jdbcDriverClass = "job_jdbc_datasource.jdbc_driver_class";
        public static final String zkAdress = "job_jdbc_datasource.zk_adress";
        public static final String status = "job_jdbc_datasource.status";
        public static final String comments = "job_jdbc_datasource.comments";
        public static final String gid = "job_jdbc_datasource.gid";
        public static final String enterpriseId = "job_jdbc_datasource.enterprise_id";
        public static final String createUid = "job_jdbc_datasource.create_uid";
        public static final String createUname = "job_jdbc_datasource.create_uname";
        public static final String createTime = "job_jdbc_datasource.create_time";
        public static final String updateUid = "job_jdbc_datasource.update_uid";
        public static final String updateUname = "job_jdbc_datasource.update_uname";
        public static final String updateTime = "job_jdbc_datasource.update_time";
        public static final String deleteFlag = "job_jdbc_datasource.delete_flag";
    }

    // ****************************** Entity ******************************

    @Override
    public Long getId() {
        return this.id;
    }

}
