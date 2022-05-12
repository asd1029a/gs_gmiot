package com.danusys.web.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.danusys.web")
@EntityScan("com.danusys.web")
@EnableJpaRepositories("com.danusys.web")
@EnableZuulProxy
@EnableScheduling
public class PlatformApplication {
	public static void main(String[] args) {
		SpringApplication.run(PlatformApplication.class, args);
	}
}
