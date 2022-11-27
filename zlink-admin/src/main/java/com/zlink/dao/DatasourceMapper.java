package com.zlink.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zlink.entity.JobDatasourceType;
import com.zlink.entity.JobJdbcDatasource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * jdbc数据源配置 Mapper 接口
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@Mapper
public interface DatasourceMapper extends BaseMapper<JobJdbcDatasource> {
    Page<JobJdbcDatasource> pageDataSource(Page<JobJdbcDatasource> page, @Param("param") JobJdbcDatasource jobJdbcDatasource);

    List<JobDatasourceType> listDataSourceType();
}
