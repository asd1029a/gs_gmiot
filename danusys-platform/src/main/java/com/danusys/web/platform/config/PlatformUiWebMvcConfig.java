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

        // 메인
        registry.addViewController("/pages/home").setViewName("view/pages/home");

        // 관제
        registry.addViewController("/pages/mntr").setViewName("view/pages/mntr");

        // 대시보드
        registry.addViewController("/pages/dashboard").setViewName("view/pages/dashboard");

        // 통계
        registry.addViewController("/pages/stats").setViewName("view/pages/stats");
        
        // 환경설정
        registry.addViewController("/pages/config/commonCode").setViewName("view/pages/config/commonCode");
        registry.addViewController("/pages/config/dimming").setViewName("view/pages/config/dimming");
        registry.addViewController("/pages/config/district").setViewName("view/pages/config/district");
        registry.addViewController("/pages/config/notice").setViewName("view/pages/config/notice");
        registry.addViewController("/pages/config/userAccount").setViewName("view/pages/config/userAccount");
        registry.addViewController("/pages/config/userGroup").setViewName("view/pages/config/userGroup");


        // 이벤트
        registry.addViewController("/pages/inqry/event1").setViewName("view/pages/inqry/event1");
        registry.addViewController("/pages/inqry/event2").setViewName("view/pages/inqry/event2");

        super.addViewControllers(registry);
    }

}
