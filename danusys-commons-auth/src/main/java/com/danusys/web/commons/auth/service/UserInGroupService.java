package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.dto.response.UserInGroupResponse;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserInGroup;
import com.danusys.web.commons.auth.service.repository.UserInGroupRepository;
import com.danusys.web.commons.auth.service.repository.UserGroupRepository;
import com.danusys.web.commons.auth.service.repository.UserRepository;
import com.danusys.web.commons.auth.util.LoginInfoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInGroupService {

    private final UserInGroupRepository userInGroupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    /* 추후 사용 예정 */
//    public UserInGroup getOneUserGroupSeq(int UserGroupSeq, String errorMessage) {
//        return userInGroupRepository.findByUserGroup(UserGroupSeq);
//    }

    /* 추후 사용 예정 */
//    public UserInGroup getOneUserSeq(int UserSeq) {
//        User findUser = new User();
//        findUser.setUserSeq(UserSeq);
//        return userInGroupRepository.findByUser(findUser);
//    }

    public Map<String, Object> getListGroup(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<UserInGroup> userGroupList;

        User user = userRepository.findByUserSeq((int) paramMap.get("userSeq"));
        userGroupList = userInGroupRepository.findAllByUser(user);
        List<UserInGroupResponse> userInGroupResponse = userGroupList.stream()
                .map(UserInGroupResponse::new)
                .collect(Collectors.toList());
        resultMap.put("data", userInGroupResponse);

        return resultMap;
    }

    /* 추후 사용 예정 */
//    public Map<String, Object> getListGroupPaging(Map<String, Object> paramMap) throws Exception {
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//
//        try {
//            List<UserInGroup> userGroupList;
//            UserGroup userGroup = userGroupRepository.findByUserGroupSeq((int) paramMap.get("userGroupSeq"));
//            userGroupList = userInGroupRepository.findAllByUserGroup(userGroup);
//            List<UserInGroupResponse> userInGroupResponse = userGroupList.stream()
//                    .map(UserInGroupResponse::new)
//                    .collect(Collectors.toList());
//            resultMap = PagingUtil.createPagingMap(paramMap, userInGroupResponse);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return resultMap;
//    }

    /* 추후 사용 예정 */
//    public Map<String, Object> getListUser(Map<String, Object> paramMap) throws Exception {
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//
//        List<UserInGroup> userGroupList;
//        UserGroup userGroup = userGroupRepository.findByUserGroupSeq((int) paramMap.get("userGroupSeq"));
//        userGroupList = userInGroupRepository.findAllByUserGroup(userGroup);
//        List<UserInGroupResponse> userInGroupResponse = userGroupList.stream()
//                .map(UserInGroupResponse::new)
//                .collect(Collectors.toList());
//        resultMap.put("data", userInGroupResponse);
//
//        return resultMap;
//    }


    /* 추후 사용 예정 */
//    public Map<String, Object> getListUserPaging(Map<String, Object> paramMap) throws Exception {
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//
//        try {
//            List<UserInGroup> userGroupList;
//            UserGroup userGroup = userGroupRepository.findByUserGroupSeq((int) paramMap.get("userGroupSeq"));
//            userGroupList = userInGroupRepository.findAllByUserGroup(userGroup);
//            List<UserInGroupResponse> userInGroupResponse = userGroupList.stream()
//                    .map(UserInGroupResponse::new)
//                    .collect(Collectors.toList());
//            resultMap = PagingUtil.createPagingMap(paramMap, userInGroupResponse);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return resultMap;
//    }

    @Transactional
    public List<Integer> add(Map<String, Object> paramMap) {
        List<Integer> userSeqList = (List<Integer>) paramMap.get("userSeqList");
        List<Integer> userGroupSeqList = (List<Integer>) paramMap.get("userGroupSeqList");
        List<UserInGroup> userInGroupList = new ArrayList<>();
        int insertUserSeq =LoginInfoUtil.getUserDetails().getUserSeq();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        userSeqList.forEach(userSeq -> {
            userGroupSeqList.forEach(userGroupSeq -> {
                UserInGroup userInGroup = new UserInGroup();
                UserGroup userGroup = new UserGroup();
                userGroup.setUserGroupSeq(userGroupSeq);
                User user = new User();
                user.setUserSeq(userSeq);

                userInGroup.setUser(user);
                userInGroup.setUserGroup(userGroup);
                userInGroup.setInsertDt(timestamp);
                userInGroup.setInsertUserSeq(insertUserSeq);
                userInGroupList.add(userInGroup);
            });
        });
        userInGroupRepository.saveAll(userInGroupList);

        return userSeqList;
    }

    @Transactional
    public void delOne(int userSeq, int userGroupSeq) {
        User findUser = userRepository.findByUserSeq(userSeq);
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(userGroupSeq);
        userInGroupRepository.deleteByUserAndUserGroup(findUser, findUserGroup);
    }

    @Transactional
    public void delUserSeq(int userSeq) {
        userInGroupRepository.deleteAllByUserSeq(userSeq);
    }

    @Transactional
    public void delUserGroupSeq(int userGroupSeq) {
        userInGroupRepository.deleteAllByUserGroupSeq(userGroupSeq);
    }
}
