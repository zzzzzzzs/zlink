package com.zlink.model;

import lombok.Getter;

/**
 * @author zs
 * @date 2022/11/25
 * 状态码封装
 */
@Getter
public enum Status {
    OK(200, "操作成功"),
    FAIL(400, "操作失败"),
    UNKNOWN_ERROR(500, "服务器出错啦"),
    E_400(400, "请求处理异常，请稍后再试"),
    E_500(500, "请求方式有误,请检查 GET/POST"),
    E_501(501, "请求路径不存在"),
    E_502(502, "权限不足"),
    E_503(503, "账号密码不存在"),
    E_504(504, "数据源不存在"),
    E_10008(10008, "角色删除失败,尚有用户属于此角色"),
    E_10009(10009, "账户已存在"),
    E_20011(20011, "登陆已过期,请重新登陆"),
    E_90003(90003, "缺少必填参数");

    /**
     * 状态码
     */
    private Integer code;
    /**
     * 内容
     */
    private String message;

    Status(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
