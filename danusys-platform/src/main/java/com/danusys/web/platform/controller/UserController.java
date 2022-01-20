package com.danusys.web.platform.controller;

import com.danusys.web.platform.service.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    public UserController(UserService userService) { this.userService = userService; }

    private final UserService userService;

    @PostMapping("/getList.ado")
    public List<HashMap<String,Object>> getListUser(@RequestBody Map<String, Object> paramMap) throws Exception {
        return userService.getListUser(paramMap);
    }
}
