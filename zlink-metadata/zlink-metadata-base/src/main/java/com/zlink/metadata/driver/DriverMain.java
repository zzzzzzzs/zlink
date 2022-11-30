package com.zlink.metadata.driver;

import java.util.ServiceLoader;

/**
 * @author zs
 * @date 2022/11/29
 */
public class DriverMain {
    public static void main(String[] args) {
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers) {
            System.out.println(driver);
        }
    }
}
