package com.danusys.web.commons.auth.config;


import com.danusys.web.commons.auth.config.auth.CommonsUserDetailsService;
import com.danusys.web.commons.auth.config.security.AccessDeniedHandler;
import com.danusys.web.commons.auth.config.security.CustomAuthenticationEntryPoint;
import com.danusys.web.commons.auth.filter.JwtRequestFilter;

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

//해야할일 1. form 로그인시 처리되게
//2. 프론트에서 세션으로 jwt 저장
//3. refreshtoekn,유효기간 달아

public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final CorsConfig corsConfig;
 //  private final JwtRequestFilter jwtRequestFilter;

    private final AccessDeniedHandler accessDeniedHandler;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Value("#{'${permit.all.page.basic}'.split(',')}")
    private String[] permitAllBasic;

    @Value("#{'${permit.all.page.add}'.split(',')}")
    private String[] permitAllAdd;
    @Value("#{'${role.manager.page}'.split(',')}")
    private String[] roleManagerPage;


    private String[] permitAll=null;
    @Value("#{'${role.admin.page}'.split(',')}")
    private String[] roleAdminPage;


    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }




//    @Bean
//    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
//        return new CustomAuthenticationEntryPoint("/login/error");
////    }
//    @Bean
//    public AuthenticationFailureHandler authenticationFailureHandler() {
//        return new LoginFailureHandler2("/login/error");
//    }

    @Autowired
    private CommonsUserDetailsService myUserDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService);
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        List<String> list=new ArrayList<String>();
        Collections.addAll(list,permitAllBasic);
        Collections.addAll(list,permitAllAdd);

        permitAll=list.toArray(new String[list.size()]);
        log.info("permitAll={}",permitAll);
        httpSecurity
                .addFilter(corsConfig.corsFilter()) //corsconfig
                .addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()       //서버에 인증정보를 보관하지 않기때문에 불필요
                .authorizeRequests() //시큐리티 처리에 HttpServletRequest를 이용한다
                .antMatchers("/**").permitAll()
//                .antMatchers(permitAll).permitAll()
                .antMatchers(roleManagerPage).access("hasRole('ROLE_MANAGER')")
                .antMatchers(roleAdminPage).access("hasRole('ROLE_ADMIN')")
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
/*
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
*/

	/*
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
*/

/*
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new LoginSuccessHandler("/home");
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new PrincipalDetailsService());
    }

    // Expose authentication manager bean
    @Override @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

*/


/*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.addFilterBefore(new MyFilter1(),BasicAuthenticationFilter.class);//시작되기전에나 시작된후에 걸어ㅓ야된다
        //basicAuthenticactionFitler 가 실행되기 전에 건다
        // CSRF 설정 Disable
       // http.addFilterBefore(new JwtAuthenticationFilter(authen ticationManagerBean()), UsernamePasswordAuthenticationFilter.class);
        http.csrf().disable();
        //sateless로 세션안쓰겠다
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//session 방식을 쓰지않는다 statteless 방식 bearer방식을 쓰려고 비활성화
                .and()
                .addFilter(corsFilter) //cors 관련 다해결 @corssorigin 인증x일때 문제 ,시큐리티 필터에 등록 인증
                .formLogin().disable() //폼로그인 사용안함
                .httpBasic().disable() //bearer방식을 쓰려고 비활성화
                .addFilter(new JwtAuthenticationFilter(authenticationManagerBean()))

                  //formlogin이 비활성화됫기때문에 직접 필터 입력 AuthenticationManager를 넘겨줘야됨
                                                                                        // authenticationManager() 는 WebSecurityConfigurerAdapter 안에있음
                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasRole('Role_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRole('ROLE_ADMIN')").anyRequest().permitAll();



/*
                // exception handling 할 때 사용 할 클래스를 추가
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 시큐리티는 기본적으로 세션을 사용
                // 여기서는 세션을 사용하지 않기 때문에 세션 설정을 Stateless 로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)


                // 로그인, 회원가입 API 는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정
                .and()
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()   // 나머지 API 는 전부 인증 필요

                // JwtFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스를 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));

 */
/*

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
                        , "/mntr/**"
						, "/webjars/**" ).permitAll()	//매칭되는것만 허용
				.anyRequest().authenticated()		//모든요청허용
				.and()
				.formLogin()
				.loginPage("/login/loginPage")
				.usernameParameter("id")
				.passwordParameter("pwd")
				.loginProcessingUrl("/login/login")
				.successHandler(authenticationSuccessHandler())
				.permitAll();




    }
     */



}



