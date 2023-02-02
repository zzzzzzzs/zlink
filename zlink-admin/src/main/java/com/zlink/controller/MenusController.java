package com.zlink.controller;


import com.zlink.model.ApiResponse;
import com.zlink.service.MenusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 运营后台用户表 前端控制器
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@CrossOrigin
public class MenusController {

    private final MenusService menusService;


    /**
     * 查询菜单
     */
    @RequestMapping(value = "/listMenus", method = RequestMethod.GET)
    public ApiResponse listMenus() {
        return ApiResponse.ofSuccess(menusService.listMenus());
    }


}

