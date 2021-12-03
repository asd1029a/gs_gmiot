package com.danusys.web.commons.auth.controller;

import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final UserRepository userRepository;

    @RequestMapping("/hi")
    public String hi(){
    return  "hi";
    }


    @RequestMapping("/hitest")
    public ModelAndView hitest(){
        ModelAndView mav =new ModelAndView();
        mav.setViewName("view/login/test.html");
        return mav;
    }
    @PostMapping("/join")
    public String join(@RequestBody User user){
        user.setUsername(user.getUsername());
        user.setPassword(user.getPassword());
        userRepository.save(user);
        return "hi";
    }
}
