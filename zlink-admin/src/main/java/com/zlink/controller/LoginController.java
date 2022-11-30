package com.zlink.controller;


import com.zlink.common.utils.JacksonObject;
import com.zlink.model.ApiResponse;
import com.zlink.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 运营后台用户表 前端控制器
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@CrossOrigin
public class LoginController {
    private final LoginService loginService;
    /**
     * 登录
     */
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ApiResponse authLogin(@RequestBody JacksonObject requestJson) {
        return loginService.authLogin(requestJson);
    }

}

