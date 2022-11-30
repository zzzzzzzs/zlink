package com.zlink;


import com.zlink.metadata.driver.Driver;
import com.zlink.metadata.driver.DriverConfig;


public class ZlinkMain {

    public static void main(String[] args) {
        Driver.get(new DriverConfig());
    }

}
