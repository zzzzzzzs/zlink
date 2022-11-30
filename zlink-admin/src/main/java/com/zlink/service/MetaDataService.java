package com.zlink.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlink.dao.DatasourceMapper;
import com.zlink.entity.JobJdbcDatasource;
import com.zlink.metadata.driver.Driver;
import com.zlink.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author zs
 * @since 2022-11-26
 */
@Service
@RequiredArgsConstructor
public class MetaDataService extends ServiceImpl<DatasourceMapper, JobJdbcDatasource> {

    public ApiResponse getSchemaAndTable(Integer id) {
        JobJdbcDatasource datasource = getById(id);
        Driver driver = Driver.build(datasource.getDriverConfig());
        return null;
    }
}
