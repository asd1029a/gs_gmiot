package com.danusys.web.socket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class DanusysServerSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(DanusysServerSocketApplication.class, args);
    }

}
