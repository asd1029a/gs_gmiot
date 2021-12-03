package com.danusys.web.drone.config;

import com.danusys.web.commons.ui.config.UiWebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2021/11/30
 * Time : 17:38
 */
@Configuration
public class DroneUiWebMvcConfig extends UiWebMvcConfig {

    @Autowired
    public DroneUiWebMvcConfig(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("home").setViewName("view/pages/home");
        registry.addViewController("dashboard").setViewName("view/pages/dashboard");
        registry.addViewController("drone").setViewName("view/pages/drone");
        registry.addViewController("misn").setViewName("view/pages/misn");
        registry.addViewController("flying").setViewName("view/pages/flying");
        registry.addViewController("log").setViewName("view/pages/log");
        registry.addViewController("config").setViewName("view/pages/config");
        super.addViewControllers(registry);
    }
}