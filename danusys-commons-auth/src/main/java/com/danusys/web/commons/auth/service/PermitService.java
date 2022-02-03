package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.repository.PermitRepository;
import com.danusys.web.commons.auth.repository.UserRepository;
import com.danusys.web.commons.auth.util.SHA256;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class PermitService {

    private final PermitRepository permitRepository;


    public PermitService(PermitRepository permitRepository) {
        this.permitRepository = permitRepository;
    }

    public Permit findPermit(int permitSeq){
        return permitRepository.findByPermitSeq(permitSeq);
    }

    public Permit findPermit(String permitName, String errorMessage) {
        return permitRepository.findByPermitName(permitName);

    }

    @Transactional
    public Permit updatePermit(String permitName, String refreshToken) {
        Permit findPermit = this.findPermit(permitName, "Error update user id");

        //    return userRepository.save(findUser);
        return findPermit;
    }

    @Transactional
    public Permit savePermit(Permit permit) {


        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        if(permit.getInsertUserSeq()!=0)
            permit.setInsertDt(timestamp);
        return permitRepository.save(permit);
    }


}
