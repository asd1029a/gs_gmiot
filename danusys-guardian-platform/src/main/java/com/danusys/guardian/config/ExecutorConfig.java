package com.danusys.guardian.config;

import com.danusys.guardian.service.executor.ApiExecutorFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Project : danusys-guardian-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/03
 * Time : 1:39 오후
 */
@Configuration
public class ExecutorConfig {
    @Bean
    public FactoryBean serviceLocatorFactoryBean() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(ApiExecutorFactory.class);
        return factoryBean;
    }

    @Bean
    public RestTemplate restTesmplate() {
        return new RestTemplate();
    }
}
