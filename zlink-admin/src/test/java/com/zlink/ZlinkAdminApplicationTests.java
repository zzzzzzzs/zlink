package com.zlink;

import com.zlink.metadata.driver.Driver;
import com.zlink.metadata.driver.DriverConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ServiceLoader;

@SpringBootTest
class ZlinkAdminApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void jdbcall() throws SQLException {
        String url = "jdbc:mysql://192.168.52.154:3306/zlink";
        String username = "root";
        String password = "123456";
        Connection conn = DriverManager.getConnection(url, username, password);//用参数得到连接对象
        System.out.println("连接成功！");
        System.out.println(conn);
    }

    @Test
    public void spiTest() {
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers) {
            System.out.println(driver);
        }
    }

}
