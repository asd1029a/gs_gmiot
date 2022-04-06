package com.danusys.web.commons.auth.session.service;

import com.danusys.web.commons.auth.session.config.auth.CommonsUserDetails;
import com.danusys.web.commons.auth.session.model.Permit;
import com.danusys.web.commons.auth.session.model.UserGroup;
import com.danusys.web.commons.auth.session.model.UserGroupPermit;
import com.danusys.web.commons.auth.session.repository.PermitRepository;
import com.danusys.web.commons.auth.session.repository.UserGroupPermitRepository;
import com.danusys.web.commons.auth.session.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public void add(UserGroupPermit userGroupPermit, int userGroupSeq, int permitSeq) {
        UserGroup userGroup = userGroupRepository.findByUserGroupSeq(userGroupSeq);

        if (userGroup == null)
            return;
        Permit permit = permitRepository.findByCodeSeq(permitSeq);
        if (permit == null)
            return;

        userGroupPermit.setUserGroup2(userGroup);
        userGroupPermit.setPermit(permit);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommonsUserDetails userDetails = (CommonsUserDetails) principal;

        userGroupPermit.setInsertUserSeq(userDetails.getUserSeq());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        userGroupPermit.setInsertDt(timestamp);
    }

    @Transactional
    public void del(int userGroupSeq, int permitSeq) {
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(userGroupSeq);
        Permit findPermit = permitRepository.findByCodeSeq(permitSeq);
        userGroupPermitRepository.deleteByUserGroup2AndPermit(findUserGroup, findPermit);
    }
}
