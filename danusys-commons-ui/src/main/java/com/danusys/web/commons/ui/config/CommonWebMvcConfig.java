package com.danusys.web.commons.ui.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : hansik.shin
 * Date : 2021/10/21
 * Time : 16:10
 */
@Configuration
public class CommonWebMvcConfig extends WebMvcConfigurationSupport {

    private final ApplicationContext applicationContext;

    public CommonWebMvcConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/ui/map").setViewName("view/commons/map");

        super.addViewControllers(registry);
    }

    public ITemplateResolver templateResolver() throws Exception {
        final SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCacheable(false);
        return resolver;
    }

}
