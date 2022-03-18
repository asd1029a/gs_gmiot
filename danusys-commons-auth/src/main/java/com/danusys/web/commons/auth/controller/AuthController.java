package com.danusys.web.commons.auth.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetailsService;
import com.danusys.web.commons.auth.model.*;
import com.danusys.web.commons.auth.service.PermitService;
import com.danusys.web.commons.auth.service.UserInGroupService;
import com.danusys.web.commons.auth.service.UserGroupService;
import com.danusys.web.commons.auth.service.user.UserService;
import com.danusys.web.commons.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    private final UserService userService;

    private final UserGroupService userGroupService;

    private final PermitService permitService;

    private final UserInGroupService userInGroupService;

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

    @PostMapping("/user")
    public ResponseEntity<?> saveUser(User user) {
        userService.saveUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("created id");
    }

    @PostMapping("/userGroup")
    public ResponseEntity<?> saveUserGroup(UserGroup usergroup) {
        userGroupService.saveUserGroup(usergroup);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("created userGroup");
    }


    @GetMapping("/user/{username}")
    public ResponseEntity<?> findUser(@PathVariable String username) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.get(username));
    }

    @PostMapping("/permit")
    public ResponseEntity<?> savePermit(Permit permit) {
        permitService.savePermit(permit);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("created permit");
    }

//    @PostMapping("/usergroupinuser")
//    public ResponseEntity<?> saveUserGroupInUser(UserGroupInUser userGroupInUser, int userSeq) {
//        userGroupInUserService.saveUserGroupInUser(userGroupInUser, userSeq);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body("created usergroupinuser");
//    }


    @PostMapping("/generateToken")
    //public ResponseEntity<?> createAuthenticationToken(@RequestBody User user) throws Exception{
    public ResponseEntity<?> createAuthenticationToken(User user) throws Exception {
      //  log.info("user={}", user);
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserId(), user.getPassword()));

        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);

        } catch (NullPointerException e2) {


            // httpServletRequest.getRequestDispatcher("/login/error").forward(httpServletRequest,httpServletResponse);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserId());
        //userDetails가 잘못들어왔을대 에러페이지 관리해야됨


        final TokenDto jwt = jwtUtil.generateToken(userDetails);
        userService.updateUser(user.getUserId(), jwt.getRefreshToken());


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

        userService.updateUser(username, jwt.getRefreshToken());
        return ResponseEntity.ok(new AuthenticationResponse(jwt.getAccessToken()));

    }


}
