package com.danusys.web.commons.auth.session.service;

import com.danusys.web.commons.auth.session.repository.PermitRepository;
import com.danusys.web.commons.auth.session.model.Permit;
import org.springframework.stereotype.Service;

@Service
public class PermitService {

    private final PermitRepository permitRepository;

    public PermitService(PermitRepository permitRepository) {
        this.permitRepository = permitRepository;
    }

    public Permit get(int permitSeq) {
        return permitRepository.findByCodeSeq(permitSeq);
    }

    public Permit get(String permitName, String errorMessage) {
        return permitRepository.findByCodeValue(permitName);
    }
}
