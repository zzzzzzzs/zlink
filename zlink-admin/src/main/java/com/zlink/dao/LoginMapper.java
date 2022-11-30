package com.zlink.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zlink.entity.JobSysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * <p>
 * 运营后台用户表 Mapper 接口
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@Mapper
public interface LoginMapper extends BaseMapper<JobSysUser> {

    Map<String, Object> getUser(String username, String password);
}
