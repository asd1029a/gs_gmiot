package com.danusys.web.commons.auth.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetailsService;
import com.danusys.web.commons.auth.model.AuthenticationResponse;
import com.danusys.web.commons.auth.model.TokenDto;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.service.UserService;
import com.danusys.web.commons.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;


    private final UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CommonsUserDetailsService userDetailsService;

    @RequestMapping("/hi4")
    public ModelAndView hi4() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/view/login/login.html");
        return mv;
    }


    @PostMapping("/generateToken")
    //public ResponseEntity<?> createAuthenticationToken(@RequestBody User user) throws Exception{
    public ResponseEntity<?> createAuthenticationToken(User user) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())

            );

        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);

        }catch(NullPointerException e2){
            throw new Exception("null pointException",e2);
        }


        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        //userDetails가 잘못들어왔을대 에러페이지 관리해야됨


        final TokenDto jwt = jwtUtil.generateToken(userDetails);
        userService.updateUser(user.getUsername(),jwt.getRefreshToken());
        return ResponseEntity.ok(new AuthenticationResponse(jwt.getAccessToken()));
    }


    @PostMapping("/regenerateToken")
    public ResponseEntity<?> RegenerateToken(HttpServletRequest request) throws Exception {


        Cookie[] cookies = request.getCookies();
        String accessToken = null;

        TokenDto jwt = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken"))
                    accessToken = cookie.getValue();

            }
        }

        String username = null;
        username = jwtUtil.extractUsername(accessToken); //토큰에서 이름추출
        log.info("username={}",username);
        User user = userService.findUser(username,"error");


        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        DecodedJWT decodedJWT = JWT.decode(accessToken);
        log.info("isTrue={}",decodedJWT.getExpiresAt());
        log.info("isTrue={}",decodedJWT.getExpiresAt().after(new Date()));

        if (decodedJWT.getExpiresAt().after(new Date())) {
                log.info("hi");
            if (jwtUtil.validateToken(user.getRefreshToken(), userDetails)) {
                jwt = jwtUtil.generateToken(userDetails);
                log.info("jwt={}",jwt);
            }
        }

        userService.updateUser(username,jwt.getRefreshToken());
        return ResponseEntity.ok(new AuthenticationResponse(jwt.getAccessToken()));

    }


}
