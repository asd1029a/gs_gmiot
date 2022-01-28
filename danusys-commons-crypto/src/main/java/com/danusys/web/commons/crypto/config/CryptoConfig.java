package com.danusys.web.commons.crypto.config;

import com.danusys.web.commons.crypto.service.executor.CryptoExecutorFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo9
 * Date : 2022/01/10
 * Time : 17:06
 */
@Configuration
public class CryptoConfig {
    @Bean
    public FactoryBean cryptoExecutorFactoryBean() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(CryptoExecutorFactory.class);
        return factoryBean;
    }
}
