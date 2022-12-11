package com.zlink.controller;


import com.zlink.common.utils.JacksonObject;
import com.zlink.model.ApiResponse;
import com.zlink.service.MetaDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 元数据
 * </p>
 *
 * @author zs
 * @since 2022-11-28
 */
@RestController
@RequestMapping("/metadata")
@RequiredArgsConstructor
@CrossOrigin
public class MetaDataController {

    private final MetaDataService metaDataService;

    /*
     * 获取数据库 schema & table
     **/
    @RequestMapping(value = "/getSchemaAndTable", method = RequestMethod.GET)
    public ApiResponse getSchemaAndTable(Integer id) {
        return ApiResponse.ofSuccess(metaDataService.getSchemaAndTable(id));
    }

    /**
     * 获取元数据的指定表的列
     */
    @RequestMapping(value = "/listColumns", method = RequestMethod.GET)
    public ApiResponse listColumns(Integer id, String schemaName, String tableName) {
        return ApiResponse.ofSuccess(metaDataService.listColumns(id, schemaName, tableName));
    }

    /**
     * 同步表结构
     */
    @RequestMapping(value = "/syncTableStruct", method = RequestMethod.POST)
    public ApiResponse syncTableStruct(@RequestBody JacksonObject json) {
        return metaDataService.syncTableStruct(json);
    }
}

