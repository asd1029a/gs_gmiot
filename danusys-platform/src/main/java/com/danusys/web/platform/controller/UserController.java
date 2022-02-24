package com.danusys.web.platform.controller;

import com.danusys.web.commons.auth.model.*;


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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    /*
      name: addUserProc
      url: /user
      type: put
      param: User user
      do: 유저 저장
      return: 저장된 userSeq,   userId로 이미 가입한 회원이 있을 경우(아이디중복)  return 0
      ,id와 패스워드를 빼고 넣었을경우 return -1 ,
     */
    @PutMapping()
    public ResponseEntity<?> addUserProc(@RequestBody User user) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.saveUser(user));

    }
    /*
      name: idCheck
      url: /checkid/{userSeq}
      type: get
      do: 아이디 중복 체크
      return : 아이디 중복일경우 0 , 중복아닐경우 1 리턴

     */
    @GetMapping("/checkid/{userId}")
    public ResponseEntity<?> idCheck(@PathVariable String userId){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.idCheck(userId));
    }

    /*
      name: modUserProc
      url: /user
      type: patch
      param: User user
      ex)
      {
                "userSeq":52,
                "password":"1324"
        }
      do: 유저 수정
      return : update된 userSeq , 잘못된 userSeq를 입력했을 경우 0 return

    */
    @PatchMapping()
    public ResponseEntity<?> modUserProc(@RequestBody User user) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateUser(user));

    }
    /*
       name: getListUserProc
       url: /user
       type: post
       param: Map<String ,Object> paramMap
       do: paramMap 조건에 맞는 유저 리스트 조회
       return : 조건에 맞는 리스트
     */
    @PostMapping()
    public ResponseEntity<?> getListUserProc(@RequestBody Map<String, Object> paramMap) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findListUser(paramMap));
    }
    /*
       name: delUserProc
       url: /user
       type: delete
       param: User user (userSeq)
       do: userSeq로 조회하여 삭제함
     */
    @DeleteMapping()
    public ResponseEntity<?> delUserProc(@RequestBody User user) {
        userService.deleteUser(user);
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }
    /*
       name: getUserProc
       url: /user/{userSeq}
       type: get
       do: userSeq로 단건 조회
       return : 조회 결과
     */
    @GetMapping("/{userSeq}")
    public ResponseEntity<?> getUserProc(@PathVariable int userSeq) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findUser(userSeq));
    }

    /*
       name: getGroupProc
       url: /group/{groupSeq}
       type: get
       do: groupSeq로 group 단건 조회
       return : 단건 조회
     */
    @GetMapping("/group/{groupSeq}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getGroupProc(@PathVariable int groupSeq) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupService.findUserGroupResponseByGroupSeq(groupSeq));
    }
    /*
       name: getListGroupProc
       url: /group/
       type: post
       param : Map<String, Object> paramMap
       ex  :{
            "groupName":"groupb43",
            "start":0,
            "length":15,
            "draw":"1"
}
       do: paramMap 조건에 맞는 list 출력
       return : paramMap 조건에 맞는 list
     */


    @PostMapping("/group")
    public ResponseEntity<?> getListGroupProc(@RequestBody Map<String, Object> paramMap) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupService.findListGroup(paramMap));
    }
    /*
       name: addGroupProc
       url: /group/
       type: put
       param : UserGroup userGroup
       do: usergroup 저장 , groupName,groupDesc 없이 전송할경우 0 리턴
       return : 저장된 groupSeq
     */
    @PutMapping("/group")
    public ResponseEntity<?> addGroupProc(@RequestBody UserGroup userGroup) {


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userGroupService.saveUserGroup(userGroup));
    }
    /*
       name: modGroupProc
       url: /group/
       type: patch
       param : UserGroup userGroup
       do: usergroup 업데이트 
       return : 저장된 groupSeq groupSeq로 조회한 group이 없을경우 0 리턴
     */
    @PatchMapping("/group")
    public ResponseEntity<?> modGroupProc(@RequestBody UserGroup userGroup) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupService.updateUserGroup(userGroup));

    }
    /*
       name: deleteGroupProc
       url: /group/
       type: delete
       param : userSeq
       do: usergroup 삭제
     */
    @DeleteMapping("/group")
    public ResponseEntity<?> deleteGroupProc(@RequestBody UserGroup userGroup) {
        userGroupService.deleteUserGroup(userGroup);
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }
    /*
       name: getListGroupInUserProc
       url: /groupinuser/
       type: post
       param : userSeq
       do: paramMap조건에 맞는 groupinuser List 조회
       return : paramMap 조건에 맞는 userSeq, userGroupSeq, insertUserSeq, insertDt
     */
    @PostMapping("/groupinuser")
    public ResponseEntity<?> getListGroupInUserProc(@RequestBody Map<String,Object> paramMap) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupInUserService.findListGroupInUser(paramMap));
    }
  /*
       name: addGroupInUserProc
       url: /groupinuser/
       type: put
       param :Map<String,Object> paramMap
              (List<Integer> userSeqList , List<Integer> userGroupSeqList)
        ex){
                "userSeqList":[49],
             "userGroupSeqList":[7,9,10]
            }


       do: paramMap조건에 맞는 groupinuser List 조회
       return : seq List 리턴 ,이미 있을 경우 null 리턴
     */

    @PutMapping("/groupinuser")
   // public ResponseEntity<?> addGroupInUserProc(@RequestBody UserGroupInUserRequest userGroupInUserRequest) {
    public ResponseEntity<?> addGroupInUserProc(@RequestBody Map<String,Object> paramMap) {

//        log.info("usergroupInUser={}", userGroupInUserRequest);



        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userGroupInUserService.saveUserGroupInUser(paramMap));
    }

    @DeleteMapping("/groupinuser")
    public ResponseEntity<?> delGroupInUserProc(@RequestBody UserGroupInUserRequest userGroupInUserRequest) {
        userGroupInUserService.deleteUserGroupInUser(userGroupInUserRequest.getUserSeq(), userGroupInUserRequest.getUserGroupSeq());
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }

    @PutMapping("/grouppermit")
    public ResponseEntity<?> addPermitProc(@RequestBody UserGroupPermitRequest userGroupPermitRequest) {

        log.info("userGroupPermitReqeuest={}", userGroupPermitRequest);
        UserGroupPermit userGroupPermit = new UserGroupPermit();
        userGroupPermit.setInsertUserSeq(userGroupPermitRequest.getInsertUserSeq());
        userGroupPermitService.saveUserGroupPermit(userGroupPermit
                , userGroupPermitRequest.getUserGroupSeq(), userGroupPermitRequest.getPermitSeq());
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }


    @DeleteMapping("/grouppermit")
    public ResponseEntity<?> delPermitProc(@RequestBody UserGroupPermitRequest userGroupPermitRequest) {

        log.info("userGroupPermitReqeuest={}", userGroupPermitRequest);
        userGroupPermitService.deleteUserGroupPermit(
                userGroupPermitRequest.getUserGroupSeq(), userGroupPermitRequest.getPermitSeq());
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }

    @GetMapping("/usercount")
    public ResponseEntity<?> getUserCountProc(){
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUserSize());

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
//


}
