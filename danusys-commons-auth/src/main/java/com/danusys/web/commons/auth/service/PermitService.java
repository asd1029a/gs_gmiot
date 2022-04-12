package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.repository.PermitRepository;
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
