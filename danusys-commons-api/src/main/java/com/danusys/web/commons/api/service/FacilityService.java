package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.Station;
import com.danusys.web.commons.api.querydsl.*;
import com.danusys.web.commons.api.repository.FacilityOptRepository;
import com.danusys.web.commons.api.repository.FacilityRepository;
import com.danusys.web.commons.api.repository.StationRepository;
import com.danusys.web.commons.app.StrUtils;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final StationRepository stationRepository;
    private final FacilityOptRepository facilityOptRepository;
    private final FacilityQueryDslRepository facilityQueryDslRepository;

    public Facility save(Facility facility) {
        return this.facilityRepository.save(facility);
    }

    public List<Facility> findAll() {
        return facilityRepository.findAll();
    }

    public Facility findByFacilityId(String facilityId) {
        return facilityRepository.findByFacilityId(facilityId);
    }
    
    public List<Facility> saveAll(List<Facility> list) {
        return facilityRepository.saveAll(list);
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

        return this.saveAll(facilityList);
    }

    public List<Facility> saveAllByList(List<Map<String, Object>> list) {
        List<Facility> facilityList = new ArrayList<Facility>();
        list.stream().forEach((d) -> {
            String facilityId = d.get("facility_id").toString();
            Facility originFacility = this.findByFacilityId(facilityId);
            Facility facility = originFacility == null ? new Facility() : originFacility;
            Station station = stationRepository.findByStationName(StrUtils.getStr(d.get("station_name")));
            Long codeSeq = facilityRepository.findCommonCode(d.get("facility_kind").toString());

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
        return this.saveAll(facilityList);
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

    /**
     * ?????? seq ?????????
     * @param stationSeq
     * @return
     */
    public List<Facility> findByStationSeq(Long stationSeq) {
        return facilityRepository.findByStationSeq(stationSeq);
    }


    public List<Long> findFacilityKindList(List<String> facilityKindValues) {
        return facilityRepository.findFacilityKindList(facilityKindValues);
    }

    public List<Facility> findByAdministZoneAndFacilityKindIn(String admnistZone, List<Long> facilityKind) {
        return facilityRepository.findByAdministZoneAndFacilityKindIn(admnistZone, facilityKind);
    }

    public List<Facility> findByFacilityKindAndLatitudeAndLongitude(Long facilityKind, double latitude, double longitude) {
        return facilityRepository.findByFacilityKindAndLatitudeAndLongitude(facilityKind, latitude, longitude);
    }

    public List<Facility> findByGeomSql(double latitude, double longitude, String administZone) {
        return facilityQueryDslRepository.findByGeomSql(latitude, longitude, administZone);
    }
}
