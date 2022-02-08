package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.dto.response.GroupInUserResponse;
import com.danusys.web.commons.auth.dto.response.GroupResponse;
import com.danusys.web.commons.auth.model.*;
import com.danusys.web.commons.auth.repository.UserGroupInUserRepository;
import com.danusys.web.commons.auth.repository.UserGroupRepository;
import com.danusys.web.commons.auth.repository.UserRepository;
import com.danusys.web.commons.auth.util.PagingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGroupInUserService {

    private final UserGroupInUserRepository userGroupInUserRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;



    public UserGroupInUser findUserGroupSeq(int UserGroupSeq, String errorMessage) {

        return userGroupInUserRepository.findByUserGroup(UserGroupSeq);

    }

    public UserGroupInUser findByUserSeq(int UserSeq) {

        User findUser =new User();
        findUser.setUserSeq(UserSeq);
        return userGroupInUserRepository.findByUser(findUser);

    }

    @Transactional
    public UserGroupInUser updatePermit(int UserGroupSeq, String refreshToken) {
        UserGroupInUser findUserGroupInUser = this.findUserGroupSeq(UserGroupSeq, "Error update user id");

        //    return userRepository.save(findUser);
        return findUserGroupInUser;
    }

    @Transactional
    public int saveUserGroupInUser(UserGroupInUser userGroupInUser,int userSeq,int userGroupSeq) {
        User user=userRepository.findByUserSeq(userSeq);
       // log.info("user={}",user);
        userGroupInUser.setUser(user);
        UserGroup userGroup=userGroupRepository.findByUserGroupSeq(userGroupSeq);
        userGroupInUser.setUserGroup(userGroup);
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        if(userGroupInUser.getInsertUserSeq()!=0)
            userGroupInUser.setInsertDt(timestamp);

        userGroupInUserRepository.save(userGroupInUser);
        return userSeq;




    }



    @Transactional
    public void deleteUserGroupInUser(int userSeq, int userGroupSeq) {
        User findUser=userRepository.findByUserSeq(userSeq);
        UserGroup findUserGroup=userGroupRepository.findByUserGroupSeq(userGroupSeq);
        userGroupInUserRepository.deleteByUserAndUserGroup(findUser,findUserGroup);
    }

    public Map<String,Object> findListGroupInUser(Map<String,Object> paramMap) {
        List<UserGroupInUser> userGroupList =  userGroupInUserRepository.findAll();
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
