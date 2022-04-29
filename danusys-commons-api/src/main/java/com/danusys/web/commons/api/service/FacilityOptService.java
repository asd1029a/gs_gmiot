package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.dto.EventReqeustDTO;
import com.danusys.web.commons.api.dto.FacilityDataRequestDTO;
import com.danusys.web.commons.api.model.Event;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.repository.FacilityOptRepository;
import com.danusys.web.commons.api.repository.FacilityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/16
 * Time : 09:11
 */
@Slf4j
@Service
public class FacilityOptService {
    private FacilityRepository facilityRepository;
    private FacilityOptRepository facilityOptRepository;

    public FacilityOptService(FacilityRepository facilityRepository,
                              FacilityOptRepository facilityOptRepository) {
        this.facilityRepository = facilityRepository;
        this.facilityOptRepository = facilityOptRepository;
    }

//    public List<FacilityOpt> findByFacilitySeq(Long facilitySeq) {
//        return this.facilityOptRepository.findByFacilitySeq(facilitySeq);
//    }

    public List<FacilityOpt> saveAllByFacilityDataRequestDTO(List<FacilityDataRequestDTO> list) throws Exception {
        List<FacilityOpt> facilityOptList = new ArrayList<>();

        list.forEach(f -> {
            Facility facility = facilityRepository.findByFacilityId(f.getFacilityId());
            f.setFacilitySeq(facility.getFacilitySeq());
            f.setFacilityOptType(3);
//
            facilityOptList.add(f.toEntity());
        });

        facilityOptRepository.saveAll(facilityOptList);
        return facilityOptList;
    }
}
