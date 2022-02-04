package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupInUser;
import com.danusys.web.commons.auth.repository.PermitRepository;
import com.danusys.web.commons.auth.repository.UserGroupInUserRepository;
import com.danusys.web.commons.auth.repository.UserGroupRepository;
import com.danusys.web.commons.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public List<UserGroupInUser> findListGroupInUser() {

        return userGroupInUserRepository.findAll();
    }
}
