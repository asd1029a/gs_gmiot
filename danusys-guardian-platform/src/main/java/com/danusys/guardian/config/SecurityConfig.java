package com.danusys.guardian.config;

import com.danusys.guardian.config.security.LoginAuthenticationProvider;
import com.danusys.guardian.config.security.LoginFailureHandler;
import com.danusys.guardian.config.security.LoginSuccessHandler;
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
 * Project : danusys-guardian-parent
 * Created by IntelliJ IDEA
 * Developer : hansik.shin
 * Date : 2021/10/21
 * Time : 16:10
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		return new LoginAuthenticationProvider();
	}

	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new LoginFailureHandler("/login/error");
	}

	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new LoginSuccessHandler("/action/page?path=intro/intro");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();

		http.authorizeRequests()
				.antMatchers("/resources/**"
						, "/test/**"
						, "/login/**"
						, "/api/**"
						, "/css/**"
						, "/font/**"
						, "/images/**"
						, "/js/**"
						, "/favicon.ico"
						, "/selectNoSession/**"
						, "/file/**"
						, "/sound/**"
						, "/svg/**" ).permitAll()
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/login/loginPage")
				.usernameParameter("id")
				.passwordParameter("pwd")
				.loginProcessingUrl("/login/login")
				.failureHandler(authenticationFailureHandler())
				.successHandler(authenticationSuccessHandler())
				.permitAll();
	}
}





