//package com.danusys.web.commons.auth.controller;
//
//import com.danusys.web.commons.auth.model.User;
//import com.danusys.web.commons.auth.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//
//@RestController
//@RequiredArgsConstructor
//public class JoinController {
//
//    private final UserRepository userRepository;
//
//    @RequestMapping("/hi")
//    public String hi(HttpServletRequest request){
//        String test=request.getHeader("Authorization");
//    return  test;
//    }
//
//
//
//    @RequestMapping("/tokenTest")
//    public ModelAndView hitest2(){
//        ModelAndView mav =new ModelAndView();
//        mav.setViewName("view/login/test2.html");
//        return mav;
//    }
//    @RequestMapping("/hitest3")
//    public ModelAndView hitest3(){
//        ModelAndView mav =new ModelAndView();
//        mav.setViewName("view/login/test3.html");
//        return mav;
//    }
////    @PostMapping("/join")
////    public String join(@RequestBody User user){
////        user.setUserId(user.getUserId());
////        user.setPassword(user.getPassword());
////        userRepository.save(user);
////        return "hi";
////    }
//}
