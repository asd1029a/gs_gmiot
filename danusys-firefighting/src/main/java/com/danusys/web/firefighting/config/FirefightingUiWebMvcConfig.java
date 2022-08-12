package com.danusys.web.firefighting.config;

import com.danusys.web.commons.ui.config.UiWebMvcConfig;
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
public class FirefightingUiWebMvcConfig extends UiWebMvcConfig {

    @Autowired
    public FirefightingUiWebMvcConfig(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        //swagger-ui
//        registry.addViewController("/swagger-ui/").setViewName("forward:/swagger-ui/index.html");

        // 홈
        registry.addViewController("/main").setViewName("view/pages/main");
//        registry.addViewController("/login/error").setViewName("view/login/loginError");

        registry.addViewController("/pages/config/userAccount").setViewName("view/pages/config/userAccount");
        registry.addViewController("/pages/config/userGroup").setViewName("view/pages/config/userGroup");

        super.addViewControllers(registry);
    }

}
