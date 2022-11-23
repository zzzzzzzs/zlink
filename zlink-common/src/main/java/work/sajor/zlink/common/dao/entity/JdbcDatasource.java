package work.sajor.zlink.common.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonValue;
import work.sajor.crap.core.mybatis.facade.FieldEnum;
import work.sajor.zlink.common.dao.entity.base.JdbcDatasourceBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
// import org.springframework.data.elasticsearch.annotations.Document;

/**
 * jdbc数据源配置
 * 实体扩展
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "job_jdbc_datasource", autoResultMap = true)
// @Document(indexName = "job_jdbc_datasource")
public class JdbcDatasource extends JdbcDatasourceBase {

    /**
      * enum 状态. 0:禁用;1:启用;
      */
    @Getter
    @AllArgsConstructor
    public enum StatusEnum implements FieldEnum<Boolean> {
      ON(Boolean.TRUE, "启用"),
      OFF(Boolean.FALSE, "禁用");

      @JsonValue
      private Boolean value;
      private String name;
    }
}