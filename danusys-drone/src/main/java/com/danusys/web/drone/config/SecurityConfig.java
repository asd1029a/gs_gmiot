package com.danusys.web.drone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2021/11/30
 * Time : 17:17
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/resources/**"
                        , "/test/**"
                        , "/login/**"
                        , "/api/**"
                        , "/aepel/**"
                        , "/css/**"
                        , "/font/**"
                        , "/images/**"
                        , "/js/**"
                        , "/favicon.ico"
                        , "/selectNoSession/**"
                        , "/file/**"
                        , "/sound/**"
                        , "/svg/**"
                        , "/ui/**"
                        , "/flying"
                        , "/drone"
                        , "/home"
                        , "/dashboard"
                        , "/misn"
                        , "/log"
                        , "/config"
                        , "/webjars/**" ).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login/loginPage")
                .usernameParameter("id")
                .passwordParameter("pwd")
                .loginProcessingUrl("/login/login")
                .permitAll();
    }
}
