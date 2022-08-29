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
        entityManagerFactoryRef = "postgresqlEntityManagerFactory",
        transactionManagerRef = "postgresqlTransactionManager",
        basePackages = {"com.danusys.web.commons.sqlconverter.repository.postgresql"})
public class PostgresqlJpaConfig {
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.postgresql")
    public DataSourceProperties postgresqlProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource postgresqlDataSource() {
        return postgresqlProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean postgresqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> postgresqlJpaProperties = new HashMap<>();
        postgresqlJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
        postgresqlJpaProperties.put("hibernate.hbm2ddl.auto", "none");

        return builder
                .dataSource(postgresqlDataSource())
                .packages("com.danusys.web.commons.sqlconverter.model.postgresql")
                //.persistenceUnit("119_gis")
                .persistenceUnit("postgresql")
                .properties(postgresqlJpaProperties)
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager postgresqlTransactionManager(@Qualifier("postgresqlEntityManagerFactory") EntityManagerFactory postgresqlEntityManagerFactory) {
        return new JpaTransactionManager(postgresqlEntityManagerFactory);
    }
}
