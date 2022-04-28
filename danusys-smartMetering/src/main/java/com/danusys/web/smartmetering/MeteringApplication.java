package com.danusys.web.smartmetering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ImportResource(value= {
        "classpath:spring/context-common.xml",
   //     "classpath:spring/context-security.xml",
  //      "classpath:spring/context-transaction.xml"
})
@ComponentScan("com.danusys.web")
@EntityScan("com.danusys.web")
@EnableJpaRepositories("com.danusys.web")
@EnableZuulProxy
public class MeteringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeteringApplication.class, args);
    }

}
