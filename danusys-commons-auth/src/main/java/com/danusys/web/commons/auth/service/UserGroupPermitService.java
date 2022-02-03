package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroupPermit;
import com.danusys.web.commons.auth.repository.UserGroupInUserRepository;
import com.danusys.web.commons.auth.repository.UserGroupPermitRepository;
import com.danusys.web.commons.auth.repository.UserRepository;
import com.danusys.web.commons.auth.util.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class UserGroupPermitService {

    private final UserGroupPermitRepository userGroupPermitRepository;

    @Transactional
    public UserGroupPermit saveUserGroupPermit(UserGroupPermit userGroupPermit){

        return userGroupPermitRepository.save(userGroupPermit);
    }


}
