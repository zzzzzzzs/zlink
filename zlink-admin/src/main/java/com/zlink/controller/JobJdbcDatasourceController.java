package com.zlink.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zlink.entity.JobJdbcDatasource;
import com.zlink.model.ApiResponse;
import com.zlink.service.JobJdbcDatasourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
public class JobJdbcDatasourceController {

    private final JobJdbcDatasourceService jobJdbcDatasourceService;

    // 查询数据源
    @RequestMapping(value = "/listDataSource", method = RequestMethod.GET)
    public ApiResponse listDataSource(JobJdbcDatasource jobJdbcDatasource) {
        Page<JobJdbcDatasource> res = jobJdbcDatasourceService.pageDataSource(jobJdbcDatasource);
        return ApiResponse.ofSuccess(res);
    }

    // 更新 or 插入数据源
    @RequestMapping(value = "/addDataSource", method = RequestMethod.POST)
    public ApiResponse addDataSource(JobJdbcDatasource jobJdbcDatasource) {
        return ApiResponse.ofSuccess(jobJdbcDatasourceService.saveOrUpdate(jobJdbcDatasource));
    }

}

