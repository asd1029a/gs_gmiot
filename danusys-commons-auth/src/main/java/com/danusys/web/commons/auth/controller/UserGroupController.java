package com.danusys.web.commons.auth.controller;

import com.danusys.web.commons.auth.dto.request.UserGroupPermitRequest;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupPermit;
import com.danusys.web.commons.auth.service.UserInGroupService;
import com.danusys.web.commons.auth.service.UserGroupPermitService;
import com.danusys.web.commons.auth.service.UserGroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping(value = "/user/group")
@RequiredArgsConstructor
@Slf4j
public class UserGroupController {

    private final UserGroupService userGroupService;
    private final UserGroupPermitService userGroupPermitService;
    private final UserInGroupService userInGroupService;

    /*
       name: get
       url: /{groupSeq}
       type: get
       do: groupSeq로 group 단건 조회
       return : 단건 조회
     */
    @GetMapping("/{groupSeq}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> get(@PathVariable int groupSeq) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupService.getOneByGroupSeq(groupSeq));
//                .body(userGroupService.findUserGroupResponseByGroupSeq(groupSeq));
    }

    /*
       name: getList
       url: /group
       type: post
       param : Map<String, Object> paramMap
       ex  :{
            "keyword":"groupb43",
        }
       do: paramMap 조건에 맞는 list 출력
       return : paramMap 조건에 맞는 list
     */
    @PostMapping()
    public ResponseEntity<?> getList(@RequestBody Map<String, Object> paramMap) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupService.getList(paramMap));
    }

    /*
       name: getListPaging
       url: /paging
       type: post
       param : Map<String, Object> paramMap
       ex  :{
            "keyword":"groupb43",
            "start":0,
            "length":15,
        }
       do: paramMap 조건에 맞는 list 출력
       return : paramMap 조건에 맞는 list
     */
    @PostMapping("/paging")
    public ResponseEntity<?> getListPaging(@RequestBody Map<String, Object> paramMap) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGroupService.getListPaging(paramMap));
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
                .body(userGroupService.getListUserInGroup(paramMap));
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
                .body(userGroupService.getListUserInGroupPaging(paramMap));
    }

    /*
       name: add
       url: /group
       type: put
       param : UserGroup userGroup
       do: usergroup 저장 , groupName,groupDesc 없이 전송할경우 0 리턴
       return : 저장된 groupSeq
     */
    @PutMapping()
    public ResponseEntity<?> add(@RequestBody Map<String, Object> paramMap) {
        /* TODO : 트랜젝션 처리 요망 */
        ObjectMapper objectMapper = new ObjectMapper();
        UserGroup userGroup = objectMapper.convertValue(paramMap, UserGroup.class);

        int result = userGroupService.add(userGroup);
        paramMap.put("userGroupSeqList", Arrays.asList(result));
        paramMap.put("userGroupSeq", result);
        userInGroupService.add(paramMap);

        userGroupPermitService.add(paramMap);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    /*
       name: mod
       url: /group
       type: patch
       param : UserGroup userGroup
       do: usergroup 업데이트
       return : 저장된 groupSeq groupSeq로 조회한 group이 없을경우 0 리턴
     */
    @PatchMapping()
    public ResponseEntity<?> mod(@RequestBody Map<String, Object> paramMap) {
        /* TODO : 트랜젝션 처리 요망 */
        ObjectMapper objectMapper = new ObjectMapper();
        UserGroup userGroup = objectMapper.convertValue(paramMap, UserGroup.class);

        int result = userGroupService.mod(userGroup);
        userInGroupService.delUserGroupSeq(userGroup.getUserGroupSeq());
        userInGroupService.add(paramMap);

        /* TODO : 삭제 구현 덜됨 */
//        userGroupPermitService.del(paramMap);
        userGroupPermitService.add(paramMap);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    /*
       name: delete
       url: /group
       type: delete
       param : userSeq
       do: usergroup 삭제
     */
    @DeleteMapping()
    public ResponseEntity<?> del(@RequestBody UserGroup userGroup) {
        userInGroupService.delUserGroupSeq(userGroup.getUserGroupSeq());
        userGroupService.del(userGroup);

        return ResponseEntity
                .status(HttpStatus.OK).build();
    }

//    @PutMapping("/groupPermit")
//    public ResponseEntity<?> addPermitProc(@RequestBody UserGroupPermitRequest userGroupPermitRequest) {
////        log.info("userGroupPermitReqeuest={}", userGroupPermitRequest);
//        UserGroupPermit userGroupPermit = new UserGroupPermit();
//        userGroupPermit.setInsertUserSeq(userGroupPermitRequest.getInsertUserSeq());
//        userGroupPermitService.add(userGroupPermit
//                , userGroupPermitRequest.getUserGroupSeq(), userGroupPermitRequest.getPermitSeq());
//        return ResponseEntity
//                .status(HttpStatus.OK).build();
//    }


    @DeleteMapping("/groupPermit")
    public ResponseEntity<?> delPermitProc(@RequestBody UserGroupPermitRequest userGroupPermitRequest) {
//        log.info("userGroupPermitReqeuest={}", userGroupPermitRequest);
        userGroupPermitService.del(
                userGroupPermitRequest.getUserGroupSeq(), userGroupPermitRequest.getPermitSeq());
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }
}
