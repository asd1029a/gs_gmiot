package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.commons.auth.dto.response.GroupInUserResponse;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupInUser;
import com.danusys.web.commons.auth.repository.UserGroupInUserRepository;
import com.danusys.web.commons.auth.repository.UserGroupRepository;
import com.danusys.web.commons.auth.repository.UserRepository;
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
public class UserGroupInUserService {

    private final UserGroupInUserRepository userGroupInUserRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    public UserGroupInUser getOneUserGroupSeq(int UserGroupSeq, String errorMessage) {
        return userGroupInUserRepository.findByUserGroup(UserGroupSeq);
    }

    public UserGroupInUser getOneUserSeq(int UserSeq) {
        User findUser = new User();
        findUser.setUserSeq(UserSeq);
        return userGroupInUserRepository.findByUser(findUser);
    }

    public Map<String, Object> getListGroup(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<UserGroupInUser> userGroupList;

        User user = userRepository.findByUserSeq((int) paramMap.get("userSeq"));
        userGroupList = userGroupInUserRepository.findAllByUser(user);
        List<GroupInUserResponse> groupInUserResponse = userGroupList.stream()
                .map(GroupInUserResponse::new)
                .collect(Collectors.toList());
        resultMap.put("data", groupInUserResponse);

        return resultMap;
    }

    public Map<String, Object> getListGroupPaging(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            List<UserGroupInUser> userGroupList;
            UserGroup userGroup = userGroupRepository.findByUserGroupSeq((int) paramMap.get("userGroupSeq"));
            userGroupList = userGroupInUserRepository.findAllByUserGroup(userGroup);
            List<GroupInUserResponse> groupInUserResponse = userGroupList.stream()
                    .map(GroupInUserResponse::new)
                    .collect(Collectors.toList());
            resultMap = PagingUtil.createPagingMap(paramMap, groupInUserResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    public Map<String, Object> getListUser(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        List<UserGroupInUser> userGroupList;
        UserGroup userGroup = userGroupRepository.findByUserGroupSeq((int) paramMap.get("userGroupSeq"));
        userGroupList = userGroupInUserRepository.findAllByUserGroup(userGroup);
        List<GroupInUserResponse> groupInUserResponse = userGroupList.stream()
                .map(GroupInUserResponse::new)
                .collect(Collectors.toList());
        resultMap.put("data", groupInUserResponse);

        return resultMap;
    }

    public Map<String, Object> getListUserPaging(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            List<UserGroupInUser> userGroupList;
            UserGroup userGroup = userGroupRepository.findByUserGroupSeq((int) paramMap.get("userGroupSeq"));
            userGroupList = userGroupInUserRepository.findAllByUserGroup(userGroup);
            List<GroupInUserResponse> groupInUserResponse = userGroupList.stream()
                    .map(GroupInUserResponse::new)
                    .collect(Collectors.toList());
            resultMap = PagingUtil.createPagingMap(paramMap, groupInUserResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    @Transactional
    public UserGroupInUser updatePermit(int UserGroupSeq, String refreshToken) {
        UserGroupInUser findUserGroupInUser = this.getOneUserGroupSeq(UserGroupSeq, "Error update user id");
        return findUserGroupInUser;
    }


    public List<Integer> saveUserGroupInUser(Map<String, Object> paramMap) {
        List<Integer> userSeqList = (List<Integer>) paramMap.get("userSeqList");
        List<Integer> userGroupSeqList = (List<Integer>) paramMap.get("userGroupSeqList");
        List<UserGroupInUser> userGroupInUserList = new ArrayList<>();
        int insertUserSeq = (int) paramMap.get("insertUserSeq");
//        String lastInsertId = ;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        userSeqList.forEach(userSeq -> {
            userGroupSeqList.forEach(userGroupSeq -> {
                UserGroupInUser userGroupInUser = new UserGroupInUser();
                UserGroup userGroup = new UserGroup();
                userGroup.setUserGroupSeq(userGroupSeq);
                User user = new User();
                user.setUserSeq(userSeq);

//                if (userGroupInUserRepository.findByUserAndUserGroup(user, userGroup).isEmpty()) {
                    userGroupInUser.setUser(user);
                    userGroupInUser.setUserGroup(userGroup);
                    userGroupInUser.setInsertDt(timestamp);
                    userGroupInUser.setInsertUserSeq(insertUserSeq);
                    userGroupInUserList.add(userGroupInUser);
//                }
            });
        });
        userGroupInUserRepository.saveAll(userGroupInUserList);

        return userSeqList;
    }


    @Transactional
    public void delOne(int userSeq, int userGroupSeq) {
        User findUser = userRepository.findByUserSeq(userSeq);
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(userGroupSeq);
        userGroupInUserRepository.deleteByUserAndUserGroup(findUser, findUserGroup);
    }

    @Transactional
    public void delUserSeq(int userSeq) {
        userGroupInUserRepository.deleteAllByUserSeq(userSeq);
//        User user = userRepository.findByUserSeq(userSeq);
//        List<UserGroupInUser> userGroupList = userGroupInUserRepository.findAllByUser(user);
//        userGroupInUserRepository.deleteAll(userGroupList);

//        userGroupInUserRepository.findAllByUser(user).stream()
//                .map(r -> userGroupInUserRepository.delete(r));
//                .orElseThrow(() -> new UserNotFoundException(User.class, userId));
    }

    @Transactional
    public void delUserGroupSeq(int userGroupSeq) {
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(userGroupSeq);
        userGroupInUserRepository.deleteByUserGroup(findUserGroup);
    }
}
