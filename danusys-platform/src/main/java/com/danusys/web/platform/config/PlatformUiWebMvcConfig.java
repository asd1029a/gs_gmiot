package com.danusys.web.platform.config;

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
public class PlatformUiWebMvcConfig extends UiWebMvcConfig {

    @Autowired
    public PlatformUiWebMvcConfig(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        // 홈
        registry.addViewController("/intro/intro").setViewName("view/intro/intro");
        registry.addViewController("/login/error").setViewName("view/login/loginError");

        registry.addViewController("/pages/home").setViewName("view/pages/home");
        registry.addViewController("/pages/mntr").setViewName("view/pages/mntr");
        registry.addViewController("/pages/dashboard").setViewName("view/pages/dashboard");
        registry.addViewController("/pages/stats").setViewName("view/pages/stats");
        registry.addViewController("/pages/config").setViewName("view/pages/config");
        registry.addViewController("/pages/inqry/event1").setViewName("view/pages/inqry/event1");
        registry.addViewController("/pages/inqry/event2").setViewName("view/pages/inqry/event2");

        super.addViewControllers(registry);
    }

}
