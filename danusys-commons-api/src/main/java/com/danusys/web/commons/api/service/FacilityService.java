package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.Station;
import com.danusys.web.commons.api.repository.FacilityOptRepository;
import com.danusys.web.commons.api.repository.FacilityRepository;
import com.danusys.web.commons.api.repository.StationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/08
 * Time : 16:41
 */
@Slf4j
@Service
public class FacilityService {
    private FacilityRepository facilityRepository;
    private StationRepository stationRepository;
    private FacilityOptRepository facilityOptRepository;

    public FacilityService(FacilityRepository facilityRepository
            , StationRepository stationRepository
            , FacilityOptRepository facilityOptRepository) {
        this.facilityRepository = facilityRepository;
        this.stationRepository = stationRepository;
        this.facilityOptRepository = facilityOptRepository;
    }

    public List<Facility> findAll() {
        return facilityRepository.findAll();
    }

    public Facility findByFacilityId(String facilityId) {
        return facilityRepository.findByFacilityId(facilityId);
    }

    public void saveAll(List<Map<String, Object>> list) {
        List<Facility> facilityList = new ArrayList<Facility>();
        list.stream().forEach((d) -> {
            String facilityId = d.get("facility_id").toString() + "DDD";
            Facility originFacility = this.findByFacilityId(facilityId);
            Facility facility = originFacility == null ? new Facility() : originFacility;
            Station station = stationRepository.findByStationName(d.get("station_name").toString());
            Long codeSeq = facilityRepository.findCommonCode(d.get("facility_kind").toString());

            facility.setLatitude(Double.parseDouble(d.get("latitude").toString()));
            facility.setLongitude(Double.parseDouble(d.get("longitude").toString()));
            facility.setFacilityId(facilityId);
            facility.setStationSeq(station.getStationSeq());
            facility.setFacilityStatus(0);
            facility.setFacilityKind(Integer.parseInt(codeSeq.toString()));

            facilityList.add(facility);
        });

        facilityRepository.saveAll(facilityList);
    }
}
