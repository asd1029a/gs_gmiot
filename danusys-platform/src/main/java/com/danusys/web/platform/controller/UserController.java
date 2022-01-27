package com.danusys.web.platform.controller;

import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.User;


import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupInUser;
import com.danusys.web.platform.service.user.UserService2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService2 userService;

//    @PostMapping("/getList.ado")
//    public List<HashMap<String,Object>> getListUser(@RequestBody Map<String, Object> paramMap) throws Exception {
//        return userService.getListUser(paramMap);
//    }

    @PostMapping("")
    public ResponseEntity<?> addUserProc(User user){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                        .body(userService.saveUser(user));

    }

    @PatchMapping("")
    public ResponseEntity<?> modProc(User user){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.updateUser(user));

    }

//    @DeleteMapping("")
//    public ResponseEntity<?> delProc(User user){
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(userService.deleteUser(user));
//
//    }



//    @PostMapping("/usergroup")
//    public ResponseEntity<?> saveUserGroup(UserGroup usergroup) {
//        userGroupService.saveUserGroup(usergroup);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body("created userGroup");
//    }
//
//
//    @GetMapping("/user/{username}")
//    public ResponseEntity<?> findUser(@PathVariable String username) {
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(userService.findUser(username));
//    }
//
//    @PostMapping("/permit")
//    public ResponseEntity<?> savePermit(Permit permit) {
//        permitService.savePermit(permit);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body("created permit");
//    }
//
//    @PostMapping("/usergroupinuser")
//    public ResponseEntity<?> saveUserGroupInUser(UserGroupInUser userGroupInUser, int userSeq) {
//        userGroupInUserService.saveUserGroupInUser(userGroupInUser, userSeq);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body("created usergroupinuser");
//    }

//    @PatchMapping("/")
//    public ResponseEntity<?> modUserProc(User user){
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(userService.updateUser(user));
//
//    }





}
