package com.danusys.web.commons.api.dto;


import com.danusys.web.commons.api.model.FacilitySetting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingDTO {

    private Long facilitySeq;
    private String facilitySettingName;
    private String facilitySettingValue;
    private String facilitySettingTime;
    private Integer facilitySettingType;
    private String facilitySettingDay;

    public SettingDTO(FacilitySetting facilitySetting){
        this.facilitySeq = facilitySetting.getFacilitySeq();
        this.facilitySettingName = facilitySetting.getFacilitySettingName();
        this.facilitySettingValue = facilitySetting.getFacilitySettingValue();
        this.facilitySettingTime = facilitySetting.getFacilitySettingTime();
        this.facilitySettingType = facilitySetting.getFacilitySettingType();
        this.facilitySettingDay = facilitySetting.getFacilitySettingDay();
    }
}
