package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.repository.UserGroupRepository;
import com.danusys.web.commons.auth.repository.UserRepository;
import com.danusys.web.commons.auth.util.SHA256;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;


    public UserGroupService(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }


    public UserGroup findUserGroup(String groupName, String errorMessage) {
        return userGroupRepository.findByGroupName(groupName);

    }
    @Transactional(readOnly = true)
    public UserGroup findUserGroupByGroupSeq(int groupSeq) {
        return userGroupRepository.findByUserGroupSeq(groupSeq);
    }

    @Transactional
    public UserGroup updateUserGroup(UserGroup userGroup) {

        UserGroup findUserGroup = this.findUserGroupByGroupSeq(userGroup.getUserGroupSeq());
        if (userGroup.getGroupDesc() != null)
            findUserGroup.setGroupDesc(userGroup.getGroupDesc());
        if (userGroup.getGroupName() != null)
            findUserGroup.setGroupName(userGroup.getGroupName());
        if (userGroup.getUpdateUserSeq() != 0) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            findUserGroup.setUpdateUserSeq(userGroup.getUpdateUserSeq());
            findUserGroup.setUpdateDt(timestamp);
        }


        //    return userRepository.save(findUser);
        return findUserGroup;
    }

    @Transactional
    public int saveUserGroup(UserGroup userGroup) {


        if (userGroup.getInsertUserSeq() != 0) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            userGroup.setInsertDt(timestamp);
        } else if (userGroup.getUpdateUserSeq() != 0) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            userGroup.setUpdateDt(timestamp);
        }
        userGroupRepository.save(userGroup);
        return userGroup.getUserGroupSeq();
    }

    public void deleteUserGroup(UserGroup userGroup){

        userGroupRepository.deleteById(userGroup.getUserGroupSeq());
    }


    public List<UserGroup> findListGroup() {


        return userGroupRepository.findAll();
    }
}
