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

    public List<Facility> findAll() {
        return facilityRepository.findAll();
    }

    public Facility findByFacilityId(String facilityId) {
        return facilityRepository.findByFacilityId(facilityId);
    }

    public void saveAll(List<Map<String, Object>> list) {
        List<Facility> facilityList = new ArrayList<Facility>();
        list.stream().forEach((d) -> {
            String facilityId = d.get("facility_id").toString();
            Facility originFacility = this.findByFacilityId(facilityId);
            Facility facility = originFacility == null ? new Facility() : originFacility;
            Station station = stationRepository.findByStationName(StrUtils.getStr(d.get("station_name")));
            Long codeSeq = facilityRepository.findCommonCode(StrUtils.getStr(d.get("facility_kind")));

            facility.setLatitude(Double.parseDouble(StrUtils.getStr(d.get("latitude"))));
            facility.setLongitude(Double.parseDouble(StrUtils.getStr(d.get("longitude"))));
            facility.setFacilityId(facilityId);
            facility.setStationSeq(station.getStationSeq());
            facility.setFacilityStatus(0);
            facility.setFacilityKind(Integer.parseInt(codeSeq.toString()));

            facilityList.add(facility);
        });

        facilityRepository.saveAll(facilityList);
    }

    public void saveAll(List<Map<String, Object>> list, String facilityKind) {
        List<Facility> facilityList = new ArrayList<Facility>();
        list.stream().forEach((d) -> {
            String facilityId = d.get("facility_id").toString();
            Facility originFacility = this.findByFacilityId(facilityId);
            Facility facility = originFacility == null ? new Facility() : originFacility;
            Station station = stationRepository.findByStationName(StrUtils.getStr(d.get("station_name")));
            Long codeSeq = facilityRepository.findCommonCode(facilityKind);

            String latitude = StrUtils.getStr(d.get("latitude"));
            String longitude = StrUtils.getStr(d.get("longitude"));
            String facilityName = StrUtils.getStr(d.get("facility_name"));


            latitude = latitude.isEmpty() ? "0" : latitude;
            longitude = longitude.isEmpty() ? "0" : longitude;

            facility.setLatitude(Double.parseDouble(latitude));
            facility.setLongitude(Double.parseDouble(longitude));
            facility.setFacilityId(facilityId);
            facility.setFacilityStatus(0);
            facility.setFacilityKind(Integer.parseInt(codeSeq.toString()));
            facility.setFacilityName(facilityName);

            if (station != null) {
                facility.setStationSeq(station.getStationSeq());
            }

            facilityList.add(facility);
        });

        facilityRepository.saveAll(facilityList);
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
}
