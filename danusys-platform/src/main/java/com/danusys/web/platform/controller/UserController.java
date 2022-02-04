package com.danusys.web.platform.controller;

import com.danusys.web.commons.auth.model.*;


import com.danusys.web.commons.auth.service.PermitService;
import com.danusys.web.commons.auth.service.UserGroupInUserService;
import com.danusys.web.commons.auth.service.UserGroupPermitService;
import com.danusys.web.commons.auth.service.UserGroupService;
import com.danusys.web.platform.dto.request.UserGroupInUserRequest;
import com.danusys.web.platform.dto.request.UserGroupPermitRequest;
import com.danusys.web.platform.service.user.UserService2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PreUpdate;
import java.security.Principal;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService2 userService;
    private final UserGroupService userGroupService;
    private final UserGroupPermitService userGroupPermitService;
    private final UserGroupInUserService userGroupInUserService;

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
                .status(HttpStatus.OK)
                .body(userService.updateUser(user));

    }

    @PostMapping()
    public ResponseEntity<?> getListProc(){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findListUser());
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

    @PostMapping("/group")
    public ResponseEntity<?> getListGroup(){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupService.findListGroup());
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
    public ResponseEntity<?> deleteGroupProc(@RequestBody UserGroup userGroup) {
        userGroupService.deleteUserGroup(userGroup);
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }

    @PostMapping("/groupinuser")
    public ResponseEntity<?> getListGroupInUserProc() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupInUserService.findListGroupInUser());
    }




    @PutMapping("/groupinuser")
    public ResponseEntity<?> addGroupInUserProc(@RequestBody UserGroupInUserRequest userGroupInUserRequest) {

        log.info("usergroupInUser={}", userGroupInUserRequest);
        UserGroupInUser userGroupInUser = new UserGroupInUser();
        userGroupInUser.setInsertUserSeq(userGroupInUserRequest.getInsertUserSeq());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userGroupInUserService.saveUserGroupInUser(userGroupInUser,
                        userGroupInUserRequest.getUserSeq(), userGroupInUserRequest.getUserGroupSeq()));
    }

    @DeleteMapping("/groupinuser")
    public ResponseEntity<?> delGroupInUserProc(@RequestBody UserGroupInUserRequest userGroupInUserRequest){
        userGroupInUserService.deleteUserGroupInUser(userGroupInUserRequest.getUserSeq(),userGroupInUserRequest.getUserGroupSeq());
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }

    @PutMapping("/grouppermit")
    public ResponseEntity<?> addPermitProc(@RequestBody UserGroupPermitRequest userGroupPermitRequest) {

        log.info("userGroupPermitReqeuest={}",userGroupPermitRequest);
        UserGroupPermit userGroupPermit =new UserGroupPermit();
        userGroupPermit.setInsertUserSeq(userGroupPermitRequest.getInsertUserSeq());
        userGroupPermitService.saveUserGroupPermit(userGroupPermit
                ,userGroupPermitRequest.getUserGroupSeq(),userGroupPermitRequest.getPermitSeq());
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }


    @DeleteMapping("/grouppermit")
    public ResponseEntity<?> delPermitProc(@RequestBody UserGroupPermitRequest userGroupPermitRequest) {

        log.info("userGroupPermitReqeuest={}",userGroupPermitRequest);
        userGroupPermitService.deleteUserGroupPermit(
                userGroupPermitRequest.getUserGroupSeq(),userGroupPermitRequest.getPermitSeq());
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }



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
