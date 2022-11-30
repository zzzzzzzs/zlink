package com.zlink.metadata;

import com.zlink.metadata.driver.Driver;
import org.junit.Test;

import java.util.ServiceLoader;

/**
 * @author zs
 * @date 2022/11/29
 */
public class DriverTest {
    @Test
    public void driverTest() {
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers) {
            System.out.println(driver);
        }
    }
}
