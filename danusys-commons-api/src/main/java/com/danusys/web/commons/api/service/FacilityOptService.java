package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.repository.FacilityOptRepository;

import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/16
 * Time : 09:11
 */
public class FacilityOptService {
    private FacilityOptRepository facilityOptRepository;

    public FacilityOptService(FacilityOptRepository facilityOptRepository) {
        this.facilityOptRepository = facilityOptRepository;
    }

//    public List<FacilityOpt> findByFacilitySeq(Long facilitySeq) {
//        return this.facilityOptRepository.findByFacilitySeq(facilitySeq);
//    }
}
