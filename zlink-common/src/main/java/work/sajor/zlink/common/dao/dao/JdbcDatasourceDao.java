package work.sajor.zlink.common.dao.dao;

import work.sajor.crap.core.mybatis.dao.BaseDao;
import work.sajor.zlink.common.dao.entity.JdbcDatasource;
import work.sajor.zlink.common.dao.mapper.JdbcDatasourceMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;

/**
 * jdbc数据源配置 Dao
 *
 * @author Sajor
 * @since 2022-11-23
 */
@Repository
@CacheConfig(cacheNames = "JdbcDatasource") // , keyGenerator = "cacheKeyGenerator")
public class JdbcDatasourceDao extends BaseDao<JdbcDatasourceMapper, JdbcDatasource> {

}
