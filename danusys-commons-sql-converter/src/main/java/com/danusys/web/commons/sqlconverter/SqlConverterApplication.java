package com.danusys.web.commons.sqlconverter;

import com.danusys.web.commons.sqlconverter.repository.mariadb.ErssEmerhydPRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.danusys.web")
@EntityScan("com.danusys.web")
//@EnableJpaRepositories("com.danusys.web.commons.sqlconverter")
@EnableScheduling
@Slf4j
public class SqlConverterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqlConverterApplication.class, args);
    }

}
