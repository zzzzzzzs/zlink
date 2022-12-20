package com.zlink.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlink.dao.FlinkModelMapper;
import com.zlink.entity.JobFlinkModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * flink 集群模式 服务类
 * </p>
 *
 * @author zs
 * @since 2022-12-19
 */
@Service
@RequiredArgsConstructor
public class FlinkModelService extends ServiceImpl<FlinkModelMapper, JobFlinkModel> {

    public List<JobFlinkModel> listFlinkModel() {
        return this.list();
    }
}
