package com.zlink.controller;

import com.zlink.cdc.FlinkInfo;
import com.zlink.model.ApiResponse;
import com.zlink.model.req.FlinkGenInfoReq;
import com.zlink.model.req.PushTaskInfoReq;
import com.zlink.service.FlinkCDCService;
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
public class FlinkCDCController {
    private final FlinkCDCService flinkcdcService;

    /**
     * 本地模式 flink cdc
     */
    @RequestMapping(value = "/localFlinkCDC", method = RequestMethod.POST)
    public ApiResponse flinkCDC(@RequestBody FlinkGenInfoReq req) throws ExecutionException, InterruptedException {
        return ApiResponse.ofSuccess(flinkcdcService.localFlinkCDC(req));
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

    /**
     * push task
     */
    @RequestMapping(value = "/pushTask", method = RequestMethod.POST)
    public ApiResponse pushTask(@RequestBody PushTaskInfoReq req) throws Exception {
        return ApiResponse.ofSuccess(flinkcdcService.pushTask(req));
    }

}
