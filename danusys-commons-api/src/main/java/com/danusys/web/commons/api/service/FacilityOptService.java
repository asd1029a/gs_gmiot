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
        FacilityOpt result = FacilityOpt.builder()
                                .facilitySeq(facility.getFacilitySeq())
                                .facilityOptName(facilityOpt.getFacilityOptName())
                                .facilityOptValue(facilityOpt.getFacilityOptValue())
                                .facilityOptType(facilityOpt.getFacilityOptType())
                                .build();
        return facilityOptRepository.save(result);
    }

    public FacilityOpt save(FacilityOpt facilityOpt){
        return facilityOptRepository.save(facilityOpt);
    }

    public List<FacilityOpt> saveAll(List<FacilityOpt> list) {
        return facilityOptRepository.saveAll(list);
    }

    @Transactional
    public List<FacilityOpt> saveAllByFacilityDataRequestDTO(List<FacilityDataRequestDTO> list) throws Exception {
        List<FacilityOpt> facilityOptList = new ArrayList<>();
        list.forEach(f -> {
            Boolean cumulativeData = (f.getFacilityOptType()!=53);
            Facility facility = facilityRepository.findByFacilityId(f.getFacilityId());
            List<FacilityOpt> facilityOpt = facilityOptRepository.findAllByFacilitySeqAndFacilityOptName(facility.getFacilitySeq(),f.getFacilityOptName());
            if((facilityOpt.size()!= 0) && !(cumulativeData)){
                facilityOpt.get(0).setFacilityOpt(facility.getFacilitySeq(),f.getFacilityOptName(),f.getFacilityOptValue(),f.getFacilityOptType());
            }else{
                FacilityOpt saveFacilityOpt = FacilityOpt.builder()
                        .facilitySeq(facility.getFacilitySeq())
                        .facilityOptName(f.getFacilityOptName())
                        .facilityOptValue(f.getFacilityOptValue())
                        .facilityOptType(f.getFacilityOptType())
                        .build();
                facilityOptList.add(saveFacilityOpt);
            }
        });
        facilityOptRepository.saveAll(facilityOptList);
        return facilityOptList;
    }

    public FacilityOpt findByFacilitySeqAndFacilityOptName(Long facilitySeq, String facilityOptName) {
        return facilityOptRepository.findByFacilitySeqAndFacilityOptName(facilitySeq, facilityOptName);
    }

    public FacilityOpt findByFacilityOptNameAndFacilityOptValue(String facilityOptName, String facilityOptValue) {
        return facilityOptRepository.findByFacilityOptNameAndFacilityOptValue(facilityOptName, facilityOptValue);
    }

    public List<FacilityOpt> findByFacilitySeq(Long facilitySeq) {
        return facilityOptRepository.findByFacilitySeq(facilitySeq);
    }

    public List<FacilityOpt> findByFacilitySeqLast(Long facilitySeq) {
        return facilityOptRepository.findByFacilitySeqLast(facilitySeq);
    }
}
