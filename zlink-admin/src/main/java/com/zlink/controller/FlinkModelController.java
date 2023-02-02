package com.zlink.controller;


import com.zlink.model.ApiResponse;
import com.zlink.service.FlinkModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * flink 集群模式 前端控制器
 * </p>
 *
 * @author zs
 * @since 2022-12-19
 */
@RestController
@RequestMapping("/api/flink-model")
@RequiredArgsConstructor
@CrossOrigin
public class FlinkModelController {
    private final FlinkModelService flinkModelService;

    /**
     * 查询 flink 模式
     */
    @RequestMapping(value = "/listFlinkModel", method = RequestMethod.GET)
    public ApiResponse listFlinkModel() {
        return ApiResponse.ofSuccess(flinkModelService.listFlinkModel());
    }

}

