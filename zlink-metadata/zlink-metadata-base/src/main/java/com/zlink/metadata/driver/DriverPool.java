package com.zlink.metadata.driver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zs
 * @date 2022/11/29
 */
public class DriverPool {

    private static volatile Map<String, Driver> driverMap = new ConcurrentHashMap<>();

    public static boolean exist(String key) {
        if (driverMap.containsKey(key)) {
            return true;
        }
        return false;
    }

    public static Integer push(String key, Driver driver) {
        driverMap.put(key, driver);
        return driverMap.size();
    }

    public static Integer remove(String key) {
        driverMap.remove(key);
        return driverMap.size();
    }

    public static Driver get(String key) {
        return driverMap.get(key);
    }
}
