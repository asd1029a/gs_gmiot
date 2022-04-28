package com.danusys.web.commons.auth.token.config;


import com.danusys.web.commons.auth.config.CorsConfig;
import com.danusys.web.commons.auth.config.DefaultPasswordEncoderFactories;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetailsService;
import com.danusys.web.commons.auth.token.filter.JwtRequestFilter;

import com.danusys.web.commons.auth.token.config.security.AccessDeniedHandler;
import com.danusys.web.commons.auth.token.config.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.*;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : hansik.shin
 * Date : 2021/10/21
 * Time : 16:10
 */
@Slf4j
@RequiredArgsConstructor        //di
@Configuration
@EnableWebSecurity        //기본 보안설정


//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)    //secure anotation 사용 가능 preAuthorize ,postAuthorize도  어노테이션 활성화
//@Secured("ROLE_ADMIN")
//@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('


public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final CorsConfig corsConfig;

    private final AccessDeniedHandler accessDeniedHandler;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;


    @Value("#{'${permit.all.page.basic}'.split(',')}")
    private String[] permitAllBasic;

    @Value("#{'${permit.all.page.add}'.split(',')}")
    private String[] permitAllAdd;

    private String[] permitAll = null;


    @Value("#{'${role.admin.page}'.split(',')}")
    private String[] roleAdminPage;
    @Value("#{'${role.manager.page}'.split(',')}")
    private String[] roleManagerPage;
    @Value("#{'${role.menu.page}'.split(',')}")
    private String[] roleMenuPage;

    private String[] permitMenuPage = null;



    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }


    private final CommonsUserDetailsService myUserDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService);
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        List<String> list = new ArrayList<String>();
        Collections.addAll(list, permitAllBasic);
        Collections.addAll(list, permitAllAdd);

        permitAll = list.toArray(new String[list.size()]);
        log.info("permitAll={}", permitAll);
        //TODO permit group 바뀐 부분 적용f
        //TODO db 하나 새로파서 개발용으로 한벌 더 작성
        httpSecurity
                .addFilter(corsConfig.corsFilter()) //corsconfig
                .addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();       //서버에 인증정보를 보관하지 않기때문에 불필요

        for (String str : roleMenuPage){
            String permitMenu = str.split("-")[0];
            String url = str. split("-")[1];
            String[] roles = {permitMenu + "_rw", permitMenu + "_r-"};
            httpSecurity.authorizeRequests()
                .antMatchers(url).hasAnyRole(roles);
        }

        httpSecurity
                .authorizeRequests() //시큐리티 처리에 HttpServletRequest를 이용한다
                .antMatchers(permitAll).permitAll()
                .antMatchers(roleAdminPage).access("hasRole('ROLE_ADMIN')")
                .antMatchers(roleManagerPage).access("hasRole('ROLE_MANAGER')")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }


    //encorder


    @Bean
    public PasswordEncoder delegatingPasswordEncoder() {
        return DefaultPasswordEncoderFactories.getInstance().createDelegatingPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}



