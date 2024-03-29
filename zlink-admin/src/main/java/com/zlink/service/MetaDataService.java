package com.zlink.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zlink.cdc.mysql.MysqlCDCBuilder;
import com.zlink.common.model.Column;
import com.zlink.common.model.Schema;
import com.zlink.common.model.SyncTableInfo;
import com.zlink.common.model.Table;
import com.zlink.common.utils.JacksonObject;
import com.zlink.dao.DatasourceMapper;
import com.zlink.entity.JobJdbcDatasource;
import com.zlink.metadata.driver.Driver;
import com.zlink.model.ApiResponse;
import com.zlink.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zs
 * @since 2022-11-26
 */
@Service
@RequiredArgsConstructor
public class MetaDataService extends ServiceImpl<DatasourceMapper, JobJdbcDatasource> {

    private static Logger logger = LoggerFactory.getLogger(MetaDataService.class);

    public List<Schema> getSchemaAndTable(Integer id) {
        JobJdbcDatasource datasource = getById(id);
        Driver driver = Driver.build(datasource.getDriverConfig());
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        driver.close();
        return schemasAndTables;
    }

    public List<Column> listColumns(Integer id, String schemaName, String tableName) {
        JobJdbcDatasource dataBase = getById(id);
        Driver driver = Driver.build(dataBase.getDriverConfig());
        List<Column> columns = driver.listColumns(schemaName, tableName);
        driver.close();
        return columns;
    }


    public ApiResponse syncTableStruct(JacksonObject json) {
        Integer sourceId = json.getBigInteger("sourceId").intValue();
        Integer targetId = json.getNode("targetData").get("targetId").asInt();
        String targetSchema = json.getNode("targetData").get("targetSchema").asText();
        List<Table> tables = json.getObject("tables", new TypeReference<List<Table>>() {
        });
        SyncTableInfo syncTableInfo = json.getObject("syncTableInfo", new TypeReference<SyncTableInfo>() {
        });
        // 获取列信息
        JobJdbcDatasource source = getById(sourceId);
        JobJdbcDatasource target = getById(targetId);
        Driver sourceDriver = Driver.build(source.getDriverConfig());
        tables.forEach(ele -> {
            ele.setColumns(sourceDriver.listColumns(ele.getSchema(), ele.getName()));
        });
        // 创建表
        Driver targetDriver = Driver.build(target.getDriverConfig());
        try {
            List<String> targetCreateTableSqls = tables.stream().map(ele -> targetDriver.generateCreateTableSql(ele, targetSchema, syncTableInfo)).collect(Collectors.toList());
            for (String sql : targetCreateTableSqls) {
                targetDriver.execute(sql);
            }
            sourceDriver.close();
            targetDriver.close();
            return ApiResponse.ofSuccess(true);
        } catch (Exception e) {
            logger.error("出现错误 {}", e);
            return ApiResponse.of(400, false, e.getMessage());
        } finally {
            sourceDriver.close();
            targetDriver.close();
        }
    }
}
