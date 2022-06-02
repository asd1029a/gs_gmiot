package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.Station;
import com.danusys.web.commons.api.repository.FacilityOptRepository;
import com.danusys.web.commons.api.repository.FacilityRepository;
import com.danusys.web.commons.api.repository.StationRepository;
import com.danusys.web.commons.app.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Facility save(Facility facility) {
        return this.facilityRepository.save(facility);
    }

    public List<Facility> findAll() {
        return facilityRepository.findAll();
    }

    public Facility findByFacilityId(String facilityId) {
        return facilityRepository.findByFacilityId(facilityId);
    }
    
    public void saveAll(List<Facility> list) {
        facilityRepository.saveAll(list);
    }

    public List<Facility> saveAll(List<Map<String, Object>> list, String facilityKind) {
        List<Facility> facilityList = new ArrayList<Facility>();
        list.stream().forEach((d) -> {
            String facilityId = d.get("facility_id").toString();
            Facility originFacility = this.findByFacilityId(facilityId);
            Facility facility = originFacility == null ? new Facility() : originFacility;
            Station station = stationRepository.findByStationName(StrUtils.getStr(d.get("station_name")));
            Long codeSeq = facilityRepository.findCommonCode(facilityKind);

            String latStr = StrUtils.getStr(d.get("latitude"));
            String lngStr = StrUtils.getStr(d.get("longitude"));
            String facilityName = StrUtils.getStr(d.get("facility_name"));


            latStr = latStr.isEmpty() ? "0" : latStr;
            lngStr = lngStr.isEmpty() ? "0" : lngStr;

            double latitude = Double.parseDouble(latStr);
            double longitude = Double.parseDouble(lngStr);

            facility.setLatitude(latitude);
            facility.setLongitude(longitude);
            facility.setFacilityId(facilityId);
            facility.setFacilityStatus(0);
            facility.setFacilityKind(Long.parseLong(codeSeq.toString()));
            facility.setAdministZone(facilityRepository.getEmdCode(longitude, latitude));
            if (!facilityName.isEmpty()) facility.setFacilityName(facilityName);

            if (station != null) {
                facility.setStationSeq(station.getStationSeq());
            }

            facilityList.add(facility);
        });

        return facilityRepository.saveAll(facilityList);
    }

    public Facility getOne(Long id) {
        return facilityRepository.getOne(id);
    }

    @Transactional
    public Facility update(Facility facility) {
        Facility d = facilityRepository.findByFacilityId(facility.getFacilityId());
        d.setLatitude(facility.getLatitude());
        d.setLongitude(facility.getLongitude());
        return d;
    }

    public List<Facility> findByFacilityKind(Long facilityKind) {
        return facilityRepository.findByFacilityKind(facilityKind);
    }

    public String getEmdCode(double longitude, double latitude) {
        return facilityRepository.getEmdCode(longitude, latitude);
    }
}
