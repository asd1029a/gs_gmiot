package com.danusys.web.drone.controller.config;

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

    private final ApplicationContext applicationContext;

    @Autowired
    public DroneUiWebMvcConfig(ApplicationContext applicationContext) {
        super(applicationContext);
        this.applicationContext = applicationContext;
    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("home").setViewName("view/pages/home");
        registry.addViewController("dashboard").setViewName("view/pages/dashboard");
        registry.addViewController("drone").setViewName("view/pages/drone");
        registry.addViewController("mission").setViewName("view/pages/mission");
        registry.addViewController("flying").setViewName("view/pages/flying");
        registry.addViewController("log").setViewName("view/pages/log");
        registry.addViewController("config").setViewName("view/pages/config");


        registry.addViewController("drone/info").setViewName("view/pages/droneInfo");


        super.addViewControllers(registry);
    }
}
