package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.Station;
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

    public void saveAll(List<Map<String, Object>> list) {
        List<Station> stationList = new ArrayList<Station>();
        list.stream().forEach((d) -> {
            String stationName = d.get("station_name").toString();
            Station originStation = findByStationName(stationName);
            Station station = originStation == null ? new Station() : originStation;
            Long codeId = stationRepository.findCommonCode("lamp_road");

            station.setLatitude(Double.parseDouble(d.get("latitude").toString()));
            station.setLongitude(Double.parseDouble(d.get("longitude").toString()));
            station.setStationName(d.get("station_name").toString());
            station.setStationKind(Integer.parseInt(codeId.toString()));
            station.setAdministZone("테스트11");
            station.setAddress("테스트");

            stationList.add(station);
        });

        stationRepository.saveAll(stationList);
    }

}
