package com.zlink.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlink.dao.JobJdbcDatasourceMapper;
import com.zlink.entity.JobJdbcDatasource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * jdbc数据源配置 服务实现类
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@Service
@RequiredArgsConstructor
public class JobJdbcDatasourceService extends ServiceImpl<JobJdbcDatasourceMapper, JobJdbcDatasource> {
    private final JobJdbcDatasourceMapper jobJdbcDatasourceMapper;

    public Page<JobJdbcDatasource> pageDataSource(JobJdbcDatasource jobJdbcDatasource) {
        Page<JobJdbcDatasource> page = new Page<>(jobJdbcDatasource.getPageNo(), jobJdbcDatasource.getPageSize());
        return  jobJdbcDatasourceMapper.pageDataSource(page, jobJdbcDatasource);
    }
}
