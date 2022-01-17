package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.repository.UserGroupRepository;
import com.danusys.web.commons.auth.repository.UserRepository;
import com.danusys.web.commons.auth.util.SHA256;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;


    public UserGroupService(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }


    public UserGroup findUserGroup(String groupName, String errorMessage) {
        return userGroupRepository.findByGroupName(groupName);

    }

    @Transactional
    public UserGroup updateUserGroup(UserGroup userGroup) {
        UserGroup findUserGroup = this.findUserGroup(userGroup.getGroupName(), "Error update user id");

        findUserGroup.setGroupDesc(userGroup.getGroupDesc());
        findUserGroup.setGroupName(userGroup.getGroupName());
        findUserGroup.setUpdateUserSeq(userGroup.getUpdateUserSeq());
        findUserGroup.setUpdateDt(userGroup.getUpdateDt());
        findUserGroup.setInsertUserSeq(userGroup.getInsertUserSeq());
        findUserGroup.setInsertDt(userGroup.getInsertDt());


        //    return userRepository.save(findUser);
        return findUserGroup;
    }

    @Transactional
    public UserGroup saveUserGroup(UserGroup userGroup) {

        Date date =new Date();
        if(userGroup.getInsertUserSeq()!=0)
            userGroup.setInsertDt(date);
        else if(userGroup.getUpdateUserSeq()!=0)
            userGroup.setUpdateDt(date);
        return userGroupRepository.save(userGroup);
    }


}
