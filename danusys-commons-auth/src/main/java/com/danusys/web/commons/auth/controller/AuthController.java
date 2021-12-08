package com.danusys.web.commons.auth.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetailsService;
import com.danusys.web.commons.auth.model.AuthenticationResponse;
import com.danusys.web.commons.auth.model.TokenDto;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CommonsUserDetailsService userDetailsService;

    @RequestMapping("/hi4")
    public ModelAndView hi4(){
        ModelAndView mv =new ModelAndView();
        mv.setViewName("/view/login/login.html");
        return mv;
    }


    @PostMapping("/generateToken")
    //public ResponseEntity<?> createAuthenticationToken(@RequestBody User user) throws Exception{
    public ResponseEntity<?> createAuthenticationToken(User user) throws Exception{
        // requestbody일경우 폼태그가 먹지않음 그래서 폼태그에서입력받은값을 json으로 변경해야됨
        log.info("user={},{}",user.getUsername(),user.getPassword());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())

            );
        }
        catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getUsername());

        final TokenDto jwt = jwtUtil.generateToken(userDetails);
        log.info("jwt={}",jwt);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/regenerateToken")
    public ResponseEntity<?> RegenerateToken(HttpServletRequest request) throws Exception{

        Cookie []cookies=request.getCookies();
        String accessToken=null;
        String refreshToken=null;
        TokenDto jwt=null;
        if(cookies!= null){
            for(Cookie cookie : cookies){
                // log.info(cookie.getName());
                //   log.info(cookie.getValue());
                if(cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();

                    //log.info("cookie.getValue()={}",authorizationHeader.substring(7));
                }

                if(cookie.getName().equals("refreshToken"))
                    refreshToken= cookie.getValue();

            }
        }
        String username=null;
        username=jwtUtil.extractUsername(accessToken);

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);


        DecodedJWT decodedJWT = JWT.decode(accessToken);

        //log.info("abc={}",abc.getExpiresAt());
        //if(jwtUtil.isTokenExpired(authorizationHeader.substring(7)) ){

            if(decodedJWT.getExpiresAt().before(new Date())){
                //      log.info("-----------");
                //    log.info("refreshToken={}",refreshToken);
                //  log.info("getUsername={}",userDetails.getUsername());
                if(jwtUtil.validateToken( refreshToken,userDetails)){
                    //3.발급
                    //accessToken=jwtUtil.generateToken(userDetails).getAccessToken();
                    jwt = jwtUtil.generateToken(userDetails);

                    //  log.info("new jwt ={}",jwt);

                }
            }



        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }



}
