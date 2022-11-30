package com.zlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlink.common.utils.JacksonObject;
import com.zlink.dao.LoginMapper;
import com.zlink.entity.JobSysUser;
import com.zlink.model.ApiResponse;
import com.zlink.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 运营后台用户表 服务实现类
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@Service
@RequiredArgsConstructor
public class LoginService extends ServiceImpl<LoginMapper, JobSysUser> implements IService<JobSysUser> {

    private final LoginMapper loginMapper;


    /**
     * 登录
     */
    public ApiResponse authLogin(JacksonObject requestJson) {
        String username = requestJson.getString("username");
        String password = requestJson.getString("password");
        Map<String, Object> user = getUser(username, password);
        if (user != null) {
            return ApiResponse.ofSuccess("success");
        }
        return ApiResponse.ofStatus(Status.E_503);
    }


    /**
     * 根据用户名和密码查询对应的用户
     */
    public Map<String, Object> getUser(String username, String password) {
        return loginMapper.getUser(username, password);
    }

}
