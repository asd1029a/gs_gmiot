package com.danusys.web.commons.auth.controller;

<<<<<<< HEAD:danusys-commons-auth/src/main/java/com/danusys/web/commons/auth/controller/UserController.java
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.service.UserInGroupService;
import com.danusys.web.commons.auth.service.UserService;
=======
import com.danusys.web.commons.auth.session.model.User;
import com.danusys.web.commons.auth.session.service.UserInGroupService;
import com.danusys.web.commons.auth.session.service.UserService;
>>>>>>> 1e0a893bc70128278dfd3c79c3fd64c78411661c:danusys-commons-auth/src/main/java/com/danusys/web/commons/auth/session/controller/UserController.java
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserInGroupService userInGroupService;

    /*
      name: idCheck
      url: /user/checkId/{userSeq}
      type: get
      do: 아이디 중복 체크
      return : 아이디 중복일경우 0 , 중복아닐경우 1 리턴

     */
    @GetMapping("/checkId/{userId}")
    public ResponseEntity<?> checkId(@PathVariable String userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.checkId(userId));
    }

    @GetMapping("/check/authority/{authName}/{permit}")
    public ResponseEntity<?> checkAuthority(@PathVariable String authName, @PathVariable String permit) {
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.checkAuthority(authName, permit));
    }
    /*
       name: get
       url: /user/{userSeq}
       type: get
       do: userSeq로 단건 조회
       return : 조회 결과
     */
    @GetMapping("/{userSeq}")
    public ResponseEntity<?> get(@PathVariable int userSeq) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.get(userSeq));
    }

    /*
       name: getList
       url: /user
       type: post
       param: Map<String ,Object> paramMap
       do: paramMap 조건에 맞는 유저 리스트 조회
       return : 조건에 맞는 리스트
     */
    @PostMapping()
    public ResponseEntity<?> getList(@RequestBody Map<String, Object> paramMap) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getList(paramMap));
    }

    /*
       name: getListUserPagingProc
       url: /user/paging
       type: post
       param: Map<String ,Object> paramMap
       do: paramMap 조건에 맞는 유저 리스트 조회
       return : 조건에 맞는 리스트
     */
    @PostMapping("/paging")
    public ResponseEntity<?> getListPaging(@RequestBody Map<String, Object> paramMap) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getListPaging(paramMap));
    }
    /*
       name: getListGroupInUser
       url: /user/userInGroup
       type: post
       param : Map<String, Object> paramMap
       ex  :{
            "userSeq":1,
            "keyword":"groupb43",
        }
       do: paramMap 조건에 맞는 list 출력
       return : paramMap 조건에 맞는 list
     */
    @PostMapping("/userInGroup")
    public ResponseEntity<?> getListUserInGroup(@RequestBody Map<String, Object> paramMap) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getListGroupInUser(paramMap));
    }

    /*
       name: getListGroupInUserPaging
       url: /user/userInGroup/paging
       type: post
       param : Map<String, Object> paramMap
       ex  :{
            "userSeq":1,
            "keyword":"groupb43",
            "start":0,
            "length":15,
        }
       do: paramMap 조건에 맞는 list 출력
       return : paramMap 조건에 맞는 list
     */
    @PostMapping("/userInGroup/paging")
    public ResponseEntity<?> getListUserInGroupPaging(@RequestBody Map<String, Object> paramMap) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getListGroupInUserPaging(paramMap));
    }

    /*
      name: add
      url: /user
      type: put
      param: User user
      do: 유저 저장
      return: 저장된 userSeq, userId로 이미 가입한 회원이 있을 경우(아이디중복)  return 0
      ,id와 패스워드를 빼고 넣었을경우 return -1 ,
     */
    @PutMapping()
    public ResponseEntity<?> add(@RequestBody Map<String, Object> paramMap) {
        /* TODO : 트랜젝션 처리 요망 */
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.convertValue(paramMap, User.class);

        int result = userService.add(user);
        paramMap.put("userSeqList", Arrays.asList(result));
        userInGroupService.add(paramMap);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    /*
      name: mod
      url: /user
      type: patch
      param: map
      ex)
      {
        "userSeq":52,
        "password":"1324"
        }
      do: 유저 수정
      return : update된 userSeq , 잘못된 userSeq를 입력했을 경우 0 return
    */
    @PatchMapping()
    public ResponseEntity<?> mod(@RequestBody Map<String, Object> paramMap) {
        /* TODO : 트랜젝션 처리 요망 */
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.convertValue(paramMap, User.class);

        int result = userService.mod(user);
        userInGroupService.delUserSeq(user.getUserSeq());
        userInGroupService.add(paramMap);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    /*
       name: del
       url: /user/{userSeq}
       type: delete
       do: userSeq로 조회하여 삭제함
     */
    @DeleteMapping("/{userSeq}")
    public ResponseEntity<?> del(@PathVariable int userSeq) {
        User user = new User();
        user.setUserSeq(userSeq);
        userService.del(user);
        return ResponseEntity
                .status(HttpStatus.OK).body("");
    }

    @GetMapping("/userCount")
    public ResponseEntity<?> getUserCountProc() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserSize());
    }
}
