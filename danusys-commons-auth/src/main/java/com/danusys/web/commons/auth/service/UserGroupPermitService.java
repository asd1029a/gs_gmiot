package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupPermit;
import com.danusys.web.commons.auth.repository.PermitRepository;
import com.danusys.web.commons.auth.repository.UserGroupInUserRepository;
import com.danusys.web.commons.auth.repository.UserGroupPermitRepository;
import com.danusys.web.commons.auth.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGroupPermitService {

    private final UserGroupPermitRepository userGroupPermitRepository;
    private final UserGroupRepository userGroupRepository;
    private final PermitRepository permitRepository;

    public UserGroupPermit saveUserGroupPermit(UserGroupPermit userGroupPermit, int userGroupSeq, int permitSeq) {

        UserGroup userGroup = userGroupRepository.findByUserGroupSeq(userGroupSeq);
        Permit permit = permitRepository.findByPermitSeq(permitSeq);
        userGroupPermit.setUserGroup2(userGroup);
        userGroupPermit.setPermit(permit);
        log.info("insertUserSeq={}", userGroupPermit.getInsertUserSeq());
        if (userGroupPermit.getInsertUserSeq() != 0) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            userGroupPermit.setInsertDt(timestamp);
        }

        return userGroupPermitRepository.save(userGroupPermit);
    }

    @Transactional
    public void deleteUserGroupPermit(int userGroupSeq, int permitSeq) {
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(userGroupSeq);
        Permit findPermit = permitRepository.findByPermitSeq(permitSeq);
        userGroupPermitRepository.deleteByUserGroup2AndPermit(findUserGroup, findPermit);
    }
}
