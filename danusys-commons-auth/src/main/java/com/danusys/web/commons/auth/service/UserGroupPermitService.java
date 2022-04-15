package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.config.auth.CommonsUserDetails;
import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupPermit;
import com.danusys.web.commons.auth.service.repository.PermitMenuRepository;
import com.danusys.web.commons.auth.service.repository.PermitRepository;
import com.danusys.web.commons.auth.service.repository.UserGroupPermitRepository;
import com.danusys.web.commons.auth.service.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGroupPermitService {

    private final UserGroupPermitRepository userGroupPermitRepository;
    private final UserGroupRepository userGroupRepository;
    private final PermitRepository permitRepository;
    private final PermitMenuRepository permitMenuRepository;

    @Transactional
    public void add(Map<String, Object> paramMap) {
        int userGroupSeq = (Integer) paramMap.get("userGroupSeq");
        Map<String, String> permitMap = (Map<String, String>) paramMap.get("permitList");
        UserGroup userGroup = userGroupRepository.findByUserGroupSeq(userGroupSeq);

        if (userGroup == null)
            return;

        List<UserGroupPermit> permitList = new ArrayList<>();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommonsUserDetails userDetails = (CommonsUserDetails) principal;
        int insUserSeq = userDetails.getUserSeq();

        permitMap.forEach((key, val) -> {
            UserGroupPermit ugp = new UserGroupPermit();
            ugp.setInsertUserSeq(insUserSeq);
            ugp.setUserGroupSeq(userGroupSeq);
            ugp.setPermitMenuSeq(permitMenuRepository.findByCodeValue(key).getCodeSeq());
            ugp.setPermitSeq(permitRepository.findByCodeValue(val).getCodeSeq());
            ugp.setInsertDt(timestamp);
            permitList.add(ugp);
        });

        userGroupPermitRepository.saveAll(permitList);
    }

    @Transactional
    public void delByUserGroupSeq(int userGroupSeq) {
        userGroupPermitRepository.deleteAllByUserGroupSeq(userGroupSeq);
    }

    @Transactional
    public void del(int userGroupSeq, int permitSeq) {
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(userGroupSeq);
        Permit findPermit = permitRepository.findByCodeSeq(permitSeq);
        userGroupPermitRepository.deleteByUserGroup2AndPermit(findUserGroup, findPermit);
    }

}
