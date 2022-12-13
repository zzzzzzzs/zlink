package com.zlink.controller;

import com.zlink.cdc.FlinkInfo;
import com.zlink.common.utils.JacksonObject;
import com.zlink.model.ApiResponse;
import com.zlink.service.FlinkcdcService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author zs
 * @date 2022/12/9
 */
@RestController
@RequestMapping("/cdc")
@RequiredArgsConstructor
@CrossOrigin
public class FlinkcdcController {
    private final FlinkcdcService flinkcdcService;

    /**
     * 本地模式 flink cdc
     */
    @RequestMapping(value = "/localFlinkCDC", method = RequestMethod.POST)
    public ApiResponse localFlinkCDC(@RequestBody JacksonObject json) throws ExecutionException, InterruptedException {
        return ApiResponse.ofSuccess(flinkcdcService.localFlinkCDC(json));
    }

    /**
     * 获取 flink 状态
     */
    @RequestMapping(value = "/getLocalFlinkInfo", method = RequestMethod.GET)
    public ApiResponse getFlinkInfo() throws ExecutionException, InterruptedException {
        return ApiResponse.ofSuccess(flinkcdcService.getLocalFlinkInfo());
    }

    /**
     * stop flink task
     */
    @RequestMapping(value = "/stopFlinkTask", method = RequestMethod.POST)
    public ApiResponse stopFlinkTask(@RequestBody List<FlinkInfo> infos) {
        return ApiResponse.ofSuccess(flinkcdcService.stopFlinkTask(infos));
    }

}
