package com.zlink.dao;

import com.zlink.entity.JobFlinkModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * flink 集群模式 Mapper 接口
 * </p>
 *
 * @author zs
 * @since 2022-12-19
 */
@Mapper
public interface FlinkModelMapper extends BaseMapper<JobFlinkModel> {

}
