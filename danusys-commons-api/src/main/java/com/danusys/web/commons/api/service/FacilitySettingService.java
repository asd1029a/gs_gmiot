package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.dto.SettingDTO;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilitySetting;
import com.danusys.web.commons.api.repository.FacilityRepository;
import com.danusys.web.commons.api.repository.FacilitySettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacilitySettingService {

    private final FacilitySettingRepository facilitySettingRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityService facilityService;

    @Transactional
    public void save(List<SettingDTO> settingDTOList) {
        List<FacilitySetting> facilitySettingList = new ArrayList<>();
        settingDTOList.stream().forEach(settingDTO ->{
            FacilitySetting facilitySetting = new FacilitySetting(settingDTO);
            Facility findFacility = facilityRepository.findByFacilitySeq(facilitySetting.getFacilitySeq());
            String facilityId = findFacility.getFacilityName();
            facilitySetting.setAdministZone(findFacility.getAdministZone());
            if(facilityId.equals("IDU_기동정지")) {
                if(facilitySetting.getFacilitySettingName().equals("power")) {
                    facilitySetting.setFacilityId(findFacility.getFacilityId());
                } else if (facilitySetting.getFacilitySettingName().equals("mode")) {
                    facilitySetting.setFacilityId(facilitySettingRepository.findFacilityId(facilitySetting.getFacilitySeq(),"운전모드"));
                } else if (facilitySetting.getFacilitySettingName().equals("fan")) {
                    facilitySetting.setFacilityId(facilitySettingRepository.findFacilityId(facilitySetting.getFacilitySeq(),"바람세기"));
                } else if (facilitySetting.getFacilitySettingName().equals("temp")) {
                    facilitySetting.setFacilityId(facilitySettingRepository.findFacilityId(facilitySetting.getFacilitySeq(),"설정온도"));
                }
            } else {
                facilitySetting.setFacilityId(findFacility.getFacilityId());
            }
            facilitySettingList.add(facilitySetting);
        });
        facilitySettingRepository.deleteAllByFacilitySeq(facilitySettingList.get(0).getFacilitySeq());
        facilitySettingRepository.saveAll(facilitySettingList);
    }

    public List<SettingDTO> findAllByFacilitySeq(Long facilitySeq){
        List<SettingDTO> settingDTOList = new ArrayList<>();
        List<FacilitySetting> facilitySettingList = facilitySettingRepository.findAllByFacilitySeqOrderByFacilitySettingTime(facilitySeq);
        facilitySettingList.stream().forEach(f -> {
            SettingDTO settingDTO = new SettingDTO(f);
            settingDTOList.add(settingDTO);
        });
        return settingDTOList;
    }

    @Transactional
    public void deleteAllByFacilitySeq(Long facilitySeq){
        facilitySettingRepository.deleteAllByFacilitySeq(facilitySeq);
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
        List<List<SettingDTO>> settingWeekdayList = printMap(map,facilitySeq);
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
        List<List<SettingDTO>> settingWeekdayList = printMap(map,facilitySeq);
        log.info("settingWeekdayList  : {} ",settingWeekdayList);
        return settingWeekdayList;
    }

    private List<List<SettingDTO>> printMap(Map<String, Integer> map,Long facilitySeq){
        List<List<SettingDTO>> settingDTOLists = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            log.info("Element : {} Count : {}",entry.getKey(),entry.getValue());
            settingDTOLists.add(makeArray(entry.getKey(), facilitySeq));
        }
        return settingDTOLists;
    }

    private List<SettingDTO> makeArray(String settingDTO, Long facilitySeq){
        List<SettingDTO> settingDTOList = new ArrayList<>();
        List<FacilitySetting> facilitySettingList = facilitySettingRepository.findAllByFacilitySeqAndFacilitySettingTimeOrderByFacilitySettingTime(facilitySeq,settingDTO);
        facilitySettingList.stream().forEach(f -> {
            SettingDTO settingThis = new SettingDTO(f);
            settingDTOList.add(settingThis);
        });
//        for (int i = 0; i < value; i++) {
//            settingDTOList.add(settingDTO);
//        }
        return settingDTOList;
    }

    public List<Map<String,Object>> findBySetScheduler() {
        return facilitySettingRepository.findBySetScheduler();
    }

    public List<FacilitySetting> findAll() {return facilitySettingRepository.findAll();}
}
