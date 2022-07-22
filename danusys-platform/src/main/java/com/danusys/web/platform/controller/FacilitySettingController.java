package com.danusys.web.platform.controller;

import com.danusys.web.commons.api.dto.SettingDTO;
import com.danusys.web.commons.api.service.ApiCallService;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.api.service.FacilitySettingService;
import com.danusys.web.platform.service.facility.FacilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value="/facilitySetting")
public class FacilitySettingController {

    private final FacilitySettingService facilitySettingService;

    @PostMapping("/{facilitySeq}")
    public List<SettingDTO> getAllList(@PathVariable Long facilitySeq) throws Exception {
        return  facilitySettingService.findAll(facilitySeq);
    }

    @PostMapping("/weekday/{facilitySeq}")
    public List<List<SettingDTO>> getWeekdayList(@PathVariable Long facilitySeq) throws Exception {
        return facilitySettingService.findWeekdayList(facilitySeq);
    }

    @PostMapping("/weekend/{facilitySeq}")
    public List<List<SettingDTO>> getWeekendList(@PathVariable Long facilitySeq) throws Exception {
        return facilitySettingService.findWeekendList(facilitySeq);
    }

    @PutMapping
    public String saveList() throws Exception {
        return "gdgd";
    }

    @DeleteMapping
    public String delete() throws Exception {
        return "gdgd";
    }
}
