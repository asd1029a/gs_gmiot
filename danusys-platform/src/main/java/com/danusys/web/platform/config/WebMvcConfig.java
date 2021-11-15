package com.danusys.web.platform.config;

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
public class WebMvcConfig extends UiConfiguration {

    @Autowired
    public WebMvcConfig(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        // 홈
        registry.addViewController("/intro/intro").setViewName("view/intro/intro");
        registry.addViewController("/login/error").setViewName("view/login/loginError");

//        registry.addViewController("/event/evtMain").setViewName("view/event/evtMain");

        registry.addViewController("/set/setMain").setViewName("view/set/setMain");
        registry.addViewController("/circlr/circlrMain").setViewName("view/circlr/circlrMain");
        registry.addViewController("/event/evtMain").setViewName("view/event/evtMain");
        registry.addViewController("/exReport/exReportMain").setViewName("view/exReport/exReportMain");
        registry.addViewController("/fclt/fcltMain").setViewName("view/fclt/fcltMain");
        registry.addViewController("/oprt/oprtMain").setViewName("view/oprt/oprtMain");
        registry.addViewController("/report/reportMain").setViewName("view/report/reportMain");
        registry.addViewController("/stats/statsMain").setViewName("view/stats/statsMain");

        registry.addViewController("/oprt/oprtEventInfoBase").setViewName("view/oprt/oprtEventInfoBase");
        registry.addViewController("/oprt/oprtSysCode").setViewName("view/oprt/oprtSysCode");
        registry.addViewController("/oprt/oprtUserGrp").setViewName("view/oprt/oprtUserGrp");
        registry.addViewController("/oprt/oprtUserEventGrp").setViewName("view/oprt/oprtUserEventGrp");
        registry.addViewController("/oprt/oprtUser").setViewName("view/oprt/oprtUser");
        registry.addViewController("/oprt/oprtLinkMenu").setViewName("view/oprt/oprtLinkMenu");
        registry.addViewController("/oprt/oprtUserMenu").setViewName("view/oprt/oprtUserMenu");
        registry.addViewController("/oprt/oprtNetMapping").setViewName("view/oprt/oprtNetMapping");
        registry.addViewController("/oprt/oprtArrearsCamera").setViewName("view/oprt/oprtArrearsCamera");
        registry.addViewController("/oprt/oprtNotice").setViewName("view/oprt/oprtNotice");
        registry.addViewController("/oprt/oprtCctvViewLog").setViewName("view/oprt/oprtCctvViewLog");
        registry.addViewController("/oprt/oprtCctvCtrlLog").setViewName("view/oprt/oprtCctvCtrlLog");
        registry.addViewController("/oprt/oprtStorageStatusLog").setViewName("view/oprt/oprtStorageStatusLog");
        registry.addViewController("/oprt/oprtDisplaySet").setViewName("view/oprt/oprtDisplaySet");
        registry.addViewController("/oprt/oprtGroup").setViewName("view/oprt/oprtGroup");
        registry.addViewController("/oprt/oprtSendSms").setViewName("view/oprt/oprtSendSms");
        registry.addViewController("/oprt/oprtSendSmsLog").setViewName("view/oprt/oprtSendSmsLog");
        registry.addViewController("/oprt/oprtSendSmsLogStat").setViewName("view/oprt/oprtSendSmsLogStat");

        registry.addViewController("layout/hello").setViewName("view/hello");
        registry.addViewController("layout/hellop").setViewName("view/hellop");

        super.addViewControllers(registry);
    }

}
