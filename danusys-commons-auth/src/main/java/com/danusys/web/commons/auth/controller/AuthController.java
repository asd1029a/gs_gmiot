package com.danusys.web.commons.auth.controller;

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

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private JwtUtil jwtTokenUtil;

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

        final TokenDto jwt = jwtTokenUtil.generateToken(userDetails);
        log.info("jwt={}",jwt);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }



}
