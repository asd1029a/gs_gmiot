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
    private Boolean flag;

    public UserGroupInUser findUserGroupSeq(int UserGroupSeq, String errorMessage) {

        return userGroupInUserRepository.findByUserGroup(UserGroupSeq);


    }

    public UserGroupInUser findByUserSeq(int UserSeq) {

        User findUser = new User();
        findUser.setUserSeq(UserSeq);
        return userGroupInUserRepository.findByUser(findUser);

    }

    @Transactional
    public UserGroupInUser updatePermit(int UserGroupSeq, String refreshToken) {
        UserGroupInUser findUserGroupInUser = this.findUserGroupSeq(UserGroupSeq, "Error update user id");

        //    return userRepository.save(findUser);
        return findUserGroupInUser;
    }


    public List<Integer> saveUserGroupInUser(Map<String, Object> paramMap) {
        List<Integer> userSeqList = (List<Integer>) paramMap.get("userSeqList");
        List<Integer> userGroupSeqList = (List<Integer>) paramMap.get("userGroupSeqList");
        //int userGroupSeq = Integer.parseInt(paramMap.get("userGroupSeq").toString());
        flag = false;
        List<UserGroupInUser> userGroupInUserList = new ArrayList<>();


        userSeqList.forEach(userSeq -> {
            userGroupSeqList.forEach(userGroupSeq -> {
                UserGroupInUser userGroupInUser = new UserGroupInUser();
                UserGroup userGroup = new UserGroup();
                userGroup.setUserGroupSeq(userGroupSeq);
                User user = new User();
                user.setUserSeq(userSeq);
                //  log.info("ㅠㅠ={}",userGroupInUserRepository.findByUserAndUserGroup(user, userGroup) );
                if (userGroupInUserRepository.findByUserAndUserGroup(user, userGroup).isEmpty()) {

                    flag = true;
                } else {

                    flag = false;
                }
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                userGroupInUser.setUser(user);
                userGroupInUser.setUserGroup(userGroup);
                userGroupInUser.setInsertDt(timestamp);
                userGroupInUserList.add(userGroupInUser);
            });

        });
        if (flag) {
            userGroupInUserRepository.saveAll(userGroupInUserList);
        } else if (!flag) {
            userSeqList = null;
        }

//        User user = userRepository.findByUserSeq(userSeq);
//        if (user == null)
//            return 0;
//
//        // log.info("user={}",user);
//        userGroupInUser.setUser(user);
//        UserGroup userGroup = userGroupRepository.findByUserGroupSeq(userGroupSeq);
//        if (userGroup == null)
//            return 0;
//        userGroupInUser.setUserGroup(userGroup);
//
//
//        CommonsUserDetails userDetails = LoginInfoUtil.getUserDetails();
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        userGroupInUser.setInsertUserSeq(userDetails.getUserSeq());
//        userGroupInUser.setInsertDt(timestamp);
//
//        userGroupInUserRepository.save(userGroupInUser);
//        return userSeq;

        return userSeqList;
    }


    @Transactional
    public void deleteUserGroupInUser(int userSeq, int userGroupSeq) {
        User findUser = userRepository.findByUserSeq(userSeq);
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(userGroupSeq);
        userGroupInUserRepository.deleteByUserAndUserGroup(findUser, findUserGroup);
    }

    public Map<String, Object> findListGroupInUser(Map<String, Object> paramMap) {
        List<UserGroupInUser> userGroupList = userGroupInUserRepository.findAll();
        List<GroupInUserResponse> groupInUserResponse = userGroupList.stream().map(GroupInUserResponse::new).collect(Collectors.toList());
        //List<MissionResponse> changeMissionList = missionList.stream().map(MissionResponse::new).collect(Collectors.toList());
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            if (paramMap.get("draw") != null) resultMap = PagingUtil.createPagingMap(paramMap, groupInUserResponse);
            else {
                resultMap.put("data", groupInUserResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return resultMap;
    }
}
