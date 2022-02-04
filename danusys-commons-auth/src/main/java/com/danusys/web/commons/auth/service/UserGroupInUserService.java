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

    @Transactional
    public UserGroupInUser updatePermit(int UserGroupSeq, String refreshToken) {
        UserGroupInUser findUserGroupInUser = this.findUserGroupSeq(UserGroupSeq, "Error update user id");

        //    return userRepository.save(findUser);
        return findUserGroupInUser;
    }

    @Transactional
    public UserGroupInUser saveUserGroupInUser(UserGroupInUser userGroupInUser,int userSeq,int userGroupSeq) {
        User user=userRepository.findByUserSeq(userSeq);
        log.info("user={}",user);
        userGroupInUser.setUser(user);
        UserGroup userGroup=userGroupRepository.findByUserGroupSeq(userGroupSeq);
        userGroupInUser.setUserGroup(userGroup);
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        if(userGroupInUser.getInsertUserSeq()!=0)
            userGroupInUser.setInsertDt(timestamp);


        return userGroupInUserRepository.save(userGroupInUser);




    }


}
