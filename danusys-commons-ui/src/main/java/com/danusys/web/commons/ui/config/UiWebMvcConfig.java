package com.danusys.web.commons.ui.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : hansik.shin
 * Date : 2021/10/21
 * Time : 16:10
 */
@Configuration
public class UiWebMvcConfig extends UiConfiguration {
    private ApplicationContext applicationContext;

    @Autowired
    public UiWebMvcConfig(ApplicationContext applicationContext) {
        super(applicationContext);
        this.applicationContext = applicationContext;
    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        // 홈
        registry.addViewController("/ui/map").setViewName("view/commons/map");

        super.addViewControllers(registry);
    }

}
