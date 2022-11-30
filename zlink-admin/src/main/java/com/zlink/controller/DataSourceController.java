package com.zlink.controller;


import cn.hutool.core.net.NetUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zlink.common.utils.JacksonObject;
import com.zlink.entity.JobJdbcDatasource;
import com.zlink.metadata.driver.Driver;
import com.zlink.model.ApiResponse;
import com.zlink.service.DatasourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>
 * jdbc数据源配置 前端控制器
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@RestController
@RequestMapping("/datasource")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class DataSourceController {

    private final DatasourceService datasourceService;

    /*
     * 查询数据源
     **/
    @RequestMapping(value = "/listDataSource", method = RequestMethod.GET)
    public ApiResponse listDataSource(JobJdbcDatasource jobJdbcDatasource) {
        Page<JobJdbcDatasource> res = datasourceService.pageDataSource(jobJdbcDatasource);
        try {
            for (JobJdbcDatasource record : res.getRecords()) {
                String ip = InetAddress.getByName(record.getJdbcUrl().split("://")[1].split(":")[0]).toString().split("/")[1];
                boolean innerIP = NetUtil.isInnerIP(ip);
                record.setIsInner(innerIP);
            }
        } catch (UnknownHostException e) {
            log.error("{}", e);
        }
        return ApiResponse.ofSuccess(res);
    }

    /*
     * 更新 or 插入数据源
     */
    @RequestMapping(value = "/addDataSource", method = RequestMethod.POST)
    public ApiResponse addDataSource(@RequestBody JobJdbcDatasource jobJdbcDatasource) {
        return ApiResponse.ofSuccess(datasourceService.saveOrUpdate(jobJdbcDatasource));
    }

    /*
     * 删除数据源
     */
    @RequestMapping(value = "/delDataSource", method = RequestMethod.POST)
    public ApiResponse delDataSource(@RequestBody JacksonObject json) {
        return ApiResponse.ofSuccess(datasourceService.removeById(json.getBigInteger("id")));
    }


    /*
     * 数据源下拉选
     */
    @RequestMapping(value = "/dataSourceType", method = RequestMethod.GET)
    public ApiResponse dataSourceType() {
        return ApiResponse.ofSuccess(datasourceService.listDataSourceType());
    }

    /*
     * 数据源测试
     */
    @RequestMapping(value = "/testJdbc", method = RequestMethod.POST)
    public ApiResponse testJdbc(@RequestBody JobJdbcDatasource jobJdbcDatasource) {
        return ApiResponse.ofSuccess(Driver.build(jobJdbcDatasource.getDriverConfig()).test());
    }
}

