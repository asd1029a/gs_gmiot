package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.repository.PermitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
public class PermitService {

    private final PermitRepository permitRepository;

    public PermitService(PermitRepository permitRepository) {
        this.permitRepository = permitRepository;
    }

    public Permit get(int permitSeq) {
        return permitRepository.findByPermitSeq(permitSeq);
    }

    public Permit get(String permitName, String errorMessage) {
        return permitRepository.findByPermitName(permitName);
    }

    @Transactional
    public Permit add(Permit permit) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (permit.getInsertUserSeq() != 0)
            permit.setInsertDt(timestamp);
        return permitRepository.save(permit);
    }

    @Transactional
    public Permit mod(String permitName, String refreshToken) {
        Permit findPermit = this.get(permitName, "Error update user id");
        return findPermit;
    }
}
