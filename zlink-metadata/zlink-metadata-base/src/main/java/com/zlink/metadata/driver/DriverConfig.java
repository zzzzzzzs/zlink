package com.zlink.metadata.driver;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zs
 * @date 2022/11/29
 */
@Data
@NoArgsConstructor
public class DriverConfig {

    private String name;
    private String type;
    private String driverClassName;
    private String ip;
    private Integer port;
    private String url;
    private String username;
    private String password;

    public DriverConfig(String name, String type, String driverClassName, String url, String username, String password) {
        this.name = name;
        this.type = type;
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
    }
}
