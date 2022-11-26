package com.zlink;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@SpringBootApplication
@EnableCaching
public class Zlink {

    public static void main(String[] args) {
        SpringApplication.run(Zlink.class, args);
    }

}
