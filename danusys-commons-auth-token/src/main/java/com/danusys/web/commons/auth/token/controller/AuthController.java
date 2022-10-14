package com.danusys.web.commons.auth.token.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetailsService;
import com.danusys.web.commons.auth.dto.EncryptedUser;
import com.danusys.web.commons.auth.encryption.RsaUtils;
import com.danusys.web.commons.auth.model.AuthenticationResponse;
import com.danusys.web.commons.auth.model.TokenDto;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.service.UserService;
import com.danusys.web.commons.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
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
import javax.servlet.http.HttpSession;
import java.security.PrivateKey;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

/*
    @Autowired
    private AuthenticationManager authenticationManager;
*/
    private final AuthenticationManager authenticationManager;

    private final UserService userService;

//    private final RsaUtils rsaUtils;
    private RsaUtils rsaUtils;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CommonsUserDetailsService userDetailsService;

    @RequestMapping("/hi4")
    public ModelAndView hi4() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("view/login/login.html");
        return mv;
    }

    @PostMapping("/generateToken")
    public ResponseEntity<?> createAuthenticationToken(HttpServletRequest request, EncryptedUser user) throws Exception {
      //  log.info("user={}", user);
        rsaUtils = new RsaUtils();
        PrivateKey privateKey = rsaUtils.privateKeyExtraction(request);

        String username = rsaUtils.decrypt(privateKey, user.getSecuredUsername()).orElseGet(() -> new String(""));
        String password = rsaUtils.decrypt(privateKey, user.getSecuredPassword()).orElseGet(() -> new String(""));

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        final TokenDto jwt = jwtUtil.generateToken(userDetails);
        userService.mod(username, jwt.getRefreshToken());

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
      //  log.info("username={}", username);
        User     user = userService.get(username, "error");

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        DecodedJWT decodedJWT = JWT.decode(accessToken);

        if (decodedJWT.getExpiresAt().after(new Date())) {
            if (jwtUtil.validateToken(user.getRefreshToken(), userDetails)) {
                jwt = jwtUtil.generateToken(userDetails);
            }
        }

        userService.mod(username, jwt.getRefreshToken());
        return ResponseEntity.ok(new AuthenticationResponse(jwt.getAccessToken()));
    }

}
