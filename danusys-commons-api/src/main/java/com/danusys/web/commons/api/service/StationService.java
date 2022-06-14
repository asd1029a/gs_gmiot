package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.Station;
import com.danusys.web.commons.api.repository.StationRepository;
import com.danusys.web.commons.app.StrUtils;
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
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public Station findByStationName(String stationName) {
        return stationRepository.findByStationName(stationName);
    }

    public Station findByStationSeq(Long stationSeq) {
        return stationRepository.findByStationSeq(stationSeq);
    }

    public void saveAll(List<Map<String, Object>> list) {
        List<Station> stationList = new ArrayList<Station>();
        list.stream().forEach((d) -> {
            String stationName = d.get("station_name").toString();
            Station originStation = findByStationName(stationName);
            Station station = originStation == null ? new Station() : originStation;
            Long codeId = stationRepository.findCommonCode("lamp_road");

            String latStr = StrUtils.getStr(d.get("latitude"));
            String lngStr = StrUtils.getStr(d.get("longitude"));

            latStr = latStr.isEmpty() ? "0" : latStr;
            lngStr = lngStr.isEmpty() ? "0" : lngStr;

            double latitude = Double.parseDouble(latStr);
            double longitude = Double.parseDouble(lngStr);

            station.setLongitude(longitude);
            station.setLatitude(latitude);
            station.setStationName(d.get("station_name").toString());
            station.setStationKind(codeId);
            station.setAdministZone(stationRepository.getEmdCode(longitude, latitude));
            station.setAddress(StrUtils.getStr(d.get("address")));

            stationList.add(station);
        });

        stationRepository.saveAll(stationList);
    }

    public void saveAll(List<Map<String, Object>> list, String stationKind) {
        List<Station> stationList = new ArrayList<Station>();
        list.stream().forEach((d) -> {
            String stationName = d.get("station_name").toString();
            Station originStation = findByStationName(stationName);
            Station station = originStation == null ? new Station() : originStation;
            Long codeId = stationRepository.findCommonCode(stationKind);

            String latStr = StrUtils.getStr(d.get("latitude"));
            String lngStr = StrUtils.getStr(d.get("longitude"));

            latStr = latStr.isEmpty() ? "0" : latStr;
            lngStr = lngStr.isEmpty() ? "0" : lngStr;

            double latitude = Double.parseDouble(latStr);
            double longitude = Double.parseDouble(lngStr);

            station.setLongitude(longitude);
            station.setLatitude(latitude);
            station.setStationName(d.get("station_name").toString());
            station.setStationKind(codeId);
            station.setAdministZone(stationRepository.getEmdCode(longitude, latitude));
            station.setAddress(StrUtils.getStr(d.get("address")));

            stationList.add(station);
        });

        stationRepository.saveAll(stationList);
    }


}
