package com.zlink.dao;

import com.zlink.entity.JobSysMenus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 运营后台用户表 Mapper 接口
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@Mapper
public interface MenusMapper extends BaseMapper<JobSysMenus> {

    List<JobSysMenus> listMenus();
}
