package com.danusys.web.commons.sqlconverter.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        entityManagerFactoryRef = "oracleEntityManagerFactory",
        transactionManagerRef = "oracleTransactionManager",
        basePackages = {"com.danusys.web.commons.sqlconverter.repository.oracle"})
@MapperScan(
        sqlSessionTemplateRef = "oracleSqlSessionTemplate",
        basePackages = {"com.danusys.web.commons.sqlconverter.dao.oracle"}
)
public class OracleDataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.oracle")
    public DataSourceProperties oracleProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource oracleDataSource() {
        return oracleProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean oracleEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> oracleJpaProperties = new HashMap<>();
        oracleJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
        oracleJpaProperties.put("hibernate.hbm2ddl.auto", "none");

        return builder
                .dataSource(oracleDataSource())
                .packages("com.danusys.web.commons.sqlconverter.model.oracle")
                //.persistenceUnit("119_gis")
                .persistenceUnit("oracle")
                .properties(oracleJpaProperties)
                .build();
    }

    @Bean
    public PlatformTransactionManager oracleTransactionManager(@Qualifier("oracleEntityManagerFactory") EntityManagerFactory oracleEntityManagerFactory) {
        return new JpaTransactionManager(oracleEntityManagerFactory);
    }

    @Bean
    public SqlSessionFactory oracleSessionFactory(DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        //bean.setMapperLocations(applicationContext.getResource("classpath:/mapper/oracle/*.xml"));
        bean.setDataSource(oracleDataSource());
        return bean.getObject();
    }

    @Bean
    public SqlSessionTemplate oracleSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
