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

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/swagger-ui/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/").resourceChain(false);
//
//
//    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        //swagger-ui
        //swagger-ui
        registry.addViewController("/swagger-ui/").setViewName("forward:/swagger-ui/index.html");

        // 홈
        registry.addViewController("/intro/intro").setViewName("view/intro/intro");
        registry.addViewController("/login/error").setViewName("view/login/loginError");

        // 관제
        registry.addViewController("/pages/mntr").setViewName("view/pages/mntr");

        // 대시보드
        registry.addViewController("/pages/dashboard").setViewName("view/pages/dashboard");

        // 통계
        registry.addViewController("/pages/stats/statistics_01").setViewName("view/pages/stats/statistics_01");
        registry.addViewController("/pages/stats/statistics_02").setViewName("view/pages/stats/statistics_02");
        registry.addViewController("/pages/stats/statistics_03").setViewName("view/pages/stats/statistics_03");
        registry.addViewController("/pages/stats/statistics_04").setViewName("view/pages/stats/statistics_04");

        // 환경설정
        registry.addViewController("/pages/config/commonCode").setViewName("view/pages/config/commonCode");
        registry.addViewController("/pages/config/dimmingSet").setViewName("view/pages/config/dimmingSet");
        registry.addViewController("/pages/config/dimmingGroup").setViewName("view/pages/config/dimmingGroup");
        registry.addViewController("/pages/config/notice").setViewName("view/pages/config/notice");
        registry.addViewController("/pages/config/signage").setViewName("view/pages/config/signage");

        registry.addViewController("/paging").setViewName("view/paging");

        registry.addViewController("/pages/config/userAccount").setViewName("view/pages/config/userAccount");
        registry.addViewController("/pages/config/userGroup").setViewName("view/pages/config/userGroup");


        // 조회관리
        registry.addViewController("/pages/inqry/event1").setViewName("view/pages/inqry/event1");
        registry.addViewController("/pages/inqry/event2").setViewName("view/pages/inqry/event2");
        registry.addViewController("/pages/inqry/station").setViewName("view/pages/inqry/station");
        registry.addViewController("/pages/inqry/facilities").setViewName("view/pages/inqry/facilities");
        registry.addViewController("/pages/inqry/eventCabinet").setViewName("view/pages/inqry/eventCabinet");
        registry.addViewController("/pages/inqry/eventDron").setViewName("view/pages/inqry/eventDron");
        registry.addViewController("/pages/inqry/faceDetection").setViewName("view/pages/inqry/faceDetection");

        super.addViewControllers(registry);
    }

}
