package com.danusys.web.firefighting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.danusys.web")
@EntityScan("com.danusys.web")
@EnableJpaRepositories("com.danusys.web")
public class FirefightingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirefightingApplication.class, args);
    }

}
