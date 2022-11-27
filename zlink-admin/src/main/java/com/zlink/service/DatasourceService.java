package com.zlink.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlink.dao.DatasourceMapper;
import com.zlink.entity.JobDatasourceType;
import com.zlink.entity.JobJdbcDatasource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

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
public class DatasourceService extends ServiceImpl<DatasourceMapper, JobJdbcDatasource> {
    private final DatasourceMapper jobJdbcDatasourceMapper;

    public Page<JobJdbcDatasource> pageDataSource(JobJdbcDatasource jobJdbcDatasource) {
        Page<JobJdbcDatasource> page = new Page<>(jobJdbcDatasource.getPageNo(), jobJdbcDatasource.getPageSize());
        return jobJdbcDatasourceMapper.pageDataSource(page, jobJdbcDatasource);
    }

    public List<JobDatasourceType> listDataSourceType() {
        return jobJdbcDatasourceMapper.listDataSourceType();
    }

    public boolean testJdbc(JobJdbcDatasource jobJdbcDatasource) {
        try {
            Class.forName(jobJdbcDatasource.getJdbcDriverClass());
            String url = jobJdbcDatasource.getJdbcUrl();
            String username = jobJdbcDatasource.getUserName();
            String password = jobJdbcDatasource.getPassword();
            Connection conn = DriverManager.getConnection(url, username, password);//用参数得到连接对象
            return true;
        } catch (Exception e) {
            log.error("{}", e);
            return false;
        }
    }
}
