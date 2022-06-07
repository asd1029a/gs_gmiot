package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.dto.FacilityDataRequestDTO;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.repository.FacilityOptRepository;
import com.danusys.web.commons.api.repository.FacilityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public FacilityOpt save(FacilityDataRequestDTO facilityOpt){
        Facility facility = facilityRepository.findByFacilityId(facilityOpt.getFacilityId());
        FacilityOpt result = new FacilityOpt(facility.getFacilitySeq(),facility.getFacilityName(),facilityOpt.getFacilityOptValue(),facilityOpt.getFacilityOptType());
        return facilityOptRepository.save(result);
    }

    public List<FacilityOpt> saveAll(List<FacilityOpt> list) {
        return facilityOptRepository.saveAll(list);
    }

    @Transactional
    public List<FacilityOpt> saveAllByFacilityDataRequestDTO(List<FacilityDataRequestDTO> list) throws Exception {
        List<FacilityOpt> facilityOptList = new ArrayList<>();

        list.forEach(f -> {
            Facility facility = facilityRepository.findByFacilityId(f.getFacilityId());
            FacilityOpt facilityOpt = facilityOptRepository.findByFacilitySeqAndFacilityOptName(facility.getFacilitySeq(),
                    f.getFacilityOptName());
            facilityOpt.setFacilityOpt(facility.getFacilitySeq(),f.getFacilityOptName(),f.getFacilityOptValue(),f.getFacilityOptType());
            facilityOptList.add(facilityOpt);
            facilityOptRepository.save(facilityOpt);
        });
        return facilityOptList;
    }

    public FacilityOpt findByFacilitySeqAndFacilityOptName(Long facilitySeq, String facilityOptName) {
        return facilityOptRepository.findByFacilitySeqAndFacilityOptName(facilitySeq, facilityOptName);
    }

}
