package com.zlink.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zlink.entity.JobFlinkConf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * flink 配置信息 Mapper 接口
 * </p>
 *
 * @author zs
 * @since 2022-12-20
 */
@Mapper
public interface FlinkConfMapper extends BaseMapper<JobFlinkConf> {

    Page<JobFlinkConf> pageFlinkConf(Page<JobFlinkConf> page, @Param("param") JobFlinkConf jobFlinkConf);
}
