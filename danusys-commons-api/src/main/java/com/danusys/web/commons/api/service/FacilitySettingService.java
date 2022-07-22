package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.dto.SettingDTO;
import com.danusys.web.commons.api.model.FacilitySetting;
import com.danusys.web.commons.api.repository.FacilityRepository;
import com.danusys.web.commons.api.repository.FacilitySettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacilitySettingService {

    private final FacilitySettingRepository facilitySettingRepository;
//    private static Map<SettingDTO, Integer> map = new HashMap<>();

    public void save(List<SettingDTO> settingDTOList) {
        List<FacilitySetting> facilitySettingList = new ArrayList<>();
        settingDTOList.stream().forEach(f ->{
            FacilitySetting facilitySetting = new FacilitySetting(f);
            facilitySettingList.add(facilitySetting);
        });
        facilitySettingRepository.saveAll(facilitySettingList);
    }

    public List<SettingDTO> findAll(Long facilitySeq){
        List<SettingDTO> settingDTOList = new ArrayList<>();
        List<FacilitySetting> facilitySettingList = facilitySettingRepository.findAllByFacilitySeqOrderByFacilitySettingTime(facilitySeq);
        facilitySettingList.stream().forEach(f -> {
            SettingDTO settingDTO = new SettingDTO(f);
            settingDTOList.add(settingDTO);
        });
        return settingDTOList;
    }

    public List<List<SettingDTO>> findWeekdayList(Long facilitySeq){
        List<SettingDTO> settingDTOList = new ArrayList<>();
        List<FacilitySetting> facilitySettingList = facilitySettingRepository.findAllByFacilitySeqOrderByFacilitySettingTime(facilitySeq);

        facilitySettingList.stream().filter(f -> f.getFacilitySettingDay().equals("weekday")).forEach(f -> {
            SettingDTO settingDTO = new SettingDTO(f);
            settingDTOList.add(settingDTO);
        });

        Map<String, Integer> map = new HashMap<>();
        for (SettingDTO temp : settingDTOList) {
            Integer count = map.get(temp.getFacilitySettingTime());
            map.put(temp.getFacilitySettingTime(), (count == null) ? 1 : count + 1);
        }
        List<List<SettingDTO>> settingWeekdayList = printMap(map);
        log.info("settingWeekdayList  : {} ",settingWeekdayList);
        return settingWeekdayList;
    }

    public List<List<SettingDTO>> findWeekendList(Long facilitySeq){
        List<SettingDTO> settingDTOList = new ArrayList<>();
        List<FacilitySetting> facilitySettingList = facilitySettingRepository.findAllByFacilitySeqOrderByFacilitySettingTime(facilitySeq);

        facilitySettingList.stream().filter(f -> f.getFacilitySettingDay().equals("weekend")).forEach(f -> {
            SettingDTO settingDTO = new SettingDTO(f);
            settingDTOList.add(settingDTO);
        });

        Map<String, Integer> map = new HashMap<>();
        for (SettingDTO temp : settingDTOList) {
            Integer count = map.get(temp.getFacilitySettingTime());
            map.put(temp.getFacilitySettingTime(), (count == null) ? 1 : count + 1);
        }
        List<List<SettingDTO>> settingWeekdayList = printMap(map);
        log.info("settingWeekdayList  : {} ",settingWeekdayList);
        return settingWeekdayList;
    }

    private List<List<SettingDTO>> printMap(Map<String, Integer> map){
        List<List<SettingDTO>> settingDTOLists = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            log.info("Element : {} Count : {}",entry.getKey(),entry.getValue());
            settingDTOLists.add(makeArray(entry.getKey(), entry.getValue()));
        }
        return settingDTOLists;
    }

    private List<SettingDTO> makeArray(String settingDTO, Integer value){
        List<SettingDTO> settingDTOList = new ArrayList<>();
        List<FacilitySetting> facilitySettingList = facilitySettingRepository.findAllByFacilitySettingTime(settingDTO);
        facilitySettingList.stream().forEach(f -> {
            SettingDTO settingThis = new SettingDTO(f);
            settingDTOList.add(settingThis);
        });
//        for (int i = 0; i < value; i++) {
//            settingDTOList.add(settingDTO);
//        }
        return settingDTOList;
    }
}
