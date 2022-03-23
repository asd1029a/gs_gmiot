package com.danusys.smartmetering.config;


import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    private final ApplicationContext applicationContext;

    public DbConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

   @Bean(name = "dataSource")
   public DataSource dataSource(){
         return DataSourceBuilder
                 .create()
                 .driverClassName("org.mariadb.jdbc.Driver")
                 .url("jdbc:mariadb://danuservice2.kro.kr:41036/smartmeter")
                 .username("root")
                 .password("danu1234")
                 .build();
//       DataSourceBuilder.create().build();
   }

    @Bean(name="sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(@Autowired @Qualifier("dataSource") DataSource dataSource,
                                                   ApplicationContext applicationContext)
            throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());

        sessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mapper/config/mybatis-config.xml"));
        sessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/sql/*.xml"));
        return sessionFactoryBean.getObject();
    }


}
