package com.danusys.web.commons.auth.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source =new UrlBasedCorsConfigurationSource();
        CorsConfiguration config =new CorsConfiguration();
        config.setAllowCredentials(true);   //내 서버 응답 할 때 json을 자바스크립트에서 처리 할 수 있게 할지를 설정
        config.addAllowedOrigin("*"); //모든 ip에 응답 허용
        config.addAllowedHeader("*"); // 모든 header에 응답 허용
        config.addAllowedMethod("*"); // 모든 post,get,put,delete,patch 요청을 하용하겠다.
        source.registerCorsConfiguration("/api/**",config); // api로 오는 것들은 이 컨피그를 따라야됨
        return new CorsFilter(source);
    }
}
