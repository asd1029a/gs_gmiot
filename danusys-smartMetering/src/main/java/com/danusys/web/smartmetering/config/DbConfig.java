package com.danusys.web.smartmetering.config;


import com.danusys.web.smartmetering.common.view.DownloadView;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource({"classpath:/application-local.properties"})
public class DbConfig {

    private final ApplicationContext applicationContext;

    public DbConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Value("${spring.datasource.username}") String username;
    @Value("${spring.datasource.password}") String password;
    @Value("${spring.datasource.url}")  String url;
    @Value("${spring.datasource.driver-class-name}")  String driverClass;
    @Value("${mybatis.mapper-locations}")  String mapperLocation;
    @Value("${mybatis.config-location}")  String configLocation;

    @Bean(name = "dataSource")
    public DataSource datasource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name="sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(@Autowired @Qualifier("dataSource") DataSource dataSource,
                                                   ApplicationContext applicationContext)
            throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setConfigLocation(applicationContext.getResource(configLocation));
        sessionFactoryBean.setMapperLocations(applicationContext.getResources(mapperLocation));

        return sessionFactoryBean.getObject();
    }

    @Bean // 엑셀 downloadView 빈 등록
    public DownloadView downloadView(){
        DownloadView downloadView = new DownloadView();
        return downloadView;
    }

}
