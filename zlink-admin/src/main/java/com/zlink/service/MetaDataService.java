package com.zlink.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlink.common.model.Schema;
import com.zlink.dao.DatasourceMapper;
import com.zlink.entity.JobJdbcDatasource;
import com.zlink.metadata.driver.Driver;
import com.zlink.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zs
 * @since 2022-11-26
 */
@Service
@RequiredArgsConstructor
public class MetaDataService extends ServiceImpl<DatasourceMapper, JobJdbcDatasource> {

    public List<Schema> getSchemaAndTable(Integer id) {
        JobJdbcDatasource datasource = getById(id);
        Driver driver = Driver.build(datasource.getDriverConfig());
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        driver.close();
        return schemasAndTables;
    }
}
