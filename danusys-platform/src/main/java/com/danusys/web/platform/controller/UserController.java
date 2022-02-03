package com.danusys.web.platform.controller;

import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.User;


import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupPermit;
import com.danusys.web.commons.auth.service.PermitService;
import com.danusys.web.commons.auth.service.UserGroupPermitService;
import com.danusys.web.commons.auth.service.UserGroupService;
import com.danusys.web.platform.service.user.UserService2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService2 userService;
    private final UserGroupService userGroupService;
    private final UserGroupPermitService userGroupPermitService;

//    @PostMapping("/getList.ado")
//    public List<HashMap<String,Object>> getListUser(@RequestBody Map<String, Object> paramMap) throws Exception {
//        return userService.getListUser(paramMap);
//    }

    @PutMapping()
    public ResponseEntity<?> addUserProc(@RequestBody User user) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.saveUser(user));

    }

    @PatchMapping()
    public ResponseEntity<?> modProc(@RequestBody User user) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.updateUser(user));

    }

    @DeleteMapping()
    public ResponseEntity<?> delProc(@RequestBody User user) {
        userService.deleteUser(user);
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }

    @GetMapping("/{userSeq}")
    public ResponseEntity<?> getUser(@PathVariable int userSeq) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findUser(userSeq));
    }


    @GetMapping("/group/{groupSeq}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getGroup(@PathVariable int groupSeq) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupService.findUserGroupByGroupSeq(groupSeq));
    }

    @PutMapping("/group")
    public ResponseEntity<?> addGroupProc(@RequestBody UserGroup userGroup) {


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userGroupService.saveUserGroup(userGroup));
    }

    @PatchMapping("/group")
    public ResponseEntity<?> modGroupProc(@RequestBody UserGroup userGroup) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupService.updateUserGroup(userGroup));

    }
    @DeleteMapping("/group")
    public ResponseEntity<?> deleteGroupProc(@RequestBody UserGroup userGroup){
        userGroupService.deleteUserGroup(userGroup);
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }


    @PutMapping("/grouppermit")
    public ResponseEntity<?> addPermitProc(@RequestBody UserGroupPermit userGroupPermit) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupPermitService.saveUserGroupPermit(userGroupPermit));
    }


//    @GetMapping("/groupinuser/{groupSeq}")
//    public ResponseEntity<?> getGroupInUser(@PathVariable int groupSeq) {
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(userGroupService.findUserGroupByGroupSeq(groupSeq));
//    }
////
//    @PutMapping("/groupinuser")
//    public ResponseEntity<?> addGroupProc(@RequestBody UserGroup userGroup) {
//
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(userGroupService.saveUserGroup(userGroup));
//    }
//
//    @PatchMapping("/groupinuser")
//    public ResponseEntity<?> modGroupProc(@RequestBody UserGroup userGroup) {
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(userGroupService.updateUserGroup(userGroup));
//
//    }
//    @DeleteMapping("/groupinuser")
//    public ResponseEntity<?> deleteGroupProc(@RequestBody UserGroup userGroup){
//
//        return ResponseEntity
//                .status(HttpStatus.OK).build();
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
