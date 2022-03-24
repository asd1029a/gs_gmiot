package com.danusys.web.commons.auth.controller;

import com.danusys.web.commons.auth.dto.request.UserRequest;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.service.UserInGroupService;
import com.danusys.web.commons.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping(value = "/user/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserInGroupService userInGroupService;

    /*
      name: idCheck
      url: /checkid/{userSeq}
      type: get
      do: 아이디 중복 체크
      return : 아이디 중복일경우 0 , 중복아닐경우 1 리턴

     */
    @GetMapping("/checkid/{userId}")
    public ResponseEntity<?> checkId(@PathVariable String userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.checkId(userId));
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
       url: /userInGroup/paging
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
       url: /userInGroup/paging
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
    public ResponseEntity<?> mod(@RequestBody UserRequest userRequest) {
        /* TODO : 트랜젝션 처리 요망 */
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> paramMap = objectMapper.convertValue(userRequest, Map.class);
        int result = userService.mod(userRequest);
        userInGroupService.delUserSeq(userRequest.getUserSeq());
        userInGroupService.add(paramMap);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    /*
       name: del
       url: /user
       type: delete
       param: User user (userSeq)
       do: userSeq로 조회하여 삭제함
     */
    @DeleteMapping()
    public ResponseEntity<?> del(@RequestBody User user) {
        userService.del(user);
        return ResponseEntity
                .status(HttpStatus.OK).body("");
    }

    @GetMapping("/userCount")
    public ResponseEntity<?> getUserCountProc() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserSize());
    }
}
