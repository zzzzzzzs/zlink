package com.zlink.controller;


import cn.hutool.core.net.NetUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zlink.common.utils.JacksonObject;
import com.zlink.entity.JobFlinkConf;
import com.zlink.model.ApiResponse;
import com.zlink.service.FlinkConfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * flink 配置信息 前端控制器
 * </p>
 *
 * @author zs
 * @since 2022-12-20
 */
@RestController
@RequestMapping("/flinkconf")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class FlinkConfController {
    private final FlinkConfService flinkConfService;

    /*
     * 查询
     **/
    @RequestMapping(value = "/pageFlinkConf", method = RequestMethod.GET)
    public ApiResponse pageFlinkConf(JobFlinkConf jobFlinkConf) {
        Page<JobFlinkConf> res = flinkConfService.pageFlinkConf(jobFlinkConf);
        return ApiResponse.ofSuccess(res);
    }

    /*
     * 更新 or 插入数据源
     */
    @RequestMapping(value = "/addFlinkConf", method = RequestMethod.POST)
    public ApiResponse addFlinkConf(@RequestBody JobFlinkConf jobFlinkConf) {
        return ApiResponse.ofSuccess(flinkConfService.saveOrUpdate(jobFlinkConf));
    }

    /*
     * 删除
     */
    @RequestMapping(value = "/delFlinkConf", method = RequestMethod.POST)
    public ApiResponse delFlinkConf(@RequestBody JacksonObject json) {
        return ApiResponse.ofSuccess(flinkConfService.removeById(json.getBigInteger("id")));
    }

    /*
     * 测试
     */
    @RequestMapping(value = "/testIp", method = RequestMethod.POST)
    public ApiResponse testIp(@RequestBody JobFlinkConf jobFlinkConf) {
        return ApiResponse.ofSuccess(NetUtil.ping(jobFlinkConf.getIp()));
    }
}

