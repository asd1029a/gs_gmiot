package com.danusys.web.commons.auth.controller;

import com.danusys.web.commons.app.FileUtil;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

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
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.add(paramMap));
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
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.mod(paramMap));
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

    @ResponseBody
    @PostMapping(value = "excel/download")
    public void exportNotice(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        Map<String, Object> dataMap = userService.getList((Map<String, Object>) paramMap.get("search"));

        paramMap.put("dataMap", dataMap.get("data"));
//        log.info("dataList = {}", dataMap.get("data"));
        Workbook wb = FileUtil.excelDownload(paramMap) ;
        wb.write(response.getOutputStream());
        wb.close();
    }
}
