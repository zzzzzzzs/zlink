package com.zlink.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zlink.entity.JobJdbcDatasource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * jdbc数据源配置 Mapper 接口
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@Mapper
public interface JobJdbcDatasourceMapper extends BaseMapper<JobJdbcDatasource> {
    Page<JobJdbcDatasource> pageDataSource(Page<JobJdbcDatasource> page, JobJdbcDatasource jobJdbcDatasource);
}
