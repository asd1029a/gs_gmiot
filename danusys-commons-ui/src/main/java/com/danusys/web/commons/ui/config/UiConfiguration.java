package com.danusys.web.commons.ui.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : hansik.shin
 * Date : 2021/10/21
 * Time : 16:10
 */
public abstract class UiConfiguration extends WebMvcConfigurationSupport {

    private final ApplicationContext applicationContext;

    @Autowired
    public UiConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jsonMessageConverter());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jsonMessageConverter());
    }

    private MappingJackson2HttpMessageConverter jsonMessageConverter() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());

        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    @Bean
    @Description("Thymeleaf view resolver")
    public ViewResolver viewResolver() throws Exception {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine(templateResolver()));
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }


    @Bean
    public ViewResolver javascriptViewResolver() throws Exception {
        final ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine(javascriptTemplateResolver()));
        resolver.setOrder(2);
        resolver.setContentType("application/javascript");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setViewNames(new String[] {"/**/*.js"});

        return resolver;
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

    public ITemplateResolver javascriptTemplateResolver() {
        final SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("classpath:/static");
        resolver.setCacheable(false);
        resolver.setTemplateMode(TemplateMode.JAVASCRIPT);
        resolver.setCharacterEncoding("UTF-8");

        return resolver;
    }

    public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver) throws Exception {
        final SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        engine.addDialect(new LayoutDialect());
        return engine;
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//        registry.addResourceHandler("/vendor/**").addResourceLocations("classpath:/static/vendor/");
//        registry.addResourceHandler("/media/**").addResourceLocations("classpath:/static/media/");

        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");


        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/").resourceChain(false);
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/css/").resourceChain(false);
        registry.addResourceHandler("/drone/**").addResourceLocations("classpath:/static/drone/").resourceChain(false);
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/").resourceChain(false);
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/").resourceChain(false);
        registry.addResourceHandler("/sound/**").addResourceLocations("classpath:/sound/js/").resourceChain(false);
        registry.addResourceHandler("/svg/**").addResourceLocations("classpath:/svg/js/").resourceChain(false);


        registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/")
                .resourceChain(false);
        registry.addResourceHandler("/result/**").addResourceLocations("file:///C:\\Users\\owner//dev/upload/135/");
        registry.setOrder(1);

        super.addResourceHandlers(registry);
    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/gs-guide-websocket").setViewName("/gs-guide-websocket");
    }
}
