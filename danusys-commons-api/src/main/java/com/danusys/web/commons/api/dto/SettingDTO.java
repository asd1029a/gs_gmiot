package com.danusys.web.commons.api.dto;


import com.danusys.web.commons.api.model.FacilitySetting;
import lombok.Builder;
import lombok.Data;

@Data
public class SettingDTO {

    private Long facilitySeq;
    private String facilitySettingName;
    private String facilitySettingValue;
    private String facilitySettingTime;
    private Integer facilitySettingType;
    private String facilitySettingDay;

    @Builder
    public SettingDTO(FacilitySetting facilitySetting){
        this.facilitySeq = facilitySetting.getFacilitySeq();
        this.facilitySettingName = facilitySetting.getFacilitySettingName();
        this.facilitySettingValue = facilitySetting.getFacilitySettingValue();
        this.facilitySettingTime = facilitySetting.getFacilitySettingTime();
        this.facilitySettingType = facilitySetting.getFacilitySettingType();
        this.facilitySettingDay = facilitySetting.getFacilitySettingDay();
    }
}
