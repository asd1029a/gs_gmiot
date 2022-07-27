package com.danusys.web.platform.controller;

import com.danusys.web.commons.api.dto.SettingDTO;
import com.danusys.web.commons.api.scheduler.service.YjMqttFacility;
import com.danusys.web.commons.api.service.FacilitySettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value="/facilitySetting")
public class FacilitySettingController {

    private final FacilitySettingService facilitySettingService;
    private final YjMqttFacility yjMqttFacility;

    @PostMapping("/{facilitySeq}")
    public ResponseEntity<List<SettingDTO>> getAllList(@PathVariable Long facilitySeq) throws Exception {
        return  ResponseEntity.ok().body(facilitySettingService.findAllByFacilitySeq(facilitySeq));
    }

    @PostMapping("/weekday/{facilitySeq}")
    public ResponseEntity<List<List<SettingDTO>>> getWeekdayList(@PathVariable Long facilitySeq) throws Exception {
        return ResponseEntity.ok().body(facilitySettingService.findWeekdayList(facilitySeq));
    }

    @PostMapping("/weekend/{facilitySeq}")
    public ResponseEntity<List<List<SettingDTO>>> getWeekendList(@PathVariable Long facilitySeq) throws Exception {
        return ResponseEntity.ok().body(facilitySettingService.findWeekendList(facilitySeq));
    }

    @PostMapping
    public ResponseEntity<?> saveList(@RequestBody List<SettingDTO> settingDTOList) throws Exception {
        facilitySettingService.save(settingDTOList);
        yjMqttFacility.setScheduler();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{facilitySeq}")
    public ResponseEntity<?> delete(@PathVariable Long facilitySeq) throws Exception {
        facilitySettingService.deleteAllByFacilitySeq(facilitySeq);
        return ResponseEntity.noContent().build();
    }
}
