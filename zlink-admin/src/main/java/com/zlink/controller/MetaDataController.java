package com.zlink.controller;


import com.zlink.model.ApiResponse;
import com.zlink.service.MetaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
@Slf4j
public class MetaDataController {

    private final MetaDataService metaDataService;

    /*
     * 获取数据库 schema & table
     **/
    @RequestMapping(value = "/getSchemaAndTable", method = RequestMethod.GET)
    public ApiResponse getSchemaAndTable(Integer id) {
        return ApiResponse.ofSuccess(metaDataService.getSchemaAndTable(id));
    }


}

