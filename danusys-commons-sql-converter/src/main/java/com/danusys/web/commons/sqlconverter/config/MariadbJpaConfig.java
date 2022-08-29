package com.danusys.web.commons.sqlconverter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
@EnableJpaRepositories(
        entityManagerFactoryRef = "mariadbEntityManagerFactory",
        transactionManagerRef = "mariadbTransactionManager",
        basePackages = {"com.danusys.web.commons.sqlconverter.repository.mariadb"})
public class MariadbJpaConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.mariadb")
    public DataSourceProperties mariadbProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource mariadbDataSource() {
        return mariadbProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean mariadbEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> mariadbJpaProperties = new HashMap<>();
        mariadbJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MariaDB103Dialect");
        mariadbJpaProperties.put("hibernate.hbm2ddl.auto", "none");

        return builder
                .dataSource(mariadbDataSource())
                .packages("com.danusys.web.commons.sqlconverter.model.mariadb")
                //.persistenceUnit("119_gis")
                .persistenceUnit("mariadb")
                .properties(mariadbJpaProperties)
                .build();
    }

    @Bean
    public PlatformTransactionManager mariadbTransactionManager(@Qualifier("mariadbEntityManagerFactory") EntityManagerFactory mariadbEntityManagerFactory) {
        return new JpaTransactionManager(mariadbEntityManagerFactory);
    }
}
