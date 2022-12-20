package com.zlink.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlink.dao.DatasourceMapper;
import com.zlink.dao.FlinkConfMapper;
import com.zlink.entity.JobFlinkConf;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zlink.entity.JobJdbcDatasource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * flink 配置信息 服务类
 * </p>
 *
 * @author zs
 * @since 2022-12-20
 */
@Service
@RequiredArgsConstructor
public class FlinkConfService extends ServiceImpl<FlinkConfMapper, JobFlinkConf> {
    private final FlinkConfMapper flinkConfMapper;

    public Page<JobFlinkConf> pageFlinkConf(JobFlinkConf jobFlinkConf) {
        Page<JobFlinkConf> page = new Page<>(jobFlinkConf.getPageNo(), jobFlinkConf.getPageSize());
        return flinkConfMapper.pageFlinkConf(page, jobFlinkConf);
    }
}
