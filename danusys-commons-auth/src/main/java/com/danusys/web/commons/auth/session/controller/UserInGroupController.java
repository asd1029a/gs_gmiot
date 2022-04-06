package com.danusys.web.commons.auth.session.controller;

import com.danusys.web.commons.auth.session.dto.request.UserInGroupRequest;
import com.danusys.web.commons.auth.session.service.UserInGroupService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/userInGroup")
@RequiredArgsConstructor
@Slf4j
public class UserInGroupController {

    private final UserInGroupService userInGroupService;

    /*
       name: getListGroupInUserProc
       url: /userInGroup
       type: post
       param : userSeq
       do: paramMap조건에 맞는 userInGroup List 조회
       return : paramMap 조건에 맞는 userSeq, userGroupSeq, insertUserSeq, insertDt
     */
    @SneakyThrows
    @PostMapping()
    public ResponseEntity<?> getList(@RequestBody Map<String,Object> paramMap) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userInGroupService.getListGroup(paramMap));
    }
    /*
       name: getListGroupInUserProc
       url: /userInGroup/paging
       type: post
       param : userSeq
       do: paramMap조건에 맞는 userInGroup List 조회
       return : paramMap 조건에 맞는 userSeq, userGroupSeq, insertUserSeq, insertDt
     */
    @SneakyThrows
    @PostMapping("/paging")
    public ResponseEntity<?> getListPaging(@RequestBody Map<String,Object> paramMap) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userInGroupService.getListGroup(paramMap));
    }
    /*
       name: addGroupInUserProc
       url: /userInGroup
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

    @PutMapping()
    public ResponseEntity<?> add(@RequestBody Map<String,Object> paramMap) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userInGroupService.add(paramMap));
    }

    @DeleteMapping()
    public ResponseEntity<?> del(@RequestBody UserInGroupRequest userInGroupRequest) {
        userInGroupService.delOne(userInGroupRequest.getUserSeq(), userInGroupRequest.getUserGroupSeq());
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }

}
