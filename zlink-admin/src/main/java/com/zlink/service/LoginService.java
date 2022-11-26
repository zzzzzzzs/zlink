package com.zlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlink.dao.LoginMapper;
import com.zlink.entity.JobSysUser;
import com.zlink.model.ApiResponse;
import com.zlink.model.Status;
import com.zlink.utils.JacksonObject;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
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
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            currentUser.login(token);
            return ApiResponse.ofSuccess("success");
        } catch (AuthenticationException e) {
            return ApiResponse.ofStatus(Status.FAIL, e);
        }
    }


    /**
     * 根据用户名和密码查询对应的用户
     */
    public Map<String, Object> getUser(String username, String password) {
        return loginMapper.getUser(username, password);
    }

}
