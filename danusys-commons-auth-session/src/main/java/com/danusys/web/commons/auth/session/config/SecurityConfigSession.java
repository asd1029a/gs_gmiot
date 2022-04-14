package com.danusys.web.commons.auth.session.config;

import com.danusys.web.commons.auth.session.config.auth.CommonsUserDetailsService;
import com.danusys.web.commons.auth.session.config.security.LoginFailureHandler;
import com.danusys.web.commons.auth.session.config.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : hansik.shin
 * Date : 2021/10/21
 * Time : 16:10
 */
@RequiredArgsConstructor
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfigSession extends WebSecurityConfigurerAdapter {

    private final CommonsUserDetailsService commonsUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return DefaultPasswordEncoderFactories.getInstance().createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new LoginFailureHandler("/admin/loginForm.do");
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() throws Exception {
        return new LoginSuccessHandler("/dashboard/index.do");
    }


    @Value("#{'${permit.all.page.basic}'.split(',')}")
    private String[] permitAllBasic;

    @Value("#{'${permit.all.page.add}'.split(',')}")
    private String[] permitAllAdd;

    private String[] permitAll = null;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(commonsUserDetailsService).passwordEncoder(passwordEncoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        List<String> list = new ArrayList<String>();
        Collections.addAll(list, permitAllBasic);
        Collections.addAll(list, permitAllAdd);

        permitAll = list.toArray(new String[list.size()]);
        log.info("permitAll={}", permitAll);

        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers(permitAll).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(authenticationSuccessHandler())
                    .failureHandler(authenticationFailureHandler())
                    .permitAll();

    }
    }

